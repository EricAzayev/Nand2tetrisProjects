import java.util.HashMap;
import java.util.Map;
import java.util.*;
import java.util.stream.Collectors;

public class Assembler {
    private ArrayList<String> lines = new ArrayList<>();
    private Map<String, Integer> symbolTable = new HashMap<>();
    private Map<String, String> destTable = new HashMap<>();
    private Map<String, String> instructionTable = new HashMap<>();
    private int[] RAM = new int[16384];
    private int n = 16; // starting RAM address for variables

    public Assembler(String argument) {
        String[] tempList = argument.split("\n"); //System.out.println(Arrays.toString(tempList));
        List<String> filteredLines = Arrays.stream(tempList)
                .filter(line -> !line.trim().isEmpty())
                .collect(Collectors.toList());

        // Convert the filtered list back to an array if needed
        String[] lineByLine = filteredLines.toArray(new String[0]);

        lines.add("Empty Line");
        for (int i = 0; i < lineByLine.length; i++) {
            String line = lineByLine[i].replaceAll("\\s+", "");

            if (line.indexOf("(") == 0) { // handle (Loops)
                String loopName = line.substring(line.indexOf("("), line.indexOf(")"));
                symbolTable.put(loopName, i); // store the line index for loop
            } else {
                lines.add(lineByLine[i]);
            }
        }
        System.out.println(Arrays.toString(lineByLine));
        initializeTables();
        //System.out.println(runThroughProgram(lines, n, destTable, instructionTable, symbolTable));
    }

    private void initializeTables() {
        // dest codes
        destTable.put("null", "000");
        destTable.put("M", "001");
        destTable.put("D", "010");
        destTable.put("MD", "011");
        destTable.put("A", "100");
        destTable.put("AM", "101");
        destTable.put("AD", "110");
        destTable.put("AMD", "111");

        // comp codes
        instructionTable.put("0", "0101010");
        instructionTable.put("1", "0111111");
        instructionTable.put("-1", "0111010");
        instructionTable.put("D", "0001100");
        instructionTable.put("A", "0110000");
        instructionTable.put("!D", "0001101");
        instructionTable.put("!A", "0110001");
        instructionTable.put("-D", "0001111");
        instructionTable.put("-A", "0110011");
        instructionTable.put("D+1", "0011111");
        instructionTable.put("A+1", "0110111");
        instructionTable.put("D-1", "0001110");
        instructionTable.put("A-1", "0110010");
        instructionTable.put("D+A", "0000010");
        instructionTable.put("D-A", "0010011");
        instructionTable.put("A-D", "0000111");
        instructionTable.put("D&A", "0000000");
        instructionTable.put("D|A", "0010101");
        instructionTable.put("M", "1110000");
        instructionTable.put("!M", "1110001");
        instructionTable.put("-M", "1110011");
        instructionTable.put("M+1", "1110111");
        instructionTable.put("M-1", "1110010");
        instructionTable.put("D+M", "1000010");
        instructionTable.put("D-M", "1010011");
        instructionTable.put("M-D", "1000111");
        instructionTable.put("D&M", "1000000");
        instructionTable.put("D|M", "1010101");

        // jump codes
        instructionTable.put("null", "000");
        instructionTable.put("JGT", "001");
        instructionTable.put("JEQ", "010");
        instructionTable.put("JGE", "011");
        instructionTable.put("JLT", "100");
        instructionTable.put("JNE", "101");
        instructionTable.put("JLE", "110");
        instructionTable.put("JMP", "111");

        // predefined symbols
        symbolTable.put("SP", 0);
        symbolTable.put("LCL", 1);
        symbolTable.put("ARG", 2);
        symbolTable.put("THIS", 3);
        symbolTable.put("THAT", 4);
        symbolTable.put("KBD", 24576);
        symbolTable.put("SCREEN", 16384);
    }

    public String runThroughProgram(ArrayList<String> lines, int n, Map<String, String> destTable, Map<String, String> instructionTable, Map<String, Integer> symbolTable) {
        StringBuilder output = new StringBuilder();

        for (int i = 1; i < lines.size(); i++) { // This is because we create an empty line in the constructor
            String line = lines.get(i).replaceAll("\\s+", "");

            if(line.indexOf("@") >= 0){  //If it is an A Instruction



                if(symbolTable.get(line.substring(1)) != null){ //taken variable or loop
                    // System.out.print(line + " recalls ");
                    String yes = Integer.toBinaryString(
                            RAM[symbolTable.get(line.substring(1)) - 1]); //pulling the address of this variable
                    // System.out.print(RAM[symbolTable.get(line.substring(1)) - 1]);

                    String fill = "";
                    for(int j = 0; j < 16 - yes.length(); j++){
                        fill+= "0";
                    }
                    output.append(fill).append(yes).append("\n"); //takes number @n convert to binary

                    //System.out.print(" in binary this is " + fill + yes);
                }

                //Handle Registers.
                if(line.contains("R")){
                    String yes = Integer.toBinaryString(Integer.parseInt(line.substring(line.indexOf("R") + 1,line.indexOf("R") + 2)));
                    StringBuilder fill = new StringBuilder();
                    for(int j = 0; j < 16 - yes.length(); j++){
                        fill.append("0");
                    }
                    output.append(fill).append(yes).append("\n"); //takes number @n convert to binary

                }

                else if(containsNumber(line)) {  //If its a number
                    Integer num = Integer.parseInt(line.substring(1,line.length()));  //CAUSING ERRORS
                    String yes = Integer.toBinaryString(num);
                    String fill = "";
                    for(int j = 0; j < 16 - yes.length(); j++){
                        fill += "0";
                    }
                    output.append(fill).append(yes).append("\n"); //takes number @n convert to binary
                    //System.out.println(output);
                }


                else if(symbolTable.get(line.substring(1)) == null) { //unrecognized variable or loop
                    symbolTable.put(line.substring(1), n);
                    String yes = Integer.toBinaryString(n);
                    String fill = "";
                    for(int j = 0; j < 16 - yes.length(); j++){
                        fill+= "0";
                    }
                    output.append(fill).append(yes).append("\n");

                    n++;
                }
            }




            else {  // Handling C Instructions    111accccccdddjjj
                String dest = "000";
                String comp = "0000000";
                String jump = "000";
                String[] parts;

                if (line.contains("=")) {
                    parts = line.split("=");
                    dest = destTable.getOrDefault(parts[0], "000");
                    line = parts[1];
                }

                if (line.contains(";")) {
                    parts = line.split(";");
                    comp = instructionTable.getOrDefault(parts[0], "0000000");
                    jump = instructionTable.getOrDefault(parts[1], "000");
                } else {
                    comp = instructionTable.getOrDefault(line, "0000000");
                }

                String binaryInstruction = "111" + comp + dest + jump;
                output.append(binaryInstruction).append("\n");
            }
        }
        return output.toString();
    }

    public String getResult() {
        return runThroughProgram(lines, n, destTable, instructionTable, symbolTable);
    }

    public boolean containsNumber(String str) {
        for (char c : str.toCharArray()) {
            if (Character.isDigit(c)) {
                return true;
            }
        }
        return false;
    }
}