import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;

public class Main extends Application {
    private static ArrayList<Item> items;
    private static ArrayList<Item> receipt;
    private static String databaseFilename, receiptFilename;
    private static Label labelItem,labelPrice;
    private static TextField textInput;
    private static HBox top,bot;
    private static Scene scene;
    private static BorderPane paneSale;
    private static boolean run;
    private static BigDecimal shoppingTotal;
    private static int maxLength;
    private static double mainX,mainY;

    @Override
    public void start(Stage window) throws Exception {
        //setup
        top = new HBox();
        bot = new HBox();
        paneSale = new BorderPane();
        labelItem = new Label("");
        labelPrice = new Label("");
        textInput = new TextField();
        top.setSpacing(10);
        paneSale.setTop(top);
        paneSale.setBottom(bot);
        //input and display added to one window for the mockup; top row is the display, bottom is input
        top.getChildren().addAll(labelItem, labelPrice);
        bot.getChildren().add(textInput);
        labelItem.setPrefWidth(145);
        labelItem.setPrefHeight(50);
        labelPrice.setPrefHeight(50);
        labelPrice.setPrefWidth(50);
        textInput.setPrefWidth(235);
        textInput.setPrefHeight(50);
        scene = new Scene(paneSale);
        window.setTitle("Point of Sale");
        window.setScene(scene);
        window.show();
        mainX=window.getX();
        mainY=window.getY();
        //events will be handled by the scene
        scene.setOnKeyPressed(e -> {
            if (e.getCode()==KeyCode.ENTER){processInput();return;}
            if (e.getCode()==KeyCode.ESCAPE){
                if(run) {
                    run = false;
                    quit(receipt, shoppingTotal);
                }
            }
            });
    }
    public static void main(String[] args){
        initData();
        launch(args);
    }
    private static void processInput() {
        String tmpStr = textInput.getText();
        textInput.setText("");
        if (tmpStr.equals("exit")){
            quit(receipt, shoppingTotal);
            return;
        } else {
            //try to get the bin code of an item (imitating the scanner), if it's unable to do so, then the bar-code is invalid
            try {
                int val = Integer.parseInt(tmpStr);
                String bin = Integer.toBinaryString(val);
                //unify to string of length=4 bc that's how many my test bar-codes have bits; in real world, EAN-13 would be used
                while (bin.length() < maxLength) {
                    bin = "0" + bin;
                }
                addItem(bin);
                return;
            } catch (Exception ex) {
                labelItem.setText("Invalid bar-code");
                return;
            }
        }
    }
    private static void quit(ArrayList<Item> argArr,BigDecimal argSum) {
        //if you didn't buy anything but still want to quit using this method then don't generate a receipt
        if(receipt.size()!=0){
            printReceiptToFile(argArr);
            //popup receipt visualisation
            dispReceipt.display(argArr,argSum,mainX,mainY);
            //LCD visualisation
            labelItem.setText("Total: ");
            labelPrice.setText(argSum+" PLN");
            return;
        }else{
            System.exit(1234);
        }

    }
    public static void addItem(String arg){
        //if bin code entered is too long  (>4 in this case) then
        if (arg.length()>maxLength){resetLabels();labelItem.setText("Product not found!");return;}
        //find the item and add it to the receipt
        for (int i=0;i<items.size();i++){
            if (items.get(i).getCode().equals(arg)){
                Item tmp = new Item(items.get(i).getName(),items.get(i).getCode(),items.get(i).getPrice());
                shoppingTotal = shoppingTotal.add(BigDecimal.valueOf(tmp.getPrice()));
                receipt.add(tmp);
                labelItem.setText(tmp.getName());
                labelPrice.setText(String.valueOf(tmp.getPrice()));
                return;
            }
        }
        labelItem.setText("Code not found");
        resetLabels();
    }
    private static void initData() {
        //initialization of arrays and vars
        databaseFilename = "itemsData.txt";
        receiptFilename ="receipt.txt";
        items = new ArrayList<Item>();
        receipt = new ArrayList<Item>();
        run=true;
        shoppingTotal = new BigDecimal(0);
        maxLength=4;//max code length in this case
        mainX=0;
        mainY=0;
        //for quick itemsData file generation: anything above 16 will not be in databe thus code won't be found
        //writing data to file if data file is not present
        if (checkForData(databaseFilename)){loadData();}else {
            //mockup of database
            Item item1 = new Item("Item1", "0000", 5.63);
            Item item2 = new Item("Item2", "0001", 1.25);
            Item item3 = new Item("Item3", "0010", 0.01);
            Item item4 = new Item("item4", "0011", 0.32);
            Item item5 = new Item("item5", "0100", 0.235);
            Item item6 = new Item("item6", "0101", 0.0246);
            Item item7 = new Item("item7", "0110", 0.012354);
            Item item8 = new Item("item8", "0111", 0.01653);
            Item item9 = new Item("item9", "1000", 0.01364);
            Item item10 = new Item("item10", "1001", 0.01346);
            Item item11 = new Item("item11", "1010", 0.01346);
            Item item12 = new Item("item12", "1011", 0.01634);
            Item item13 = new Item("item13", "1100", 0.05671);
            Item item14 = new Item("item14", "1101", 0.05671);
            Item item15 = new Item("item15", "1110", 0.01584);
            Item item16 = new Item("item16", "1111", 0.01356);
            items.add(item1);
            items.add(item2);
            items.add(item3);
            items.add(item4);
            items.add(item5);
            items.add(item6);
            items.add(item7);
            items.add(item8);
            items.add(item9);
            items.add(item10);
            items.add(item11);
            items.add(item12);
            items.add(item13);
            items.add(item14);
            items.add(item15);
            items.add(item16);
            writeData(items);
        }
    }
    private static void resetLabels(){
        labelPrice.setText("");
        labelItem.setText("");
        textInput.setText("");
    }
    private static boolean checkForData(String argName) {
        File filePath = new File(argName);
        String path = filePath.getAbsolutePath();
        if (filePath.exists()){return true;}else{return false;}
    }
    private static void printReceiptToFile(ArrayList<Item> argArrayList) {
        //prints receipt to file, don't know how the data would be passed in a real register
        try {
            File file = new File(receiptFilename);
            Writer output = new BufferedWriter(new FileWriter(file));
            for (int i=0;i<argArrayList.size();i++){
                output.write(argArrayList.get(i).toPrint());
            }
            output.close();
        }
        catch (IOException e){e.toString();System.exit(1);}
    }
    private static void writeData(ArrayList<Item> argArrayList) {
        //if data is not present on the machine
        try {
            FileOutputStream fos = new FileOutputStream(databaseFilename);
            try {
                ObjectOutputStream output = new ObjectOutputStream(fos);
                output.writeObject(argArrayList);
                output.close();
            } catch (Exception ex) {
                System.out.println(ex.toString());
                System.exit(1);
            }
            fos.close();
        }
        catch (IOException e){e.toString();System.exit(1);}
    }
    public static void loadData(){
        //load database of items
        try {
            FileInputStream fis = new FileInputStream(databaseFilename);
            try {
                ObjectInputStream input = new ObjectInputStream(fis);
                items = (ArrayList<Item>) input.readObject();
                input.close();
            } catch (Exception e) {
                System.out.println(e.toString());
                System.exit(1);
            }
            fis.close();
        }
        catch (IOException e){e.toString();System.exit(1);}
    }
}

