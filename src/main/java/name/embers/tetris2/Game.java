package name.embers.tetris2;

import javax.swing.*;

public class Game extends JFrame {

    public Game() {
        initUI();
    }

    private void initUI() {
        Board board = new Board();
        add(board);
        setTitle("Тетріс");
        setSize(200, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        Game game = new Game();
        game.setVisible(true);
    }
}