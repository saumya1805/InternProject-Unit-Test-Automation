import javax.swing.*;
import javax.swing.plaf.metal.MetalComboBoxUI;
import java.awt.*;
import java.io.IOException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import static javafx.scene.paint.Color.color;

public class UI extends JFrame{
    private JPanel mainPanel;
    private JTextField filePath;
    private JButton chooseFileButton;
    private JComboBox objName;
    private JButton addToTextAreaButton;
    private JComboBox funcName;
    private JButton addToTextAreaButton3;
    private JTextField custFuncName;
    private JComboBox publicFuncName;
    private JComboBox objName1;
    private JComboBox funcName1;
    private JTextArea customizationSpace;
    private JButton AddToUnitTestButton;
    private JButton cancelButton;
    private JButton generateTestButton;
    private JComboBox assertsList;
    private JButton addToTextAreaButton2;
    private JLabel info1;
    private JLabel info2;
    private JLabel info3;

    public Set<String> box2=new HashSet<>();

    public static boolean check = false;

    String s1;

    String s2;

    public static int numKeys;

    public static int beforeFlag = 0;

    FileWriter output;

    //Name of the class being tested
    public static String nameOfClassBeingTested;

    //Name of current function being parsed. Helps to associate the lines being read in the function body with the function
    public static String currFunction;

    //The absolute filepath taken as input from the user via the Java Swing GUI
    public static String inputFilePath;

    //Stores the external objects being referenced in a class which would be mocked
    public static Vector<String> externalObjectList = new Vector<>();

    //Stores the functions to be used in @Test
    public static Vector<String> functionsToBeTested = new Vector<>();

    public static Vector<String> autowiredObjectList = new Vector<>();

    //Hashmap storing information about the public functions being declared in a class, what external objects these functions use and what functions fo these external objects call
    public static HashMap<String, HashMap<String, List<String>>> functionData = new LinkedHashMap();

    //Hashmap to store what dummy value will be returned for the mocked functions

    public static HashMap<String, Vector<String>> whenReturnThisFunctions = new LinkedHashMap();

    public static Vector<String> beforeData = new Vector<>();

    public static String t1;

    public static String t2;

    public static String t3;

    public static String t4;

    //Flag for registering that previous line had an @Autowired object
    public static int autowiredFlag = 0;

    public int ActionFlag=0;

    public int ActionFlag2=0;

    public int ActionFlag3=0;

    public int addFlag=0;

    public int testedBefore=0;

    DefaultTableModel model;

    UI() throws IOException{

        add(mainPanel);
        setSize(1200,1500);
        Color color=new Color(111,151,158);
        Color color2=new Color(246,246,246);

        objName.setUI(new MetalComboBoxUI());
        publicFuncName.setUI(new MetalComboBoxUI());
        objName1.setUI(new MetalComboBoxUI());
        funcName.setUI(new MetalComboBoxUI());
        funcName1.setUI(new MetalComboBoxUI());
        assertsList.setUI(new MetalComboBoxUI());

        objName.setEditable(true);
        objName.getEditor().getEditorComponent().setBackground(color);
        objName.getEditor().getEditorComponent().setForeground(color2);
        Font f=new Font("Menlo",0,16);
        objName.getEditor().getEditorComponent().setFont(f);

        info1.setToolTipText("<html>Choose the object name and function name<br>and click the <b>Add to Text Area</b> button following<br>which you can add the parameters and return<br>value. Do this for each setup statement<br>you wish to add and once done click the<br><b>Add to unit test</b> button beside<br>the text area.</html>");
        info2.setToolTipText("<html>Choose the function for which you<br>wish to create a test function<br>and fill in a unique function name.<br>Choose the object and external function you<br>require and click <b>Add to text area</b> for<br>each. Customize in the text area<br> and once done click the <b>Add to unit test</b><br>button</html>");
        info3.setToolTipText("<html>Select the assert function you wish to insert<br>and click the <b>Add to Text Area</b><br>button. Add the parameters in the text area<br> and once done click the <b>Add to unit test</b><br>button</html>");

        funcName.setEditable(true);
        funcName.getEditor().getEditorComponent().setBackground(color);
        funcName.getEditor().getEditorComponent().setForeground(color2);
        funcName.getEditor().getEditorComponent().setFont(f);

        publicFuncName.setEditable(true);
        publicFuncName.getEditor().getEditorComponent().setBackground(color);
        publicFuncName.getEditor().getEditorComponent().setForeground(color2);
        publicFuncName.setBorder(null);
        publicFuncName.getEditor().getEditorComponent().setFont(f);

        objName1.setEditable(true);
        objName1.getEditor().getEditorComponent().setBackground(color);
        objName1.getEditor().getEditorComponent().setForeground(color2);
        objName1.getEditor().getEditorComponent().setFont(f);

        funcName1.setEditable(true);
        funcName1.getEditor().getEditorComponent().setBackground(color);
        funcName1.getEditor().getEditorComponent().setForeground(color2);
        funcName1.getEditor().getEditorComponent().setFont(f);

        assertsList.setEditable(true);
        assertsList.getEditor().getEditorComponent().setBackground(color);
        assertsList.getEditor().getEditorComponent().setForeground(color2);
        assertsList.getEditor().getEditorComponent().setFont(f);

        chooseFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inputFilePath=filePath.getText();
                try {
                    readUsingFileReader(inputFilePath);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

                for(Map.Entry<String,HashMap<String,List<String>>> entry : functionData.entrySet()){
                    publicFuncName.addItem(entry.getKey());
                }
            }
        });
        addToTextAreaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(addFlag==0){
                    customizationSpace.setText("@Before\n");
                    customizationSpace.append("public void beforeTest(){\n\n");
                    customizationSpace.append("MockitoAnnotations.initMocks(this);\n");
                    addFlag=1;
                }
                String objectName= (String) objName.getSelectedItem();
                String functionName=(String) funcName.getSelectedItem();
                customizationSpace.append("when("+functionName+").thenReturn();\n");
            }
        });
        addToTextAreaButton3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String publicFunctionName= (String) publicFuncName.getSelectedItem();
                String functionName=custFuncName.getText();
                try {
                    if (Files.lines(Paths.get("TestCodeTester.java")).filter(line1 -> line1.contains(functionName)).count() != 0){
                        JOptionPane.showMessageDialog(mainPanel,"A test function with the function name provided is already present. Choose another function name");
                    }
                    else{
                        if(addFlag==0){
                            customizationSpace.setText("@Test\n");
                            customizationSpace.append("public void "+functionName+"{\n");
                            addFlag=1;
                        }
                        String objectName=(String) objName1.getSelectedItem();
                        String functionName1=(String) funcName1.getSelectedItem();
                        customizationSpace.append("when("+functionName1+").thenReturn();\n");
                    }
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        addToTextAreaButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String asserts= (String) assertsList.getSelectedItem();
                customizationSpace.append(asserts+"\n");
            }
        });
        AddToUnitTestButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addFlag=0;
                try {
                    output.write(customizationSpace.getText());
                    output.write("\n");
                    output.close();
                    output=new FileWriter("TestCodeTester.java",true);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                customizationSpace.setText("Customization Space...");
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        generateTestButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    output.write("}\n");
                    output.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        objName.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                funcName.removeAllItems();
                if(ActionFlag==0){
                    ActionFlag=1;
                }
                else{
                    String compare= (String) objName.getSelectedItem();
                    for(Map.Entry<String,HashMap<String,List<String>>> entry:functionData.entrySet()){
                        for(Map.Entry<String,List<String>> entry2:entry.getValue().entrySet()){
                            if(entry2.getKey()==compare){
                                for(int i=0;i<entry2.getValue().size();i++){
                                    box2.add(entry2.getValue().get(i));
                                }
                            }
                        }
                    }
                    for(String temp:box2){
                        funcName.addItem(temp);
                    }
                }
            }
        });
        publicFuncName.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                objName1.removeAllItems();
                if(ActionFlag2==0){
                    ActionFlag2=1;
                }
                else{
                    String compare=(String) publicFuncName.getSelectedItem();
                    for(Map.Entry<String,HashMap<String,List<String>>> entry:functionData.entrySet()){
                        if(entry.getKey()==compare){
                            for(Map.Entry<String,List<String>> entry2:entry.getValue().entrySet()){
                                objName1.addItem(entry2.getKey());
                            }
                            break;
                        }
                    }
                }
            }
        });
        objName1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                funcName1.removeAllItems();
                if(ActionFlag3==0){
                    ActionFlag3=1;
                }
                else{
                    String compare1=(String)publicFuncName.getSelectedItem();
                    String compare2=(String)objName1.getSelectedItem();
                    for(Map.Entry<String,HashMap<String,List<String>>> entry:functionData.entrySet()){
                        if(entry.getKey()==compare1){
                            for(Map.Entry<String,List<String>> entry2:entry.getValue().entrySet()){
                                if(entry2.getKey()==compare2){
                                    for(int i=0;i<entry2.getValue().size();i++){
                                        funcName1.addItem(entry2.getValue().get(i));
                                    }
                                    break;
                                }
                            }
                            break;
                        }
                    }
                }
            }
        });
    }

    //Function that parses the code to be tested

    public void readUsingFileReader(String filePath) throws IOException {

        File file = new File(filePath);
        FileReader fr = null;
        try {
            fr = new FileReader(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        //Reads the file line by line
        BufferedReader br = new BufferedReader(fr);
        //Stores the current line of code that was read
        String line;

        //Creates file that will store the unit test code corresponding to the code to be tested
        File file1 = new File("TestCodeTester.java");

        //To write into the unit test code file

        try {
            // create a new file with name specified
            // by the file object
            boolean value = file1.createNewFile();

            if (value) {
                System.out.println("New Java File is created.");
                output = new FileWriter("TestCodeTester.java", true);
            } else {
                System.out.println("The file already exists.");
                output = new FileWriter("TestCodeTester.java", true);
                testedBefore = 1;
                JOptionPane.showMessageDialog(mainPanel,"A unit test for this code already exists. Proceed with Test generation.");
            }

        } catch (Exception e) {
            e.getStackTrace();
        }

        while (true) {
            try {
                if (!((line = br.readLine()) != null)) break;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            //This is where the actual parsing happens
            //Every line of code will be parsed to identify the tokens present in it

            //Line read was an import statement
            //Copy the import to the unit test file

            if (testedBefore == 0 && line.contains("import")) {

                output.write(line);
                output.write("\n");
            }

            //Detects the class which is being tested
            else if (testedBefore == 0 && line.contains("public class")) {
                //System.out.println("Hi in pc");
                //Importing the additional dependencies here (as to be imported only once)
                output.write("import static org.junit.Assert.assertEquals;\n");
                output.write("import static org.junit.Assert.assertFalse;\n");
                output.write("import static org.junit.Assert.assertNotNull;\n");
                output.write("import static org.junit.Assert.assertTrue;\n");
                output.write("import org.junit.Before;\n");
                output.write("import org.junit.Rule;\n");
                output.write("import org.junit.Test;\n");
                output.write("import org.junit.mockito2.*;\n");
                output.write("import static org.mockito2.Mockito.mock;\n");
                output.write("import static org.mockito2.Mockito.spy;\n");
                output.write("import static org.mockito2.Mockito.when;\n");
                output.write("import static org.mockito2.Mockito.doNothing;\n");
                output.write("\n");

                //Declares the public class which will hold the mocks and tests in the unit test code file
                output.write("public class TestCodeTester{\n\n");

                //@InjectMocks creates class instances which need to be tested in the test class
                output.write("@InjectMocks\n");

                //Logic to extract class name (Assumes starts with public class)
                int i = line.indexOf("public class") + 13;
                String temp = "";
                while (i < line.length()) {
                    if (line.charAt(i) == '{' || line.charAt(i) == ' ') {
                        break;
                    }
                    temp += line.charAt(i);
                    i++;
                }
                nameOfClassBeingTested = temp;
                output.write(temp + " " + temp.toLowerCase() + ";\n\n");
            }
            //Public function has been detected in the line
            else if (line.contains("throws") && line.contains("public")) {

                //System.out.println("Hi in throw");
                //Logic to extract function name
                int index = line.indexOf("public") + 7;
                int flag = 0;

                while (flag != 1) {
                    if (line.charAt(index) == ' ') {
                        flag = 1;
                    }
                    index++;
                }

                String functionName = line.substring(index, line.indexOf(')') + 1);
                HashMap<String, List<String>> temp = new LinkedHashMap<>();
                functionData.put(functionName, temp);
                functionsToBeTested.add(functionName);
                currFunction = functionName;
            } else if (line.contains("@Autowired")) {
                //System.out.println("In autowire");
                autowiredFlag = 1;
            } else if (autowiredFlag == 1) {
                //System.out.println("In autowire body");
                String temp = line.substring(line.indexOf("private") + 8, line.indexOf(";")); //Abc abc
                String objName = temp.substring(temp.indexOf(" ") + 1);
                autowiredObjectList.add(objName);
                autowiredFlag = 0;
            } else {
                    for (int i = 0; i < autowiredObjectList.size(); i++) {
                        if (line.contains(autowiredObjectList.get(i) + ".")) {
                            int startIndex = line.indexOf(autowiredObjectList.get(i));
                            int endIndex = startIndex + 1;
                            while (endIndex < line.length()) {
                                if (line.charAt(endIndex) == '(') {
                                    endIndex++;
                                    break;
                                }
                                endIndex++;
                            }

                            if (!functionData.get(currFunction).containsKey(autowiredObjectList.get(i))) {
                                List<String> temp = new ArrayList<>();
                                temp.add(line.substring(startIndex, endIndex) + ")");
                                functionData.get(currFunction).put(autowiredObjectList.get(i), temp);
                            } else {
                                functionData.get(currFunction).get(autowiredObjectList.get(i)).add(line.substring(startIndex, endIndex) + ")");
                            }
                            line = line.trim();
                            List<String> temp1 = Arrays.asList(line.split(" "));
                            String startWord = temp1.get(0);
                            //Not returning a value
                            if (startWord.contains(".")) {
                                continue;
                            }
                            break;
                        }
                    }
            }
            numKeys = functionData.size();
        }

        if (testedBefore == 0) {
            //Generating @Mocks in the file
            for (String s : autowiredObjectList) {
                output.write("@Mock\n");
                output.write(s + " mock" + s + ";\n\n");
                objName.addItem(s);
            }
        }

        //final Object[] row = new Object[4];

        /*for (Map.Entry<String, HashMap<String, List<String>>> entry : functionData.entrySet()) {
            row[0] = entry.getKey();
            comboBox3.addItem(entry.getKey());
            int flag = 0;
            for (Map.Entry<String, List<String>> entry2 : entry.getValue().entrySet()) {

                for (int i = 0; i < entry2.getValue().size(); i++) {
                    if (flag == 0) {
                        flag = 1;
                    } else {
                        row[0] = "";
                    }
                    row[1] = entry2.getKey();
                    row[2] = entry2.getValue().get(i);
                    model.addRow(row);
                }
            }
        }*/
    }
}
