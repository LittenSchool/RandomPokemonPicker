import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class RandomNumberGUI {
    private JFrame frame;
    private JLabel numberLabel;
    private JButton rollButton, saveButton, discardButton;
    private JList[] lists;
    private DefaultListModel<String>[] listModels;
    private int currentNumber = -1;
    private Random random = new Random();

    public RandomNumberGUI() {
        frame = new JFrame("Random Pokemon Roller");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);
        frame.setLayout(new BorderLayout());

        // Top panel with roll + display
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout());
        numberLabel = new JLabel("Roll to Generate Player");
        numberLabel.setFont(new Font("Impact", Font.BOLD, 36));
        rollButton = new JButton("Roll Random Number");

        topPanel.add(numberLabel);
        topPanel.add(rollButton);

        frame.add(topPanel, BorderLayout.NORTH);

        // Center panel with 7 lists
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new GridLayout(1, 7));

        lists = new JList[7];
        listModels = new DefaultListModel[7];

        for (int i = 0; i < 7; i++) {
            listModels[i] = new DefaultListModel<>();
            lists[i] = new JList<>(listModels[i]);

            // Set custom font here
            applyListFont(lists[i], new Font("Impact", Font.PLAIN, 64));

            JScrollPane scroll = new JScrollPane(lists[i]);
            JPanel column = new JPanel(new BorderLayout());
            String[] options = {"Jason", "Jiveen", "Jacob", "Isabelle", "Calum", "Karesz", "Viktors"};
            column.add(new JLabel(options[i], SwingConstants.CENTER), BorderLayout.NORTH);
            column.add(scroll, BorderLayout.CENTER);
            listPanel.add(column);
        }


        frame.add(listPanel, BorderLayout.CENTER);

        // Bottom buttons for save + discard
        JPanel bottomPanel = new JPanel();
        saveButton = new JButton("Save to Player...");
        discardButton = new JButton("Discard");

        bottomPanel.add(saveButton);
        bottomPanel.add(discardButton);

        frame.add(bottomPanel, BorderLayout.SOUTH);

        addListeners();

        frame.setVisible(true);
    }

    private void addListeners() {
        rollButton.addActionListener(e -> {
            currentNumber = random.nextInt(1022) + 1;
            numberLabel.setText("Rolled: " + TheCollection.pokemon.get(currentNumber).name);
        });

        discardButton.addActionListener(e -> {
            currentNumber = -1;
            numberLabel.setText("Roll to Generate Pokemon");
        });

        saveButton.addActionListener(e -> {
            if (currentNumber == -1) return;

            // Popup to choose list
            String[] options = {"Jason", "Jiveen", "Jacob", "Isabelle", "Calum", "Karesz", "Viktors"};
            int choice = JOptionPane.showOptionDialog(frame,
                    "Choose a Player to save to:",
                    "Save Pokemon",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    options,
                    options[0]);

            if (choice >= 0 && choice < 7) {
                listModels[choice].addElement(TheCollection.pokemon.get(currentNumber).name);
                currentNumber = -1;
                numberLabel.setText("Roll to Generate Pokemon");
            }
        });
    }
    private void applyListFont(JList<Integer> list, Font font) {
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

}
