import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        RandomNumberGUI.setImageFolderPath("Pokemon Icons/");
        TheCollection.run();
        System.out.println(TheCollection.getSpecies().size());

    }
}


