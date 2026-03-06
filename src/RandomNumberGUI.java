import javax.swing.*;
import javax.swing.Timer;
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
    private static boolean isTradeMenuOpen = false;
    private static JLabel spriteLabel;

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

        spriteLabel = new JLabel();
        spriteLabel.setHorizontalAlignment(SwingConstants.CENTER);
        frame.add(spriteLabel, BorderLayout.SOUTH);

        lists = new JList[numberOfPlayers];
        listModels = new DefaultListModel[numberOfPlayers];
        currentNumbers = new int[numberOfPlayers];
        int gridLayout = numberOfPlayers;

        if (numberOfPlayers > 7) {
            
            gridLayout = Math.ceilDiv(numberOfPlayers,2);
        }

        JPanel listPanel = new JPanel(new GridLayout(0, gridLayout));

        for (int i = 0; i < numberOfPlayers; i++) {
            currentNumbers[i] = -1;

            listModels[i] = new DefaultListModel<>();
            lists[i] = new JList<>(listModels[i]);
            applyListFont(lists[i], new Font("Impact", Font.PLAIN, TheCollection.getFontSize()));

            int finalIndex = i;
            lists[i].addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int idx = lists[finalIndex].locationToIndex(e.getPoint());
                    if (idx != -1 && e.getButton() == MouseEvent.BUTTON1) {
                        onListElementClicked(finalIndex, listModels[finalIndex].getElementAt(idx));
                    }
                    else if (idx != -1 && e.getButton() == MouseEvent.BUTTON3 && !isTradeMenuOpen) {
                        isTradeMenuOpen = true;
                        TradeMenu.run(finalIndex, listModels[finalIndex].getElementAt(idx));
                    }
                }
            });

            JScrollPane scroll = new JScrollPane(lists[i]);

            JPanel column = new JPanel(new BorderLayout());

            JLabel nameLabel = new JLabel(players[i], SwingConstants.CENTER);
            nameLabel.setFont(new Font("Impact", Font.BOLD, Math.clamp(TheCollection.getFontSize(),0,60)));
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

        //System.out.println(TheCollection.species);
    }


    private static void handleRoll(int playerIndex) throws IOException {

        // Show the rolling animation window and get the final Pokémon
        String pokemonName = showRollingWindow(playerIndex);

        // Display result on main GUI
        numberLabel.setText(players[playerIndex] + " rolled: " + pokemonName);

        // Show confirmation
        finishRoll(playerIndex, pokemonName);
    }

    private static void finishRoll(int playerIndex, String pokemonName) throws IOException {

        String wholeEvoLine = getEvoLineFromSpecies(pokemonName);

        numberLabel.setText(players[playerIndex] + " rolled: The " + pokemonName + " line");

        Object[] options = {
                "Save Pokémon",
                "Discard (Remaining: " + discardsPerPlayer[playerIndex] + ")",
                "Cancel"
        };

        Object[] discardOptions = {
                "Put back into pool",
                "Fuck you I'm Vetoing this shit",
        };

        ImageIcon icon = loadPokemonIcon(pokemonName, TheCollection.getFontSize()*3);

        int choice = JOptionPane.showOptionDialog(
                frame,
                players[playerIndex] + " rolled: The " + pokemonName + " line" +
                        "\n\nThis line has the Pokémon:\n" +
                        wholeEvoLine +
                        "\nSave this Pokémon or discard it?",
                "Confirm Roll",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                icon,
                options,
                options[0]
        );

        if (choice == 0) {
            Pokemon temp = new Pokemon(pokemonName);
            temp.setColour(TheCollection.getPlayerColours().get(playerIndex));
            TheCollection.pokemonInPlay.add(temp);
            listModels[playerIndex].addElement(pokemonName);
            TheCollection.species.remove(pokemonName);
        }
        else if (choice == 1) {

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

                if (discardChoice == 1) {
                    TheCollection.vetod.add(pokemonName);
                    TheCollection.species.remove(pokemonName);
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

        TheCollection.saveList();
    }

    private static int speedConverter(int speed) {
        return Math.round((float) 1 /((float) speed /4000));
    }

    private static String showRollingWindow(int playerIndex) {

        final int[] numberOfTicks = {-1};
        Random rand = new Random();

        int timerDeceleration = 3;

        JDialog rollDialog = new JDialog(frame, players[playerIndex] + " Rolling...", true);
        rollDialog.setSize(TheCollection.getFontSize()*6, TheCollection.getFontSize()*6);
        rollDialog.setLocationRelativeTo(frame);
        rollDialog.setLayout(new BorderLayout());

        JLabel spriteLabel = new JLabel("", SwingConstants.CENTER);
        JLabel nameLabel = new JLabel("", SwingConstants.CENTER);

        nameLabel.setFont(new Font("Impact", Font.PLAIN, TheCollection.getFontSize()));

        rollDialog.add(spriteLabel, BorderLayout.CENTER);
        rollDialog.add(nameLabel, BorderLayout.SOUTH);

        final String[] result = new String[1];
        final int[] ticks = {0};
        final int[] timerVelocity = {rand.nextInt(100,400)};
        final boolean[] hasEnded = {false};


        Timer timer = new Timer(speedConverter(timerVelocity[0]), null);

        timer.addActionListener(e -> {

            String randomPokemon = TheCollection.getSpecies().get(random.nextInt(TheCollection.getSpecies().size()));

            nameLabel.setText(randomPokemon);

            ImageIcon icon = loadPokemonIcon(randomPokemon, TheCollection.getFontSize()*3);
            spriteLabel.setIcon(icon);
            timer.setDelay(speedConverter(timerVelocity[0]));
            timerVelocity[0] -= timerDeceleration;
            if (timerVelocity[0] <= 0) {
                timerVelocity[0] = 1;
            }
            System.out.println(timerVelocity[0]);


            ticks[0]++;

            if (hasEnded[0]) { // Makes the last rolled pokemon pop up last
                System.out.println("balls");
                timer.stop();
                rollDialog.dispose();

            } else if (timerVelocity[0] <= 1) { // how long the rolling lasts

                result[0] = randomPokemon;
                numberOfTicks[0] = ticks[0];
                if (random.nextInt(10) == 0) {
                    hasEnded[0] = false;
                } else {hasEnded[0] = true;}


            }
        });

        timer.start();
        rollDialog.setVisible(true);

        return result[0];
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
                    // Alternating colour
                        if (index%2 == 1) {
                            label.setBackground(Color.decode("#dfdfdf"));
                        } else {
                            label.setBackground(Color.WHITE);
                        }

                        if (index%10 == 9) {
                            label.setBackground(Color.decode("#ffdddd"));
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
        ImageIcon icon = loadPokemonIcon(value, TheCollection.getFontSize()*3);
        Object[] option = {"Okay"};
        JOptionPane.showOptionDialog(
                frame,
                players[playerIndex] + " selected the " + value + " line" +
                        "\nThis line has the Pokémon:\n" +
                        getEvoLineFromSpecies(value),
                "Confirm Roll",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                icon,
                option,
                option[0]

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

        String cacheKey = pokemonName + "_" + TheCollection.getFontSize();

        if (iconCache.containsKey(cacheKey)) {
            return iconCache.get(cacheKey);
        }

        if (imageFolderPath == null || imageFolderPath.isEmpty())
            return null;

        String filePath = imageFolderPath + pokemonName + ".png";

        ImageIcon icon = new ImageIcon(filePath);

        if (icon.getIconWidth() <= 0) {
            iconCache.put(cacheKey, null);
            return null;
        }

        Image scaled = icon.getImage().getScaledInstance(TheCollection.getFontSize(), TheCollection.getFontSize(), Image.SCALE_SMOOTH);
        ImageIcon result = new ImageIcon(scaled);

        iconCache.put(cacheKey, result);
        return result;
    }



    private static ImageIcon loadPokemonIcon(String pokemonName, int size) {

        String cacheKey = pokemonName + "_" + size;

        if (iconCache.containsKey(cacheKey)) {
            return iconCache.get(cacheKey);
        }

        if (imageFolderPath == null || imageFolderPath.isEmpty())
            return null;

        String filePath = imageFolderPath + pokemonName + ".png";

        ImageIcon icon = new ImageIcon(filePath);

        if (icon.getIconWidth() <= 0) {
            iconCache.put(cacheKey, null);
            return null;
        }

        Image scaled = icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
        ImageIcon result = new ImageIcon(scaled);

        iconCache.put(cacheKey, result);
        return result;
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

    public static int getWindowWidth() {
        if (frame == null) {
            return -1;
        } else {
            return frame.getWidth();
        }
    }

    public static void toggleIsTradeMenuOpen() {
        isTradeMenuOpen = !isTradeMenuOpen;
        //System.out.println(isTradeMenuOpen);
    }

    //test 2

}
