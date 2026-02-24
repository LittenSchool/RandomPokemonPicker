import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Objects;

public class TradeMenu {
    private static JFrame frame;
    private static JLabel title;
    private static String bufferedTradePokemon;
    private static int bufferedTradePlayerID;

    public static void run(int playerID, String tradePokemon) {
        frame = new JFrame("Trade Menu");
        frame.setSize(1000, 100);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());



        if (!RandomNumberGUI.getIsTradeMode()) {
            title = new JLabel("Does " + RandomNumberGUI.getPlayer(playerID) + " wish to trade " + tradePokemon + "?");
            title.setFont(new Font("Arial", Font.PLAIN, 20));
            title.setHorizontalAlignment(SwingConstants.CENTER);
            frame.add(title, BorderLayout.NORTH);

            JButton confirm = new JButton("Yes");
            confirm.addActionListener(e -> {
                bufferedTradePokemon = tradePokemon;
                bufferedTradePlayerID = playerID;
                RandomNumberGUI.setIsTradeMode(!RandomNumberGUI.getIsTradeMode());
                frame.dispose();
            });
            frame.add(confirm, BorderLayout.EAST);

            JButton deny = new JButton("No");
            deny.addActionListener(e -> {
                frame.dispose();
            });
            frame.add(deny, BorderLayout.WEST);

            frame.setVisible(true);

        } else {
            if (bufferedTradePlayerID == playerID) {
                frame.dispose();
                System.out.println("wheey");
            } else {
                title = new JLabel("Does " + RandomNumberGUI.getPlayer(bufferedTradePlayerID) + " wish to trade " + bufferedTradePokemon + " for " + RandomNumberGUI.getPlayer(playerID) + "'s " + tradePokemon + "?");
                title.setFont(new Font("Arial", Font.PLAIN, 20));
                title.setHorizontalAlignment(SwingConstants.CENTER);
                frame.add(title, BorderLayout.NORTH);

                JButton confirm = new JButton("Yes");
                confirm.addActionListener(e -> {
                    RandomNumberGUI.trade(bufferedTradePokemon,tradePokemon,bufferedTradePlayerID,playerID);
                    RandomNumberGUI.setIsTradeMode(!RandomNumberGUI.getIsTradeMode());
                    frame.dispose();
                });
                frame.add(confirm, BorderLayout.EAST);

                JButton deny = new JButton("No");
                deny.addActionListener(e -> {
                    RandomNumberGUI.setIsTradeMode(!RandomNumberGUI.getIsTradeMode());
                    frame.dispose();
                });
                frame.add(deny, BorderLayout.WEST);

                frame.setVisible(true);
            }
        }





    }

}
