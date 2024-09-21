package name.embers.tetris2;

import javax.swing.*;
import java.awt.*;

public class Game extends JFrame {

    JLabel statusbar;
    NextPiecePanel nextPiecePanel;

    public Game() {
        initUI();
    }

    private void initUI() {
        statusbar = new JLabel(" 0");
        statusbar.setForeground(Color.WHITE);

        // Використовуємо панель для більш гнучкого компонування
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(30, 30, 30));

        Board board = new Board(this);
        panel.add(board, BorderLayout.CENTER);

        nextPiecePanel = new NextPiecePanel(board.getNextPiece(), board.getColors());
        panel.add(nextPiecePanel, BorderLayout.EAST);

        board.setNextPiecePanel(nextPiecePanel);

        add(panel);
        add(statusbar, BorderLayout.SOUTH);

        board.start();

        setTitle("Тетріс");
        setSize(300, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        nextPiecePanel.setBorder(BorderFactory.createTitledBorder("Наступна фігура"));

        nextPiecePanel.setBackground(new Color(30, 30, 30));
    }


    public JLabel getStatusBar() {
        return statusbar;
    }

    public static void main(String[] args) {
        Game game = new Game();
        game.setVisible(true);
    }
}