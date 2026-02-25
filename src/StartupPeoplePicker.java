import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Random;

public class StartupPeoplePicker {
    private static JFrame frame;
    private static JLabel title;
    public static boolean newSave = false;
    static Random rand = new Random();

    public static void run() {
        newSave = true;
        frame = new JFrame("Enter Players");
        frame.setSize(500, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        title = new JLabel("Enter Players");
        title.setFont(new Font("Impact", Font.BOLD, 32));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        frame.add(title, BorderLayout.NORTH);

        JTextField enterBox = new JTextField();
        enterBox.setFont(new Font("Arial", Font.BOLD, 32));
        frame.add(enterBox, BorderLayout.CENTER);

        JButton confirm = new JButton("Confirm");
        confirm.addActionListener(e -> {
            Color color = new Color(rand.nextInt(0xFFFFFF));
            TheCollection.addPlayer(enterBox.getText());
            System.out.println(color);
            TheCollection.addColour(TheCollection.colourToHex(color));
            enterBox.setText("");
        });
        frame.add(confirm, BorderLayout.SOUTH);

        JButton proceed = new JButton("Start The Game");
        proceed.addActionListener(e -> {
            RandomNumberGUI.run();
            frame.dispose();
        });
        frame.add(proceed, BorderLayout.EAST);
        frame.setVisible(true);

    }

}
