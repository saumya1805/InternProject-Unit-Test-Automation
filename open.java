//This class is used to run the UI for the Unit Test Template Generator
import java.awt.*;
import java.io.IOException;

public class open {

    //form object holds the UI created
    public static UI form;
    public static void main(String[] args) throws IOException, FontFormatException {
        form=new UI();
        //Make the UI visible
        form.setVisible(true);
    }
}
