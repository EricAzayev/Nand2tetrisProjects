// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        Parser parseThis = new Parser();
        String argument = parseThis.vmToString("src/text.vm");
        System.out.println(argument);
        parseThis.createHack("name", argument); //creates the txt file containing code.
        //To finish, create all neccesary Hack Arguments.

    }
}