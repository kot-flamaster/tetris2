package name.embers.tetris2;

import java.awt.*;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.event.*;
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

    public Board(Game parent) {
        setFocusable(true);
        curPiece = new Shape();
        timer = new Timer(400, this);
        timer.start();

        statusbar = parent.getStatusBar();
        board = new Tetrominoes[BoardWidth * BoardHeight];
        clearBoard();
        addKeyListener(new TAdapter());

        // Встановлюємо темний фон
        setBackground(new Color(30, 30, 30));
    }

    public void actionPerformed(ActionEvent e) {
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
        newPiece();
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

        Dimension size = getSize();
        int boardTop = (int) size.getHeight() - BoardHeight * squareHeight();

        // Малюємо фіксовані фігури
        for (int i = 0; i < BoardHeight; ++i) {
            for (int j = 0; j < BoardWidth; ++j) {
                Tetrominoes shape = shapeAt(j, BoardHeight - i - 1);
                if (shape != Tetrominoes.NoShape)
                    drawSquare(g, j * squareWidth(), boardTop + i * squareHeight(), shape);
            }
        }

        if (curPiece.getShape() != Tetrominoes.NoShape) {
            int dropY = calculateDropPosition();

            // Малюємо проекцію фігури
            for (int i = 0; i < 4; ++i) {
                int x = curX + curPiece.x(i);
                int y = dropY - curPiece.y(i);
                drawGhostSquare(g, x * squareWidth(),
                        boardTop + (BoardHeight - y - 1) * squareHeight(),
                        curPiece.getShape());
            }

            // Малюємо поточну падаючу фігуру
            for (int i = 0; i < 4; ++i) {
                int x = curX + curPiece.x(i);
                int y = curY - curPiece.y(i);
                drawSquare(g, x * squareWidth(),
                        boardTop + (BoardHeight - y - 1) * squareHeight(),
                        curPiece.getShape());
            }
        }
    }

    private void drawGhostSquare(Graphics g, int x, int y, Tetrominoes shape) {
        Color colors[] = {
                new Color(0, 0, 0, 50),
                new Color(204, 102, 102, 50),
                new Color(102, 204, 102, 50),
                new Color(102, 102, 204, 50),
                new Color(204, 204, 102, 50),
                new Color(204, 102, 204, 50),
                new Color(102, 204, 204, 50),
                new Color(218, 170, 0, 50)
        };

        Color color = colors[shape.ordinal()];

        Graphics2D g2d = (Graphics2D) g;
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));

        g.setColor(color);
        g.fillRect(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2);

        // Відновлюємо прозорість
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
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
        curPiece.setRandomShape();
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

    private void removeFullLines() {
        int numFullLines = 0;

        for (int i = BoardHeight - 1; i >= 0; --i) {
            boolean lineIsFull = true;

            for (int j = 0; j < BoardWidth; ++j) {
                if (shapeAt(j, i) == Tetrominoes.NoShape) {
                    lineIsFull = false;
                    break;
                }
            }

            if (lineIsFull) {
                ++numFullLines;
                for (int k = i; k < BoardHeight - 1; ++k) {
                    for (int j = 0; j < BoardWidth; ++j)
                        board[(k * BoardWidth) + j] = shapeAt(j, k + 1);
                }
            }
        }

        if (numFullLines > 0) {
            numLinesRemoved += numFullLines;
            statusbar.setText(String.valueOf(numLinesRemoved));
            isFallingFinished = true;
            curPiece.setShape(Tetrominoes.NoShape);
            repaint();
        }
    }

    private void drawSquare(Graphics g, int x, int y, Tetrominoes shape) {
        Color colors[] = {
                new Color(0, 0, 0),
                new Color(204, 102, 102),
                new Color(102, 204, 102),
                new Color(102, 102, 204),
                new Color(204, 204, 102),
                new Color(204, 102, 204),
                new Color(102, 204, 204),
                new Color(218, 170, 0)
        };

        Color color = colors[shape.ordinal()];

        g.setColor(color);
        g.fillRect(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2);

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

            if (!isStarted || curPiece.getShape() == Tetrominoes.NoShape) {
                return;
            }

            int keycode = e.getKeyCode();

            if (keycode == 'p' || keycode == 'P') {
                pause();
                return;
            }

            if (keycode == 'n' || keycode == 'N') {
                startNewGame();
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
}
