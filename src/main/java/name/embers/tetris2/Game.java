package name.embers.tetris2;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;

public class Game extends JFrame {

    private JLabel statusbar;
    private NextPiecePanel nextPiecePanel;

    public Game() {
        initUI();
    }

    private void initUI() {
        // Ініціалізація статусбару
        statusbar = new JLabel(" 0");
        statusbar.setForeground(Color.WHITE);

        // Створюємо панель для ігрового поля та наступної фігури
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(30, 30, 30));

        // Створюємо ігрове поле
        Board board = new Board(this);
        panel.add(board, BorderLayout.CENTER);

        // Створюємо панель для наступної фігури
        nextPiecePanel = new NextPiecePanel(board.getNextPiece(), board.getColors());
        nextPiecePanel.setBorder(BorderFactory.createTitledBorder("Наступна фігура"));
        panel.add(nextPiecePanel, BorderLayout.EAST);

        // Передаємо посилання на панель до ігрового поля
        board.setNextPiecePanel(nextPiecePanel);

        // Додаємо панель до вікна
        add(panel, BorderLayout.CENTER);
        add(statusbar, BorderLayout.SOUTH);

        // Налаштування вікна
        setTitle("Тетріс");
        setSize(400, 500); // Збільшуємо розмір вікна для нових компонентів
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);


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
