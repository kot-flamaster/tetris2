package name.embers.tetris2;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

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
    Shape curPiece;
    Tetrominoes[] board;

    public Board() {
        setFocusable(true);
        curPiece = new Shape();
        timer = new Timer(400, this);
        timer.start();

        board = new Tetrominoes[BoardWidth * BoardHeight];
        addKeyListener(new TAdapter());
        clearBoard();
    }

    public void actionPerformed(ActionEvent e) {
        if (isFallingFinished) {
            isFallingFinished = false;
            newPiece();
        } else {
            oneLineDown();
        }
    }

    // Інші необхідні методи: start(), pause(), paint(), dropDown(), oneLineDown(), clearBoard(), newPiece(), tryMove(), removeFullLines(), squareAt(), і т.д.

    class TAdapter extends KeyAdapter {
        public void keyPressed(KeyEvent e) {
            // Обробка натискань клавіш
        }
    }
}
