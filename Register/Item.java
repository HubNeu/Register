import java.io.Serializable;
import static java.lang.Math.ceil;

//simple class to store data in an organized way

public class Item implements Serializable {
    private String code;
    private String name;
    private double price;
    private static final long serialVersionUID = 1177396887574904760L;

    public Item(String argName,String argCode, double argPrice) {
        //should insert some code to prevent incorrect values
        code=argCode;
        name=argName;
        price=argPrice;
        fixPrice(); //simple way of fixing the price if it's incorrect, but then again it shouldn't be typed in incorrectly
    }
    public Item() {
        code="0000";
        name="not named";
        price=0;
    }
    public String getName(){
        return name;
    }
    public void setName(String arg){
        name=arg;
    }
    public String getCode(){
        return code;
    }
    public void setCode(String arg){
        code=arg;
    }
    public double getPrice(){
        return price;
    }
    public void setPrice(double arg){
        price=arg;
        fixPrice();
    }
    //aids in
    @Override
    public String toString(){
        String toStringString = this.getName()+"\n"+this.getCode()+"\n"+this.getPrice();
        return toStringString;
    }
    public String toPrint(){
        String toPrintString = this.getName()+" "+this.getPrice()+"PLN\n";
        return toPrintString;
    }
    public void fixPrice(){
        //to fix the price for this demo if typed incorrectly
        double tmp=price;
        tmp=ceil(tmp*100);
        price=tmp/100;
    }
}