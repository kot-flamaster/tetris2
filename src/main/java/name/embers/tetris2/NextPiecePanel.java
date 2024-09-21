package name.embers.tetris2;

import javax.swing.*;
import java.awt.*;

public class NextPiecePanel extends JPanel {

    private Shape nextPiece;
    private final Color[] colors;

    public NextPiecePanel(Shape nextPiece, Color[] colors) {
        this.nextPiece = nextPiece;
        this.colors = colors;
        setPreferredSize(new Dimension(100, 100));
        setBackground(new Color(30, 30, 30));
    }

    public void setNextPiece(Shape nextPiece) {
        this.nextPiece = nextPiece;
        repaint();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (nextPiece.getShape() != Tetrominoes.NoShape) {
            int squareWidth = getWidth() / 6;
            int squareHeight = getHeight() / 6;

            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            for (int i = 0; i < 4; ++i) {
                int x = (nextPiece.x(i) + 1) * squareWidth;
                int y = (nextPiece.y(i) + 1) * squareHeight;

                drawSquare(g2d, x, y, nextPiece.getShape());
            }
        }
    }

    private void drawSquare(Graphics g, int x, int y, Tetrominoes shape) {
        Color color = colors[shape.ordinal()];

        g.setColor(color);
        g.fillRoundRect(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2, 5, 5);

        g.setColor(color.brighter());
        g.drawRoundRect(x, y, squareWidth() - 1, squareHeight() - 1, 5, 5);
    }

    private int squareWidth() {
        return getWidth() / 6;
    }

    private int squareHeight() {
        return getHeight() / 6;
    }
}