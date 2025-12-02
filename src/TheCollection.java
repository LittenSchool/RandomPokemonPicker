import java.util.ArrayList;
import java.util.Scanner;

public class TheCollection {
    static ArrayList<Pokemon> pokemon = new ArrayList<Pokemon>();
    public static void run() {
        String filename = "Pokemon.txt";
        Scanner sc = new Scanner(System.in);

        FileHandler fh = new FileHandler();
        String collectable;
        String[] stats;
        for (int i = 0; i < FileHandler.getLineAmount(filename); i++) {
            collectable = FileHandler.returnLine(filename, i);
            stats = collectable.split(",");
            pokemon.add(new Pokemon(stats[0], Double.parseDouble(stats[1]), Integer.parseInt(stats[2]), Integer.parseInt(stats[3]), Integer.parseInt(stats[4]), Integer.parseInt(stats[5]), Integer.parseInt(stats[6]), Integer.parseInt(stats[7])));
        }
        new RandomNumberGUI();
    }

    public ArrayList<Pokemon> getPokemon() {
        return pokemon;
    }
}
