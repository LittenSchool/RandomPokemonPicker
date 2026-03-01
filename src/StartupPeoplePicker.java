import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Random;

public class StartupPeoplePicker {
    private static JFrame frame;
    private static JFrame frame2;
    private static JLabel title;
    public static boolean newSave = false;
    static Random rand = new Random();
    private static JColorChooser colorChooser = new JColorChooser();

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

        JButton colourButton = new JButton("Colour");
        frame.add(colourButton, BorderLayout.WEST);
        colourButton.addActionListener(e -> {
            frame2 = new JFrame("Colour");
            frame2.setLocationRelativeTo(frame);
            frame2.add(colorChooser, BorderLayout.CENTER);
            frame2.setSize(600, 400);
            frame2.setVisible(true);
        });

        JButton confirm = new JButton("Confirm");
        confirm.addActionListener(e -> {
            Color color = new Color(0,0,0);
            if (colorChooser.getColor() != Color.WHITE) {
                color = colorChooser.getColor();
            }
            TheCollection.addPlayer(enterBox.getText());
            System.out.println(TheCollection.colourToHex(color));
            TheCollection.addColour(TheCollection.colourToHex(color));
            enterBox.setText("");
        });
        frame.add(confirm, BorderLayout.SOUTH);

        JButton proceed = new JButton("Start The Game");
        proceed.addActionListener(e -> {
            RandomNumberGUI.run();
            frame.dispose();
            frame2.dispose();
        });
        frame.add(proceed, BorderLayout.EAST);
        frame.setVisible(true);

    }

}
