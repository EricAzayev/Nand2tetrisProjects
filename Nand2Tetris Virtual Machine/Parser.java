import java.io.*;

public class Parser {
    public Parser(){}
    public String vmToString(String path) {
        String toReturn = "";
        String line = "";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));

            while((line = reader.readLine())!= null) {
                if(line.indexOf("//") > -1){line = line.substring(0, line.indexOf("//")).trim();} // Remove comments
                String process = line.trim().replaceAll("\\s+", "");

                toReturn += process + "\n";
            }
            reader.close();
            return toReturn;
        } catch(FileNotFoundException e){
            e.printStackTrace();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return toReturn;
    }
    public void createHack(String name, String machineLanguage) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(name + ".mch"));
            writer.write(machineLanguage);
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}