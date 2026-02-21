import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class TheCollection {
    static String filename = "Pokemon.txt";
    static String savedList = "Save.txt";
    static ArrayList<Pokemon> pokemon = new ArrayList<>();
    static ArrayList<String> species = new ArrayList<>();
    static ArrayList<String> vetod = new ArrayList<>();
    static ArrayList<String> players = new ArrayList<>();
    static RandomNumberGUI gui;

    public static void run() {
        String collectable;
        String[] stats;
        for (int i = 0; i < FileHandler.getLineAmount(filename); i++) {
            collectable = FileHandler.returnLine(filename, i);
            stats = collectable.split(",");
            pokemon.add(new Pokemon(stats[0], Double.parseDouble(stats[1]), Integer.parseInt(stats[2]), Integer.parseInt(stats[3]), Integer.parseInt(stats[4]), Integer.parseInt(stats[5]), Integer.parseInt(stats[6]), Integer.parseInt(stats[7]),stats[8]));
        }
        for (int i = 0; i < pokemon.size(); i++) {
            if (!species.contains(pokemon.get(i).getEvoLine())) {
                species.add(pokemon.get(i).getEvoLine());
            }
        }
        File f = new File(savedList);
        if(!(f.exists() && !f.isDirectory())) {
            StartupPeoplePicker.run();
        } else {
            String[] thePlayers = FileHandler.returnLine(TheCollection.savedList, FileHandler.getLineAmount(TheCollection.savedList) - 2).split(",");
            for (int i = 1; i < thePlayers.length; i++) {
                players.add(thePlayers[i]);
            }
            RandomNumberGUI.run();
        }
    }

    public static void save() throws IOException {
        FileHandler.clearFile(filename);
        for (int i = 0; i < pokemon.size(); i++) {

            FileHandler.writeLine(filename, pokemon.get(i).getCSV());
        }
    }

    public static void saveList() throws IOException {
        FileHandler.clearFile(savedList);
        FileHandler.writeLine(savedList,RandomNumberGUI.getCSV());
    }



    static public ArrayList<Pokemon> getPokemon() {
        return pokemon;
    }

    public static ArrayList<String> getVetod() {
        return vetod;
    }

    public static ArrayList<String> getSpecies() {
        return species;
    }

    public static void addPlayer(String player) {
        players.add(player);
    }
}
