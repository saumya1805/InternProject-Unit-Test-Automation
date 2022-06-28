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
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.util.List;

public class UI extends JFrame {
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
    private JButton button1;
    private JLabel setup;
    private JLabel testGen;
    private JLabel assertGen;

    DefaultTableModel model;

    UI() throws IOException {

        add(mainPanel);
        setSize(1200, 1500);
        Color color = new Color(111, 151, 158);
        Color color2 = new Color(246, 246, 246);

        objName.setUI(new MetalComboBoxUI());
        publicFuncName.setUI(new MetalComboBoxUI());
        objName1.setUI(new MetalComboBoxUI());
        funcName.setUI(new MetalComboBoxUI());
        funcName1.setUI(new MetalComboBoxUI());
        assertsList.setUI(new MetalComboBoxUI());

        objName.setEditable(true);
        objName.getEditor().getEditorComponent().setBackground(color);
        objName.getEditor().getEditorComponent().setForeground(color2);
        Font f = new Font("Menlo", 0, 16);
        objName.getEditor().getEditorComponent().setFont(f);

        setup.setToolTipText("<html>Choose the object name and function name<br>and click the <b>Add to Text Area</b> button following<br>which you can add the parameters and return<br>value. Do this for each setup statement<br>you wish to add and once done click the<br><b>Add to unit test</b> button beside<br>the text area.</html>");
        testGen.setToolTipText("<html>Choose the function for which you<br>wish to create a test function<br>and fill in a unique function name.<br>Choose the object and external function you<br>require and click <b>Add to text area</b> for<br>each. Customize in the text area<br> and once done click the <b>Add to unit test</b><br>button</html>");
        assertGen.setToolTipText("<html>Select the assert function you wish to insert<br>and click the <b>Add to Text Area</b><br>button. Add the parameters in the text area<br> and once done click the <b>Add to unit test</b><br>button</html>");

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
        addToTextAreaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Backend.addFlag == 0) {
                    customizationSpace.setText("@Before\n");
                    customizationSpace.append("public void beforeTest(){\n\n");
                    customizationSpace.append("MockitoAnnotations.initMocks(this);\n");
                    Backend.addFlag = 1;
                }
                String objectName = (String) objName.getSelectedItem();
                String functionName = (String) funcName.getSelectedItem();
                customizationSpace.append("when(" + functionName + ").thenReturn();\n");
            }
        });
        addToTextAreaButton3.addActionListener(new ActionListener() {
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
                        String objectName = (String) objName1.getSelectedItem();
                        String functionName1 = (String) funcName1.getSelectedItem();
                        customizationSpace.append("when(" + functionName1 + ").thenReturn();\n");
                    }
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        addToTextAreaButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String asserts = (String) assertsList.getSelectedItem();
                customizationSpace.append(asserts + "\n");
            }
        });
        AddToUnitTestButton.addActionListener(new ActionListener() {
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
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
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
        objName.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                funcName.removeAllItems();
                if (Backend.ActionFlag == 0) {
                    Backend.ActionFlag = 1;
                } else {
                    String compare = (String) objName.getSelectedItem();
                    for (Map.Entry<String, HashMap<String, List<String>>> entry : Backend.functionData.entrySet()) {
                        for (Map.Entry<String, List<String>> entry2 : entry.getValue().entrySet()) {
                            if (entry2.getKey() == compare) {
                                for (int i = 0; i < entry2.getValue().size(); i++) {
                                    Backend.box2.add(entry2.getValue().get(i));
                                }
                            }
                        }
                    }
                    for (String temp : Backend.box2) {
                        funcName.addItem(temp);
                    }
                }
            }
        });
        publicFuncName.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                objName1.removeAllItems();
                if (Backend.ActionFlag2 == 0) {
                    Backend.ActionFlag2 = 1;
                } else {
                    String compare = (String) publicFuncName.getSelectedItem();
                    for (Map.Entry<String, HashMap<String, List<String>>> entry : Backend.functionData.entrySet()) {
                        if (entry.getKey() == compare) {
                            for (Map.Entry<String, List<String>> entry2 : entry.getValue().entrySet()) {
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
                if (Backend.ActionFlag3 == 0) {
                    Backend.ActionFlag3 = 1;
                } else {
                    String compare1 = (String) publicFuncName.getSelectedItem();
                    String compare2 = (String) objName1.getSelectedItem();
                    for (Map.Entry<String, HashMap<String, List<String>>> entry : Backend.functionData.entrySet()) {
                        if (entry.getKey() == compare1) {
                            for (Map.Entry<String, List<String>> entry2 : entry.getValue().entrySet()) {
                                if (entry2.getKey() == compare2) {
                                    for (int i = 0; i < entry2.getValue().size(); i++) {
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

        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UIManager.put("OptionPane.minimumSize", new Dimension(300, 300));

                DefaultMutableTreeNode root = new DefaultMutableTreeNode("Code Preview");

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

                JOptionPane.showMessageDialog(mainPanel, "Code Preview:\n");
                JOptionPane.showMessageDialog(mainPanel, codePreview);
            }
        });
    }

    public void setObjName(String ObjectName){
        objName.addItem(ObjectName);
    }

    public void showMessage(String message){
        JOptionPane.showMessageDialog(mainPanel,message);
    }
}