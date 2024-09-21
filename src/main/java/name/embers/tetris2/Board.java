package name.embers.tetris2;

import java.awt.*;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.JLabel;

public class Board extends JPanel implements ActionListener {

    final int BoardWidth = 10;
    final int BoardHeight = 22;

    Timer timer;
    boolean isFallingFinished = false;
    boolean isStarted = false;
    boolean isPaused = false;
    int numLinesRemoved = 0;
    int curX = 0;
    int curY = 0;
    JLabel statusbar;
    Shape curPiece;
    Tetrominoes[] board;

    private Shape nextPiece; // Додаємо це поле

    private NextPiecePanel nextPiecePanel;


    public final int totalAnimationSteps = 6; // 3 миготіння * 2 стани (видимий/невидимий)

    public Board(Game parent) {
        setFocusable(true);
        curPiece = new Shape();
        timer = new Timer(500, this);
        timer.start();

        statusbar = parent.getStatusBar();
        board = new Tetrominoes[BoardWidth * BoardHeight];
        clearBoard();
        addKeyListener(new TAdapter());

        // Встановлюємо темний фон
        setBackground(new Color(30, 30, 30));

        animationTimer = new Timer(100, new AnimationListener());
    }

    class AnimationListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            animationStep++;
            if (animationStep >= totalAnimationSteps) { // Кількість кроків анімації
                animationTimer.stop();
                deleteFullLines();
                isAnimating = false;
                timer.start();
            }
            repaint();
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (isAnimating) {
            return;
        }
        if (isFallingFinished) {
            isFallingFinished = false;
            newPiece();
        } else {
            oneLineDown();
        }
    }

    int squareWidth() { return (int) getSize().getWidth() / BoardWidth; }
    int squareHeight() { return (int) getSize().getHeight() / BoardHeight; }
    Tetrominoes shapeAt(int x, int y) { return board[(y * BoardWidth) + x]; }


    public void startNewGame() {
        timer.stop();
        isStarted = false;
        isFallingFinished = false;
        numLinesRemoved = 0;
        clearBoard();
        statusbar.setText(String.valueOf(numLinesRemoved));
        start();
    }
    public void start() {
        isStarted = true;
        isPaused = false;
        isFallingFinished = false;
        numLinesRemoved = 0;
        clearBoard();

        nextPiece = new Shape();      // Ініціалізуємо наступну фігуру
        nextPiece.setRandomShape();

        newPiece();                   // Створюємо першу фігуру
        timer.start();
        statusbar.setText(String.valueOf(numLinesRemoved));
    }

    private void pause() {
        if (!isStarted)
            return;

        isPaused = !isPaused;
        if (isPaused) {
            timer.stop();
            statusbar.setText("Пауза");
        } else {
            timer.start();
            statusbar.setText(String.valueOf(numLinesRemoved));
        }
        repaint();
    }

    private int calculateDropPosition() {
        int dropY = curY;
        while (dropY > 0) {
            if (!canMove(curPiece, curX, dropY - 1))
                break;
            --dropY;
        }
        return dropY;
    }

    public void paint(Graphics g) {
        super.paint(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        Dimension size = getSize();
        int boardTop = (int) size.getHeight() - BoardHeight * squareHeight();

        // Малюємо фіксовані фігури та анімацію
        for (int i = 0; i < BoardHeight; ++i) {
            for (int j = 0; j < BoardWidth; ++j) {
                Tetrominoes shape = shapeAt(j, BoardHeight - i - 1);
                if (shape != Tetrominoes.NoShape) {
                    int x = j * squareWidth();
                    int y = boardTop + i * squareHeight();

                    if (isAnimating && fullLines.contains(BoardHeight - i - 1)) {
                        drawAnimatedSquare(g2d, x, y, shape);
                    } else {
                        drawSquare(g2d, x, y, shape);
                    }
                }
            }
        }

        // Малюємо поточну падаючу фігуру, якщо не в режимі анімації
        if (curPiece.getShape() != Tetrominoes.NoShape && !isAnimating) {
            int dropY = calculateDropPosition();

            // Малюємо проекцію фігури
            for (int i = 0; i < 4; ++i) {
                int x = curX + curPiece.x(i);
                int y = dropY - curPiece.y(i);
                drawGhostSquare(g2d, x * squareWidth(),
                        boardTop + (BoardHeight - y - 1) * squareHeight(),
                        curPiece.getShape());
            }

            // Малюємо поточну фігуру
            for (int i = 0; i < 4; ++i) {
                int x = curX + curPiece.x(i);
                int y = curY - curPiece.y(i);
                drawSquare(g2d, x * squareWidth(),
                        boardTop + (BoardHeight - y - 1) * squareHeight(),
                        curPiece.getShape());
            }
        }
    }

    private final Color[] colors = {
            new Color(30, 30, 30),        // Фон (темно-сірий)
            new Color(233, 84, 84),       // Червоний (Z-образна фігура)
            new Color(115, 194, 118),     // Зелений (S-образна фігура)
            new Color(102, 153, 204),     // Синій (Лінійна фігура)
            new Color(178, 102, 255),     // Фіолетовий (T-образна фігура)
            new Color(255, 230, 102),     // Жовтий (Квадратна фігура)
            new Color(255, 167, 92),      // Оранжевий (L-образна фігура)
            new Color(102, 217, 255)      // Блакитний (Перевернута L-образна фігура)
    };

    private void drawAnimatedSquare(Graphics g, int x, int y, Tetrominoes shape) {
        boolean isVisible = (animationStep % 2) == 0;

        Color color = colors[shape.ordinal()];

        Graphics2D g2d = (Graphics2D) g;
        Composite originalComposite = g2d.getComposite();

        if (isVisible) {
            // Використовуємо початковий колір з прозорістю
            float alpha = 0.6f; // Значення від 0.0f до 1.0f
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            drawSquare(g2d, x, y, color);
        } else {
            // Малюємо квадрат кольором фону
            g.setColor(getBackground());
            g.fillRect(x, y, squareWidth(), squareHeight());
        }

        // Відновлюємо оригінальний Composite
        g2d.setComposite(originalComposite);
    }


    private void drawGhostSquare(Graphics g, int x, int y, Tetrominoes shape) {
        Color color = colors[shape.ordinal()];

        // Створюємо прозорий варіант кольору
        int alpha = 100; // Значення альфа-каналу (0-255)
        Color ghostColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);

        Graphics2D g2d = (Graphics2D) g;
        Composite originalComposite = g2d.getComposite();

        // Встановлюємо прозорість для проекції
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, ghostColor.getAlpha() / 255f));

        // Малюємо проекцію фігури
        drawSquare(g2d, x, y, ghostColor);

        // Відновлюємо оригінальний Composite
        g2d.setComposite(originalComposite);
    }



    private void dropDown() {
        int newY = curY;
        while (newY > 0) {
            if (!tryMove(curPiece, curX, newY - 1))
                break;
            --newY;
        }
        pieceDropped();
    }

    private void oneLineDown() {
        if (!tryMove(curPiece, curX, curY - 1))
            pieceDropped();
    }

    private void clearBoard() {
        for (int i = 0; i < BoardHeight * BoardWidth; ++i)
            board[i] = Tetrominoes.NoShape;
    }

    private void pieceDropped() {
        for (int i = 0; i < 4; ++i) {
            int x = curX + curPiece.x(i);
            int y = curY - curPiece.y(i);
            board[(y * BoardWidth) + x] = curPiece.getShape();
        }

        removeFullLines();

        if (!isFallingFinished)
            newPiece();
    }

    private void newPiece() {
        curPiece = nextPiece;         // Поточна фігура стає наступною
        nextPiece = new Shape();      // Генеруємо нову наступну фігуру
        nextPiece.setRandomShape();

        if (nextPiecePanel != null) {
            nextPiecePanel.setNextPiece(nextPiece);
        }

        curX = BoardWidth / 2 + 1;
        curY = BoardHeight - 1 + curPiece.minY();

        if (!tryMove(curPiece, curX, curY)) {
            curPiece.setShape(Tetrominoes.NoShape);
            timer.stop();
            isStarted = false;
            statusbar.setText("Кінець гри");
        }
    }
    private boolean canMove(Shape piece, int newX, int newY) {
        for (int i = 0; i < 4; ++i) {
            int x = newX + piece.x(i);
            int y = newY - piece.y(i);
            if (x < 0 || x >= BoardWidth || y < 0 || y >= BoardHeight)
                return false;
            if (shapeAt(x, y) != Tetrominoes.NoShape)
                return false;
        }
        return true;
    }

    private boolean tryMove(Shape newPiece, int newX, int newY) {
        for (int i = 0; i < 4; ++i) {
            int x = newX + newPiece.x(i);
            int y = newY - newPiece.y(i);
            if (x < 0 || x >= BoardWidth || y < 0 || y >= BoardHeight)
                return false;
            if (shapeAt(x, y) != Tetrominoes.NoShape)
                return false;
        }

        curPiece = newPiece;
        curX = newX;
        curY = newY;
        repaint();
        return true;
    }

    private final Timer animationTimer;
    private boolean isAnimating = false;
    private int animationStep = 0;
    private final java.util.List<Integer> fullLines = new ArrayList<>();

    private void removeFullLines() {
        fullLines.clear();
        for (int i = BoardHeight - 1; i >= 0; --i) {
            boolean lineIsFull = true;

            for (int j = 0; j < BoardWidth; ++j) {
                if (shapeAt(j, i) == Tetrominoes.NoShape) {
                    lineIsFull = false;
                    break;
                }
            }

            if (lineIsFull) {
                fullLines.add(i);
            }
        }

        if (!fullLines.isEmpty()) {
            isAnimating = true;
            animationStep = 0;
            timer.stop();
            animationTimer.start();
        }
    }

    private void deleteFullLines() {
        for (int index : fullLines) {
            for (int k = index; k < BoardHeight - 1; ++k) {
                for (int j = 0; j < BoardWidth; ++j)
                    board[(k * BoardWidth) + j] = shapeAt(j, k + 1);
            }
            for (int j = 0; j < BoardWidth; ++j)
                board[((BoardHeight - 1) * BoardWidth) + j] = Tetrominoes.NoShape;
        }

        numLinesRemoved += fullLines.size();
        statusbar.setText(String.valueOf(numLinesRemoved));
        fullLines.clear();
        repaint();
    }


    private void drawSquare(Graphics g, int x, int y, Tetrominoes shape) {
        Color color = colors[shape.ordinal()];
        drawSquare(g, x, y, color);
    }

    private void drawSquare(Graphics g, int x, int y, Color color) {

        // Додаємо тінь
        g.setColor(color.darker().darker());
        g.fillRect(x + 3, y + 3, squareWidth() - 2, squareHeight() - 2);

        // Малюємо саму фігуру
        g.setColor(color);
        g.fillRect(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2);

        // Додаємо світіння
        g.setColor(color.brighter());
        g.drawLine(x, y + squareHeight() - 1, x, y);
        g.drawLine(x, y, x + squareWidth() - 1, y);

        g.setColor(color.darker());
        g.drawLine(x + 1, y + squareHeight() - 1,
                x + squareWidth() - 1, y + squareHeight() - 1);
        g.drawLine(x + squareWidth() - 1, y + squareHeight() - 1,
                x + squareWidth() - 1, y + 1);
    }


    // **Додаємо внутрішній клас TAdapter для обробки вводу**

    class TAdapter extends KeyAdapter {
        public void keyPressed(KeyEvent e) {
            int keycode = e.getKeyCode();

            if (keycode == KeyEvent.VK_N) {
                startNewGame();
                return;
            }

            if (!isStarted || curPiece.getShape() == Tetrominoes.NoShape || isAnimating) {
                return;
            }



            if (keycode == 'p' || keycode == 'P') {
                pause();
                return;
            }



            if (isPaused)
                return;

            switch (keycode) {
                case KeyEvent.VK_LEFT:
                    tryMove(curPiece, curX - 1, curY);
                    break;
                case KeyEvent.VK_RIGHT:
                    tryMove(curPiece, curX + 1, curY);
                    break;
                case KeyEvent.VK_DOWN:
                    tryMove(curPiece.rotateRight(), curX, curY);
                    break;
                case KeyEvent.VK_UP:
                    tryMove(curPiece.rotateLeft(), curX, curY);
                    break;
                case KeyEvent.VK_SPACE:
                    dropDown();
                    break;
                case 'd':
                case 'D':
                    oneLineDown();
                    break;
            }
        }
    }
    public void setNextPiecePanel(NextPiecePanel panel) {
        this.nextPiecePanel = panel;
    }

    public Shape getNextPiece() {
        return nextPiece;
    }

    public Color[] getColors() {
        return colors;
    }
}
