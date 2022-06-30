//This class implements the UI for the Unit Test Template Generator
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.util.List;

public class UI extends JFrame {
    private JPanel mainPanel;

    //Text field that accepts the file path as input
    private JTextField filePath;

    //Button when pressed, file is selected and parsing is initiated
    private JButton chooseFileButton;

    //Drop down list consisting of all the object names (used in the setup section)
    private JComboBox objNameSetup;

    //Drop down list consisting of the functions called by the object selected in the objNameSetup drop down list.
    private JComboBox funcNameSetup;

    //Drop down list consisting of all the public functions being declared in the class to be tested.
    private JComboBox publicFuncName;

    //Field accepting the custom function name provided by the user
    private JTextField custFuncName;

    //Drop down list consisting of the objects being referenced in the public function selected in publicFuncName drop down list.
    private JComboBox objNameTestGen;

    //Drop down list consisting of the functions being called by the object selected in the objNameTestGen drop down list.
    private JComboBox funcNameTestGen;

    //Buttons used to add the input fields' data (drop down list selected item and text field inputs) to the customization text area
    private JButton addToTextAreaButton;
    private JButton addToTextAreaButtonTestAssert;
    private JButton addToTextAreaButtonTestGen;

    //Drop down list displaying the various assert statements that can be inserted by the user in the code.
    private JComboBox assertsList;

    // A rich text area used to by the user to add customizations to the test code
    private JTextArea customizationSpace;

    //Button to write the data in the customization space to the unit test code.
    private JButton AddToUnitTestButtonSetup;

    //Button to exit the application.
    private JButton cancelButton;

    //Button to complete unit test generation
    private JButton generateTestButton;

    //Labels used to label the various sections of the UI.
    private JLabel setup;
    private JLabel testGen;
    private JLabel assertGen;
    private JLabel codepreview;

    UI() throws IOException, FontFormatException {

        add(mainPanel);
        setSize(1200, 1600);
        Color color = new Color(87, 89, 89);
        Color color2 = new Color(246, 246, 246);

        objNameSetup.setEditable(true);
        objNameSetup.getEditor().getEditorComponent().setBackground(color);
        objNameSetup.getEditor().getEditorComponent().setForeground(color2);
        Font f = new Font("Menlo", 0, 16);
        objNameSetup.getEditor().getEditorComponent().setFont(f);

        setup.setToolTipText("<html>Choose the object name and function name<br>and click the <b>Add to Text Area</b> button following<br>which you can add the parameters and return<br>value. Do this for each setup statement<br>you wish to add and once done click the<br><b>Add to unit test</b> button beside<br>the text area.</html>");
        testGen.setToolTipText("<html>Choose the function for which you<br>wish to create a test function<br>and fill in a unique function name.<br>Choose the object and external function you<br>require and click <b>Add to text area</b> for<br>each. Customize in the text area<br> and once done click the <b>Add to unit test</b><br>button</html>");
        assertGen.setToolTipText("<html>Select the assert function you wish to insert<br>and click the <b>Add to Text Area</b><br>button. Add the parameters in the text area<br> and once done click the <b>Add to unit test</b><br>button</html>");

        funcNameSetup.setEditable(true);
        funcNameSetup.getEditor().getEditorComponent().setBackground(color);
        funcNameSetup.getEditor().getEditorComponent().setForeground(color2);
        funcNameSetup.getEditor().getEditorComponent().setFont(f);

        publicFuncName.setEditable(true);
        publicFuncName.getEditor().getEditorComponent().setBackground(color);
        publicFuncName.getEditor().getEditorComponent().setForeground(color2);
        publicFuncName.setBorder(null);
        publicFuncName.getEditor().getEditorComponent().setFont(f);

        objNameTestGen.setEditable(true);
        objNameTestGen.getEditor().getEditorComponent().setBackground(color);
        objNameTestGen.getEditor().getEditorComponent().setForeground(color2);
        objNameTestGen.getEditor().getEditorComponent().setFont(f);

        funcNameTestGen.setEditable(true);
        funcNameTestGen.getEditor().getEditorComponent().setBackground(color);
        funcNameTestGen.getEditor().getEditorComponent().setForeground(color2);
        funcNameTestGen.getEditor().getEditorComponent().setFont(f);

        assertsList.setEditable(true);
        assertsList.getEditor().getEditorComponent().setBackground(color);
        assertsList.getEditor().getEditorComponent().setForeground(color2);
        assertsList.getEditor().getEditorComponent().setFont(f);

        //Starts file parsing
        chooseFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Backend.inputFilePath = filePath.getText();
                try {
                    Backend.readUsingFileReader(Backend.inputFilePath);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

                for (Map.Entry<String, HashMap<String, List<String>>> entry : Backend.functionData.entrySet()) {
                    publicFuncName.addItem(entry.getKey());
                }
            }
        });

        //Adds Setup section code to the customization text area
        addToTextAreaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Backend.addFlag == 0) {
                    customizationSpace.setText("@Before\n");
                    customizationSpace.append("public void setup(){\n\n");
                    customizationSpace.append("MockitoAnnotations.initMocks(this);\n");
                    Backend.addFlag = 1;
                }
                String objectName = (String) objNameSetup.getSelectedItem();
                String functionName = (String) funcNameSetup.getSelectedItem();
                customizationSpace.append("when(" + functionName + ").thenReturn();\n");
            }
        });

        //Adds Test function code to the customization text area
        addToTextAreaButtonTestGen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String publicFunctionName = (String) publicFuncName.getSelectedItem();
                String functionName = custFuncName.getText();
                try {
                    if (functionName.equals("")) {
                        JOptionPane.showMessageDialog(mainPanel, "Please enter a function name");
                    } else if (Files.lines(Paths.get(Backend.fileName + "Tester.java")).filter(line1 -> line1.contains(functionName)).count() != 0) {
                        JOptionPane.showMessageDialog(mainPanel, "A test function with the function name provided is already present. Choose another function name");
                    } else {
                        if (Backend.addFlag == 0) {
                            customizationSpace.setText("@Test\n");
                            customizationSpace.append("public void " + functionName + "{\n");
                            Backend.addFlag = 1;
                        }
                        String objectName = (String) objNameTestGen.getSelectedItem();
                        String functionName1 = (String) funcNameTestGen.getSelectedItem();
                        customizationSpace.append("when(" + functionName1 + ").thenReturn();\n");
                    }
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        //Adds asserts to the Customization text area
        addToTextAreaButtonTestAssert.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String asserts = (String) assertsList.getSelectedItem();
                customizationSpace.append(asserts + "\n");
            }
        });

        //Adds the code in the customization text area to the unit test code
        AddToUnitTestButtonSetup.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Backend.addFlag = 0;
                try {
                    customizationSpace.append("}\n");
                    Backend.output.write(customizationSpace.getText());
                    Backend.output.write("\n");
                    Backend.output.close();
                    Backend.output = new FileWriter(Backend.fileName + "Tester.java", true);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                customizationSpace.setText("Customization Space...");
            }
        });

        //Terminates unit test generation
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        //To complete unit test generation
        generateTestButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Backend.output.write("}\n");
                    Backend.output.close();
                    System.exit(0);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        //Adds corresponding functions being called to the 'function name dropdown' of the setup section
        objNameSetup.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                funcNameSetup.removeAllItems();
                if (Backend.ActionFlag == 0) {
                    Backend.ActionFlag = 1;
                } else {
                    String compare = (String) objNameSetup.getSelectedItem();
                    for (Map.Entry<String, HashMap<String, List<String>>> entry : Backend.functionData.entrySet()) {
                        for (Map.Entry<String, List<String>> entry2 : entry.getValue().entrySet()) {
                            if (entry2.getKey() == compare) {
                                for (int i = 0; i < entry2.getValue().size(); i++) {
                                    Backend.funcNameSetupItems.add(entry2.getValue().get(i));
                                }
                            }
                        }
                    }
                    for (String temp : Backend.funcNameSetupItems) {
                        funcNameSetup.addItem(temp);
                    }
                }
            }
        });

        //To display the objects referenced in the public function selected by the publicFuncName JCombobox
        publicFuncName.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                objNameTestGen.removeAllItems();
                if (Backend.ActionFlag2 == 0) {
                    Backend.ActionFlag2 = 1;
                } else {
                    String compare = (String) publicFuncName.getSelectedItem();
                    for (Map.Entry<String, HashMap<String, List<String>>> entry : Backend.functionData.entrySet()) {
                        if (entry.getKey() == compare) {
                            for (Map.Entry<String, List<String>> entry2 : entry.getValue().entrySet()) {
                                objNameTestGen.addItem(entry2.getKey());
                            }
                            break;
                        }
                    }
                }
            }
        });

        //To display the functions referenced in the object selected by the objNameTestGen JCombobox
        objNameTestGen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                funcNameTestGen.removeAllItems();
                if (Backend.ActionFlag3 == 0) {
                    Backend.ActionFlag3 = 1;
                } else {
                    String compare1 = (String) publicFuncName.getSelectedItem();
                    String compare2 = (String) objNameTestGen.getSelectedItem();
                    for (Map.Entry<String, HashMap<String, List<String>>> entry : Backend.functionData.entrySet()) {
                        if (entry.getKey() == compare1) {
                            for (Map.Entry<String, List<String>> entry2 : entry.getValue().entrySet()) {
                                if (entry2.getKey() == compare2) {
                                    for (int i = 0; i < entry2.getValue().size(); i++) {
                                        funcNameTestGen.addItem(entry2.getValue().get(i));
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

        //Loads the code preview when the code preview icon is pressed
        codepreview.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                UIManager.put("OptionPane.minimumSize", new Dimension(300, 300));

                DefaultMutableTreeNode root = new DefaultMutableTreeNode(Backend.nameOfClassBeingTested);

                for (Map.Entry<String, HashMap<String, List<String>>> entry : Backend.functionData.entrySet()) {
                    DefaultMutableTreeNode row = new DefaultMutableTreeNode(entry.getKey());
                    for (Map.Entry<String, List<String>> entry2 : entry.getValue().entrySet()) {
                        for (int i = 0; i < entry2.getValue().size(); i++) {
                            DefaultMutableTreeNode node = new DefaultMutableTreeNode(entry2.getValue().get(i));
                            row.add(node);
                        }
                    }
                    root.add(row);
                }
                DefaultTreeModel model = new DefaultTreeModel(root);
                JTree codePreview = new JTree(model);

                Enumeration e1 = root.breadthFirstEnumeration();
                while (e1.hasMoreElements()) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) e1.nextElement();
                    if (node.isLeaf()) break;
                    int row = codePreview.getRowForPath(new TreePath(node.getPath()));
                    codePreview.expandRow(row);
                }

                JOptionPane.showMessageDialog(mainPanel, "Code Preview\n");
                JOptionPane.showMessageDialog(mainPanel, codePreview);
            }
        });
    }

    public void setObjName(String ObjectName){
        objNameSetup.addItem(ObjectName);
    }

    public void showMessage(String message){
        JOptionPane.showMessageDialog(mainPanel,message);
    }
}