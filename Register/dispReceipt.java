import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.*;

import java.math.BigDecimal;
import java.util.ArrayList;

public class dispReceipt {
    private static Label tmp;
    public static void display(ArrayList<Item> argArrL, BigDecimal sum,double argX,double argY){

        //set up the window

        Stage window = new Stage();
        //offset the window so it doesn't get in the way
        window.setX(argX+250);
        window.setY(argY);
        window.initModality(Modality.APPLICATION_MODAL);
        window.setOnCloseRequest(e->System.exit(1234));
        //make the layout and it's components
        VBox layout = new VBox(5);
        tmp = new Label("Receipt (copy saved to file)");
        layout.getChildren().add(tmp);

        //for every item in the receipt arraylist add a child to layout

        for (int i=0;i<argArrL.size();i++){
            tmp = new Label(argArrL.get(i).getName()+"  "+argArrL.get(i).getPrice()+"PLN");
            layout.getChildren().add(tmp);
        }
        tmp = new Label("Total: "+sum+"PLN");
        layout.getChildren().add(tmp);
        tmp = new Label("Press any key to end.");
        layout.getChildren().add(tmp);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);
        scene.setOnKeyPressed(e->System.exit(1234));
        window.setScene(scene);
        window.show();
    }
}
