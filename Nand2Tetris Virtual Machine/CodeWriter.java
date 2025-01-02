import java.util.*;
import java.io.*;

public class CodeWriter {
    ArrayList<Integer> Stack = new ArrayList<>(8192);
    int[] RAM = new int[8192];
    int SP = 256; // Stack Pointer
    HashMap<String, Integer> memorySegments = new HashMap<>();
    HashMap<String, Integer> memorySegmentPointer = new HashMap<>();

    public CodeWriter(){}


    public String writeReturn(String mainFile) { //creates final VM Return

        Parser parseThis = new Parser();

        String main = parseThis.vmToString(mainFile);
        //System.out.println(argument);
        initiateMemorySegments();

        String toReturn = "@SP\nD=A\n@SP\nM=D\n";
        toReturn += fullyProcessVM(main.split("\n"), 0);

    }




    private String fullyProcessVM(String[] fileParsed, int parameters){  //argument should have no whitespace or comments.  // writeObject.fullyProcessVM(argument);
        //Pass parameters into called function (for(params), pop from stack add here)
        //Determine and save return address(SP - parameters)

        //Question: for "Jump to execute function", Where in the vm is the code for function stored?

        String toReturn;
        for(int line = 0; line < fileParsed.length; line++){
            if(fileParsed[line].contains("call")){
                String[] parts = fileParsed[line].split(" ");
                String functionContent = (new Parser().vmToString(parts[1] + ".vm"));
                String s = fullyProcessVM(functionContent, Integer.parseInt(parts[2]));//this should be a recursive function that reads the contents of the
            }
            else if(fileParsed[line].contains("return")){
                    return toReturn;
            }
            else{
                toReturn += processLogicByLine(fileParsed[line]);
            }

        }
        processLogicByLine();
        // Output the generated Hack assembly code
        return toReturn;
    }

    public String processLogicByLine(String line) {
        StringBuilder toReturn = new StringBuilder(   /*Call Function Main*/);
        String[] parts = line.split(" ");
        String commandType = parts[0];

        switch (commandType) {
            case "push":
                toReturn.append(handlePush(parts[1], Integer.parseInt(parts[2])));
                break;
            case "pop":
                toReturn.append(handlePop(parts[1], Integer.parseInt(parts[2])));
                break;
            case "add":
                toReturn.append(generateAddCode());
                break;
            case "sub":
                toReturn.append(generateSubCode());
                break;
            case "neg":
                toReturn.append(generateNegCode());
                break;
            case "eq":
                toReturn.append(generateEqCode());
                break;
            case "gt":
                toReturn.append(generateGtCode());
                break;
            case "lt":
                toReturn.append(generateLtCode());
                break;
            case "and":
                toReturn.append(generateAndCode());
                break;
            case "or":
                toReturn.append(generateOrCode());
                break;
            case "not":
                toReturn.append(generateNotCode());
                break;
            default:
                throw new IllegalArgumentException("Invalid command: " + commandType);
        }
        return toReturn.toString();
    }

    private String handlePush(String segment, int index) {
        int address = memorySegmentPointer.get(segment) + index;
        Stack.add(SP++, RAM[address]);
        return "@" + index + "\nD=A\n@" + segment + "\nA=M\nD=D+A\n@SP\nA=M\nM=D\n@SP\nM=M+1\n";
    }

    private String handlePop(String segment, int index) {
        int address = memorySegmentPointer.get(segment) + index;
        RAM[address] = Stack.get(--SP);
        return "@SP\nM=M-1\nA=M\nD=M\n@" + segment + "\nA=M\nD=D+A\n@R13\nM=D\n@SP\nA=M\nD=M\n@R13\nA=M\nM=D\n";
    }

    private String generateAddCode() {
        return "@SP\nM=M-1\nA=M\nD=M\n@SP\nM=M-1\nA=M\nD=D+M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n";
    }

    private String generateSubCode() {
        return "@SP\nM=M-1\nA=M\nD=M\n@SP\nM=M-1\nA=M\nD=M-D\n@SP\nA=M\nM=D\n@SP\nM=M+1\n";
    }

    private String generateNegCode() {
        return "@SP\nM=M-1\nA=M\nM=-M\n@SP\nM=M+1\n";
    }

    private String generateEqCode() {
        return "@SP\nM=M-1\nA=M\nD=M\n@SP\nM=M-1\nA=M\nD=M-D\n@EQ_TRUE\nD;JEQ\n@SP\nA=M\nM=0\n@END\n0;JMP\n(EQ_TRUE)\n@SP\nA=M\nM=-1\n(END)\n@SP\nM=M+1\n";
    }

    private String generateGtCode() {
        return "@SP\nM=M-1\nA=M\nD=M\n@SP\nM=M-1\nA=M\nD=M-D\n@GT_TRUE\nD;JGT\n@SP\nA=M\nM=0\n@END\n0;JMP\n(GT_TRUE)\n@SP\nA=M\nM=-1\n(END)\n@SP\nM=M+1\n";
    }

    private String generateLtCode() {
        return "@SP\nM=M-1\nA=M\nD=M\n@SP\nM=M-1\nA=M\nD=M-D\n@LT_TRUE\nD;JLT\n@SP\nA=M\nM=0\n@END\n0;JMP\n(LT_TRUE)\n@SP\nA=M\nM=-1\n(END)\n@SP\nM=M+1\n";
    }

    private String generateAndCode() {
        return "@SP\nM=M-1\nA=M\nD=M\n@SP\nM=M-1\nA=M\nD=D&M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n";
    }

    private String generateOrCode() {
        return "@SP\nM=M-1\nA=M\nD=M\n@SP\nM=M-1\nA=M\nD=D|M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n";
    }

    private String generateNotCode() {
        return "@SP\nM=M-1\nA=M\nM=!M\n@SP\nM=M+1\n";
    }

    private void initiateMemorySegments() {
        memorySegmentPointer.put("constant", 1000);
        memorySegmentPointer.put("local", 2000);
        memorySegmentPointer.put("argument", 3000);
        memorySegmentPointer.put("this", 4000);
        memorySegmentPointer.put("that", 5000);
        memorySegmentPointer.put("static", 6000);

        memorySegments.put("constant", 1000);
        memorySegments.put("local", 2000);
        memorySegments.put("argument", 3000);
        memorySegments.put("this", 4000);
        memorySegments.put("that", 5000);
        memorySegments.put("static", 6000);
    }
}
