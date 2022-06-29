//This class is to implement the Backend logic for the Unit Test Template Generator
import java.io.*;
import java.util.*;

public class Backend {

    //Stores the name of the file being tested. Used to check if a unit test already exists for the file and naming the unit test file.
    public static String fileName;

    public static Set<String> box2 = new HashSet<>();

    //Used to write into the unit test file
    static FileWriter output;

    //Name of the class being tested
    public static String nameOfClassBeingTested;

    //Name of current function being parsed. Helps to associate the lines being read in the function body with the function
    public static String currFunction;

    //The absolute filepath taken as input from the user via the Java Swing GUI
    public static String inputFilePath;

    public static Vector<String> autowiredObjectList = new Vector<>();

    //Hashmap storing information about the public functions being declared in a class, what external objects these functions use and what functions do these external objects call
    public static HashMap<String, HashMap<String, List<String>>> functionData = new LinkedHashMap();


    //Flag for registering that previous line had an @Autowired object
    public static int autowiredFlag = 0;

    //Flags to ensure the default JCombobox item is not selected
    public static int ActionFlag = 0;

    public static int ActionFlag2 = 0;

    public static int ActionFlag3 = 0;

    /*Flag to decided whether a code statement should be appended to the customization space (if the code segment is already present
    in the customization space) or should the customization space initialize the section.*/
    public static int addFlag = 0;

    /*Flag that is set when a unit test for the existing file already exists. Prevents the regeneration of certain sections which should
    be present in the code only once (imports,mocks)*/
    public static int testedBefore = 0;

    //Function that parses the code to be tested. Used to identify sections of the code.
    public static void readUsingFileReader(String filePath) throws IOException {

        File file = new File(filePath);
        FileReader fr = null;
        try {
            fileName = file.getName();
            //Initializes file reader for the file
            fr = new FileReader(file);
        } catch (FileNotFoundException e) {
            //Alert if the file does not exist
            open.form.showMessage("A unit test for this code already exists. Proceed with Test generation.");
        }

        //Reads the file line by line
        BufferedReader br = new BufferedReader(fr);

        //Stores the current line of code that was read
        String line;

        //Creates file that will store the unit test code corresponding to the code to be tested
        File file1 = new File(fileName + "Tester.java");

        //To write into the unit test code file

        try {
            // create a new file with name specified by the file object. Returns true if a new file was created and false if a file by the same name already exists.
            boolean value = file1.createNewFile();

            if (value) {
                //New file created. No unit test was pre-existing
                System.out.println("New Java File is created.");
                output = new FileWriter(fileName + "Tester.java", true);
            } else {
                //A unit test already pre-exists
                System.out.println("The file already exists.");
                output = new FileWriter(fileName + "Tester.java", true);
                testedBefore = 1;
                //Alert for user to skip setup and proceed with Test function generation
                open.form.showMessage("A unit test for this code already exists. Proceed with Test functions generation.");
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
            else if (testedBefore == 0 && line.contains("public") && line.contains("class")) {

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
                output.write("public class " + fileName + "Tester{\n\n");

                //@InjectMocks creates class instances which need to be tested in the test class
                output.write("@InjectMocks\n");

                //Logic to extract class name
                int i = line.indexOf("class") + 6;
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
            else if (line.contains("throws") && line.contains("public") || line.contains("(") && line.contains("public")) {

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
                currFunction = functionName;
            }
            //@Autowired encountered
            else if (line.contains("@Autowired")) {
                autowiredFlag = 1;
            }
            //Autowired object encountered
            else if (autowiredFlag == 1) {
                String objName = "";
                if (line.contains("private")) {
                    String temp = line.substring(line.indexOf("private") + 8, line.indexOf(";"));
                    objName = temp.substring(temp.indexOf(" ") + 1);
                } else if (line.contains("protected")) {
                    String temp = line.substring(line.indexOf("protected") + 10, line.indexOf(";"));
                    objName = temp.substring(temp.indexOf(" ") + 1);
                }
                else{
                    String temp = line.substring(line.indexOf("public") + 7, line.indexOf(";"));
                    objName = temp.substring(temp.indexOf(" ") + 1);
                }
                autowiredObjectList.add(objName);
                autowiredFlag = 0;
            }
            //Parses the body of the public function line by line
            else {
                //Searches for mock object in the line
                for (String s : autowiredObjectList) {
                    if (line.contains(s + ".")) {
                        int startIndex = line.indexOf(s);
                        int endIndex = startIndex + 1;
                        while (endIndex < line.length()) {
                            if (line.charAt(endIndex) == '(') {
                                endIndex++;
                                break;
                            }
                            endIndex++;
                        }

                        //Updating hashmap
                        if (!functionData.get(currFunction).containsKey(s)) {
                            List<String> temp = new ArrayList<>();
                            temp.add(line.substring(startIndex, endIndex) + ")");
                            functionData.get(currFunction).put(s, temp);
                        } else {
                            functionData.get(currFunction).get(s).add(line.substring(startIndex, endIndex) + ")");
                        }
                    }
                }
            }
        }

        if (testedBefore == 0) {
            //Generating @Mocks in the file
            for (String s : autowiredObjectList) {
                output.write("@Mock\n");
                output.write(s + " mock" + s + ";\n\n");
                open.form.setObjName(s);
            }
        }
    }
}
