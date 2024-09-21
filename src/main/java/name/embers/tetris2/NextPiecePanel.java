package name.embers.tetris2;

import java.awt.*;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class NextPiecePanel extends JPanel {

    private Shape nextPiece;
    private Color[] colors;

    public NextPiecePanel(Shape nextPiece, Color[] colors) {
        this.nextPiece = nextPiece;
        this.colors = colors;
        setPreferredSize(new Dimension(150, 150)); // Збільшуємо розмір панелі для кращого відображення
        setBackground(new Color(30, 30, 30)); // Відповідний фон
    }

    public void setNextPiece(Shape nextPiece) {
        this.nextPiece = nextPiece;
        repaint();
    }

    public void setColors(Color[] colors) {
        this.colors = colors;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (nextPiece == null || nextPiece.getShape() == Tetrominoes.NoShape) {
            return;
        }

        Graphics2D g2d = (Graphics2D) g;

        // Включаємо антиаліасинг для плавних країв
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Розраховуємо розмір кожного квадрата
        int squareSize = Math.min(getWidth(), getHeight()) / 4; // Використовуємо 4 квадрати для фігури
        int offsetX = (getWidth() - (squareSize * 4)) / 2;
        int offsetY = (getHeight() - (squareSize * 4)) / 2;

        // Визначаємо координати для фігури
        for (int i = 0; i < 4; i++) {
            int x = nextPiece.x(i) * squareSize + offsetX + squareSize;
            int y = nextPiece.y(i) * squareSize + offsetY + squareSize;

            drawSquare(g2d, x, y, nextPiece.getShape(), squareSize);
        }
    }

    private void drawSquare(Graphics g, int x, int y, Tetrominoes shape, int size) {
        Color color = colors[shape.ordinal()];
        g.setColor(color);
        g.fillRect(x, y, size, size);

        // Додаємо світіння
        g.setColor(color.brighter());
        g.drawLine(x, y + size - 1, x, y);
        g.drawLine(x, y, x + size - 1, y);

        // Додаємо тіні
        g.setColor(color.darker());
        g.drawLine(x + 1, y + size - 1, x + size - 1, y + size - 1);
        g.drawLine(x + size - 1, y + size - 1, x + size - 1, y + 1);
    }
}
