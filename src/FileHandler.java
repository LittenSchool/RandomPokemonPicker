import java.io.*;
import java.util.ArrayList;


public class FileHandler {


    public static ArrayList<String> returnFile(String filename) {

        ArrayList<String> file = new ArrayList<>();

        try (FileReader fr = new FileReader(filename);
             BufferedReader br = new BufferedReader(fr)) {

            String line = br.readLine();
            while (line != null) {
                file.add(line);
                line = br.readLine();

            }

        }
        catch (IOException e) {
            e.printStackTrace();

        }
        return file;
    }

    public static int getLineAmount(String filename) {
        int lineAmount = 0;
        String result = null;
        try (FileReader fr = new FileReader(filename);
             BufferedReader br = new BufferedReader(fr)) {
            result = br.readLine();
            while (result != null) {
                result = br.readLine();
                lineAmount++;
            }
        } catch (IOException e) {
            System.out.println("oopsie");
            e.printStackTrace();
        }
        return lineAmount;
    }



    public static String returnLine(String filename, int lineNumber) {
        String result = null;
        try (FileReader fr = new FileReader(filename);
             BufferedReader br = new BufferedReader(fr)) {
            result = br.readLine();
            int counter = 0;
            while (counter < lineNumber && result != null) {
                result = br.readLine();
                counter++;
            }
        } catch (IOException e) {
            System.out.println("oopsie");
            e.printStackTrace();
        }

        return result;
    }



    public static void printFile(String filename) {
        ArrayList<String> file = returnFile(filename);
        for (String line : file) {
            System.out.println(line);
        }
    }

    public static void MakeFile(String filename, ArrayList<String> text) {
        try (FileWriter fw = new FileWriter(filename);
             PrintWriter pw = new PrintWriter(fw)) {

            for(String line:text) {
                pw.println(line);
            }

        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeLine(String filename, String text) {
        try (FileWriter fw = new FileWriter(filename,true);
             PrintWriter pw = new PrintWriter(fw)) {

            pw.println(text);


        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void clearFile(String filename) throws IOException {
        new FileWriter(filename, false).close();
    }


}