import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Objects;
import java.util.Random;

public class RandomNumberGUI {
    private static JFrame frame;
    private static JLabel numberLabel;

    private static JList[] lists;
    private static DefaultListModel<String>[] listModels;
    private static int[] currentNumbers; // Stores roll per player

    private static Random random = new Random();

    // Players
    static String[] players = {"Jason", "Jiveen", "Jacob", "Isabelle", "Calum", "Karesz", "Viktors","8th more sinister option"};
    static int[] discardsPerPlayer = new int[players.length];
    static int numberOfPlayers = players.length;

    // ===== GLOBAL DISCARD LIMIT =====
    private static final int remainingDiscards = 3;


    public static void run() {
        for (int i = 0; i < numberOfPlayers; i++) {
            discardsPerPlayer[i] = remainingDiscards;
        }
        frame = new JFrame("Random Pokemon Roller");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1100, 650);
        frame.setLayout(new BorderLayout());
        // Top display label
        numberLabel = new JLabel("Roll for a Player Below");
        numberLabel.setFont(new Font("Impact", Font.BOLD, 32));
        numberLabel.setHorizontalAlignment(SwingConstants.CENTER);
        frame.add(numberLabel, BorderLayout.NORTH);

        // Arrays
        lists = new JList[numberOfPlayers];
        listModels = new DefaultListModel[numberOfPlayers];
        currentNumbers = new int[numberOfPlayers];

        JPanel listPanel = new JPanel(new GridLayout(1, numberOfPlayers));

        for (int i = 0; i < numberOfPlayers; i++) {
            currentNumbers[i] = -1;

            listModels[i] = new DefaultListModel<>();
            lists[i] = new JList<>(listModels[i]);
            applyListFont(lists[i], new Font("Impact", Font.PLAIN, 48));

            JScrollPane scroll = new JScrollPane(lists[i]);

            JPanel column = new JPanel(new BorderLayout());

            JLabel nameLabel = new JLabel(players[i], SwingConstants.CENTER);
            nameLabel.setFont(new Font("Impact", Font.BOLD, 28));
            column.add(nameLabel, BorderLayout.NORTH);

            JButton rollBtn = new JButton("Roll");
            int index = i;

            rollBtn.addActionListener(e -> {
                try {
                    handleRoll(index);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });

            JPanel buttonRow = new JPanel();
            buttonRow.setLayout(new GridLayout(1, 1));
            buttonRow.add(rollBtn);

            column.add(scroll, BorderLayout.CENTER);
            column.add(buttonRow, BorderLayout.SOUTH);

            listPanel.add(column);
        }

        // Load save file
        frame.add(listPanel, BorderLayout.CENTER);
        frame.setVisible(true);
        for (int i = 0; i < FileHandler.getLineAmount(TheCollection.savedList); i++) {
            String collectable = FileHandler.returnLine(TheCollection.savedList, i);
            if (!collectable.isEmpty()) {
                String[] stats = collectable.split(",");
                if (Objects.equals(stats[0], "-1")) {
                    TheCollection.vetod.add(stats[1]);

                } else if (Objects.equals(stats[0], "discards")){
                    for (int j = 0; j < numberOfPlayers; j++) {
                        discardsPerPlayer[j] = Integer.parseInt(stats[j + 1]);
                    }

                } else {
                    listModels[Integer.parseInt(stats[0])].addElement(stats[1]);
                }
                TheCollection.species.remove(stats[1]);

            }
        }
        System.out.println(TheCollection.species);
    }

    private static void handleRoll(int playerIndex) throws IOException {
        int rolledNumber = random.nextInt(TheCollection.getSpecies().size()) + 1;
        String pokemonName = TheCollection.getSpecies().get(rolledNumber);
        for (int i = 0; i < players.length; i++) {
            if (players[i].equals(pokemonName)) {
            }
        }

        //Make the whole evo line
        StringBuilder wholeEvoLine = new StringBuilder();
        for (int i = 0; i < TheCollection.getPokemon().size(); i++) {
            if (TheCollection.getPokemon().get(i).getEvoLine().equals(pokemonName)) {
                assert false;
                wholeEvoLine.append(TheCollection.getPokemon().get(i).getName());
                wholeEvoLine.append("\n");
            }
        }


        numberLabel.setText(players[playerIndex] + " rolled: The " + pokemonName + " line");

        // Confirmation Window
        Object[] options = {
                "Save Pokémon",
                "Discard (Remaining: " + discardsPerPlayer[playerIndex] + ")",
                "Cancel"
        };

        Object[] discardOptions = {
                "Put back into pool",
                "Fuck you I'm Vetoing this shit",
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
            listModels[playerIndex].addElement(pokemonName);
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

    private static void applyListFont(JList<Integer> list, Font font) {
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
                return label;
            }
        });
    }

    static String getCSV() {
        String csv = "";
        for (int i = 0; i < numberOfPlayers; i++) {
            for (int j = 0; j < listModels[i].size(); j++) {
                csv += i + "," +listModels[i].get(j) + "\n";
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
        return csv;
    }
}
