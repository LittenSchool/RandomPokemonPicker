import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.*;

public class RandomNumberGUI {
    private static JFrame frame;
    private static JLabel numberLabel;
    private static boolean isTradeMode;

    private static JList[] lists;
    private static DefaultListModel<String>[] listModels;
    private static int[] currentNumbers; // Stores roll per player
    private static ArrayList<String> removedSpeciesBuffer = new ArrayList<>();
    private static Random random = new Random();

    // Players
    static String[] players;
    static int[] discardsPerPlayer;
    static int numberOfPlayers;

    // ===== GLOBAL DISCARD LIMIT =====
    private static final int remainingDiscards = 3;


    public static void run() {
        // make the list of players
        players = new String[TheCollection.players.size()];
        for (int i = 0; i < TheCollection.players.size(); i++) {
            players[i] = TheCollection.players.get(i);
        }
        numberOfPlayers = players.length;
        discardsPerPlayer = new int[players.length];

        for (int i = 0; i < numberOfPlayers; i++) {
            discardsPerPlayer[i] = remainingDiscards;
        }

        frame = new JFrame("Random Pokemon Roller");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1100, 650);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                try {
                    TheCollection.saveList();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });


        numberLabel = new JLabel("Roll for a Player Below");
        numberLabel.setFont(new Font("Impact", Font.BOLD, 32));
        numberLabel.setHorizontalAlignment(SwingConstants.CENTER);
        frame.add(numberLabel, BorderLayout.NORTH);

        lists = new JList[numberOfPlayers];
        listModels = new DefaultListModel[numberOfPlayers];
        currentNumbers = new int[numberOfPlayers];

        JPanel listPanel = new JPanel(new GridLayout(1, numberOfPlayers));

        for (int i = 0; i < numberOfPlayers; i++) {
            currentNumbers[i] = -1;

            listModels[i] = new DefaultListModel<>();
            lists[i] = new JList<>(listModels[i]);
            applyListFont(lists[i], new Font("Impact", Font.PLAIN, 30));

            int finalIndex = i;
            lists[i].addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int idx = lists[finalIndex].locationToIndex(e.getPoint());
                    if (idx != -1 && e.getButton() == MouseEvent.BUTTON1) {
                        onListElementClicked(finalIndex, listModels[finalIndex].getElementAt(idx));
                    }
                    else if (idx != -1 && e.getButton() == MouseEvent.BUTTON3) {
                        TradeMenu.run(finalIndex, listModels[finalIndex].getElementAt(idx));
                    }
                }
            });

            JScrollPane scroll = new JScrollPane(lists[i]);

            JPanel column = new JPanel(new BorderLayout());

            JLabel nameLabel = new JLabel(players[i], SwingConstants.CENTER);
            nameLabel.setFont(new Font("Impact", Font.BOLD, 32));
            Color tempColour = Color.decode("#" + Objects.requireNonNull(TheCollection.getPlayerColours().get(i)));
            nameLabel.setForeground(tempColour);
            column.add(nameLabel, BorderLayout.NORTH);

            JButton rollBtn = new JButton("Roll");
            int idx = i;
            rollBtn.addActionListener(e -> {
                try {
                    handleRoll(idx);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            JPanel buttonRow = new JPanel(new GridLayout(1, 1));
            buttonRow.add(rollBtn);

            column.add(scroll, BorderLayout.CENTER);
            column.add(buttonRow, BorderLayout.SOUTH);

            listPanel.add(column);
        }

        frame.add(listPanel, BorderLayout.CENTER);

        // ------------------------------
        // LOAD SAVE FILE *BEFORE SHOWING FRAME*
        // ------------------------------
        ArrayList<String> speciesToRemove = new ArrayList<>();

        if(!StartupPeoplePicker.newSave) {

            for (int i = 0; i < FileHandler.getLineAmount(TheCollection.savedList); i++) {

                String collectable = FileHandler.returnLine(TheCollection.savedList, i);
                if (!collectable.isEmpty()) {

                    String[] stats = collectable.split(",");

                    if (stats[0].equals("-1")) {
                        TheCollection.vetod.add(stats[1]);
                    } else if (stats[0].equals("discards")) {
                        for (int j = 0; j < numberOfPlayers; j++)
                            discardsPerPlayer[j] = Integer.parseInt(stats[j + 1]);
                    } else if (stats[0].equals("players")) {

                    } else if (stats[0].equals("colours")) {

                    }
                    else {
                        // adding the pokemon to a list
                        Pokemon temp = new Pokemon(stats[1]);
                        temp.setColour(stats[2]);
                        TheCollection.pokemonInPlay.add(temp);

                        int p = Integer.parseInt(stats[0]);
                        listModels[p].addElement(stats[1]);  // <-- OK now (not visible yet)
                    }

                    speciesToRemove.add(stats[1]);
                }
            }
        }

        // ensure species cleanup happens after model fill
        SwingUtilities.invokeLater(() -> {
            for (String s : speciesToRemove) {
                TheCollection.species.remove(s);
            }
        });

        // ------------------------------
        // NOW show the frame safely
        // ------------------------------
        frame.setVisible(true);

        System.out.println(TheCollection.species);
    }


    private static void handleRoll(int playerIndex) throws IOException {
        if ((TheCollection.getSpecies().isEmpty())) {
            System.out.println("End of list");
            System.out.println(TheCollection.species);
            return;
        }
        int rolledNumber = random.nextInt(TheCollection.getSpecies().size());
        String pokemonName = TheCollection.getSpecies().get(rolledNumber);
        for (int i = 0; i < players.length; i++) {
            if (players[i].equals(pokemonName)) {
            }
        }

        //Make the whole evo line
        String wholeEvoLine = getEvoLineFromSpecies(pokemonName);


        numberLabel.setText(players[playerIndex] + " rolled: The " + pokemonName + " line");

        // Confirmation Window
        Object[] options = {
                "Save Pokémon",
                "Discard (Remaining: " + discardsPerPlayer[playerIndex] + ")",
                "Cancel"
        };

        Object[] discardOptions = {
                "Put back into pool",
                "I'm Vetoing this",
        };

        int choice = JOptionPane.showOptionDialog(
                frame,
                players[playerIndex] + " rolled: The " + pokemonName + " line" +
                        "\n\nThis line has the Pokémon:\n" +
                        wholeEvoLine +
                        "\nSave this Pokémon or discard it?",
                "Confirm Roll",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
        );


        if (choice == 0) { // Save
            Pokemon temp = new Pokemon(pokemonName);
            temp.setColour(TheCollection.getPlayerColours().get(playerIndex));
            TheCollection.pokemonInPlay.add(temp);
            listModels[playerIndex].addElement(pokemonName);
            TheCollection.species.remove(pokemonName);
        }
        else if (choice == 1) { // Discard
            int discardChoice;
            if (discardsPerPlayer[playerIndex] > 0) {
                discardsPerPlayer[playerIndex]--;

                discardChoice = JOptionPane.showOptionDialog(
                        frame,
                        "Discarded. Remaining discards: " + discardsPerPlayer[playerIndex],
                        "Discard Used",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        discardOptions,
                        discardOptions[0]
                );

                if (discardChoice == 1) { //Veto
                    TheCollection.vetod.add(pokemonName);
                    TheCollection.species.remove(pokemonName);
                }

                if (discardsPerPlayer[playerIndex] == 0) {
                    JOptionPane.showMessageDialog(
                            frame,
                            "No discards remaining!",
                            "Out of Discards",
                            JOptionPane.WARNING_MESSAGE
                    );
                }

            } else {
                JOptionPane.showMessageDialog(
                        frame,
                        "No discards remaining!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                listModels[playerIndex].addElement(pokemonName);
            }
        }
        // Cancel does nothing
        TheCollection.saveList();
    }

    private static void applyListFont(JList<String> list, Font font) {

        list.setCellRenderer(new DefaultListCellRenderer() {

            @Override
            public Component getListCellRendererComponent(
                    JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus) {

                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);

                label.setFont(font);

                if (value != null) {
                    String name = value.toString();


                    // --- ICON ---
                    ImageIcon icon = loadPokemonIcon(name);
                    label.setIcon(icon);

                    // --- TEXT COLOR LOGIC ---
                        if (TheCollection.getPokemonInPlayString().contains(name)) {
                            Color tempColour = Color.decode("#" + Objects.requireNonNull(TheCollection.getPokemonColour(name)));
                            label.setForeground(tempColour);
                        }




                }

                return label;
            }
        });
    }


    static String getCSV() {
        String csv = "";
        for (int i = 0; i < numberOfPlayers; i++) {
            for (int j = 0; j < listModels[i].size(); j++) {
                csv += i + "," +listModels[i].get(j) + "," + TheCollection.getPokemonColour(listModels[i].get(j)) + "\n";
            }
        }
        for (int i = 0; i < TheCollection.vetod.size(); i++) {
            csv += "-1," +TheCollection.vetod.get(i) + "\n";
        }
        csv += "discards,";
        for (int i = 0; i < discardsPerPlayer.length; i++) {
            csv += discardsPerPlayer[i] + ",";
        }
        csv += "\n";
        csv += "players,";
        for (int i = 0; i < numberOfPlayers; i++) {
            csv += players[i] + ",";
        }
        csv += "\n";
        csv += "colours,";
        for (int i = 0; i < numberOfPlayers; i++) {
            csv += TheCollection.getPlayerColours().get(i) + ",";
        }
        csv += "\n";

        return csv;
    }

    private static void onListElementClicked(int playerIndex, String value) {
        String message = getEvoLineFromSpecies(value);
        JOptionPane.showMessageDialog(frame,
                value + " evo line includes: \n" + message,
                "Item Clicked",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private static String getEvoLineFromSpecies(String pokemonName) {
        StringBuilder wholeEvoLine = new StringBuilder();
        for (int i = 0; i < TheCollection.getPokemon().size(); i++) {
            if (TheCollection.getPokemon().get(i).getEvoLine().equals(pokemonName)) {
                assert false;
                wholeEvoLine.append(TheCollection.getPokemon().get(i).getName());
                wholeEvoLine.append(" - BST ");
                wholeEvoLine.append(TheCollection.getPokemon().get(i).getBST());
                wholeEvoLine.append("\n");
            }
        }
        return wholeEvoLine.toString();
    }

    // ---- IMAGE SUPPORT ----
    private static String imageFolderPath = ""; // YOU set this externally
    private static final Map<String, ImageIcon> iconCache = new HashMap<>();

    public static void setImageFolderPath(String path) {
        imageFolderPath = path;
    }

    private static ImageIcon loadPokemonIcon(String pokemonName) {
        if (iconCache.containsKey(pokemonName)) {
            return iconCache.get(pokemonName);
        }
        if (imageFolderPath == null || imageFolderPath.isEmpty()) {
            return null;
        }

        try {
            String filePath = imageFolderPath + pokemonName + ".png";
            ImageIcon icon = new ImageIcon(filePath);

            if (icon.getIconWidth() <= 0) {
                iconCache.put(pokemonName, null);
                return null;
            }

            // Resize to fit list
            Image scaled = icon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
            ImageIcon result = new ImageIcon(scaled);
            iconCache.put(pokemonName, result);
            return result;

        } catch (Exception e) {
            iconCache.put(pokemonName, null);
            return null;
        }
    }

    public static void trade(String pokemonNameA, String pokemonNameB, int playerA, int playerB) {
        listModels[playerA].removeElement(pokemonNameA);
        listModels[playerB].removeElement(pokemonNameB);
        listModels[playerA].addElement(pokemonNameB);
        listModels[playerB].addElement(pokemonNameA);
    }

    public static Pokemon nameToPokemon(String pokemonName) {
        for (int i = 0; i < TheCollection.getPokemon().size(); i++) {
            if (TheCollection.getPokemon().get(i).getName().equals(pokemonName)) {
                return TheCollection.getPokemon().get(i);
            }
        }
        return null;
    }

    public static boolean getIsTradeMode() {
        return isTradeMode;
    }

    public static void setIsTradeMode(boolean isTradeMode) {
        RandomNumberGUI.isTradeMode = isTradeMode;
    }

    public static String getPlayer(int id) {
        return players[id];
    }

}
