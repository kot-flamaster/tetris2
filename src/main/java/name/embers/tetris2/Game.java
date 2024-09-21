package name.embers.tetris2;

import javax.swing.*;
import java.awt.*;

public class Game extends JFrame {

    JLabel statusbar;

    public Game() {
        initUI();
    }

    private void initUI() {
        statusbar = new JLabel(" 0");
        //statusbar.setForeground(Color.WHITE); // Встановлюємо білий колір тексту статусбару
        add(statusbar, BorderLayout.SOUTH);
        Board board = new Board(this);
        add(board);
        board.start();

        setTitle("Тетріс");
        setSize(400, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public JLabel getStatusBar() {
        return statusbar;
    }

    public static void main(String[] args) {
        Game game = new Game();
        game.setVisible(true);
    }
}