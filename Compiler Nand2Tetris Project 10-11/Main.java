// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {

        FileIO parser = new FileIO();

        String argument = parser.asmToString("C:\\Users\\delom\\Desktop\\Nand2Tetris Compiler Week6\\src\\Mult.asm");
        System.out.println(argument);
        Assembler assemble = new Assembler(argument);
        String result = assemble.getResult();
        System.out.println(result);
        parser.createHack("Code", result);
        }
        //System.out.println(


    }
