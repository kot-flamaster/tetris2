package name.embers.tetris2;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

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
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Розраховуємо розмір кожного квадрата
        int squareSize = Math.min(getWidth(), getHeight()) / 4; // Використовуємо 4 квадрати для фігури

        // Обчислюємо розміри фігури (bounding box)
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (int i = 0; i < 4; i++) {
            minX = Math.min(minX, nextPiece.x(i));
            maxX = Math.max(maxX, nextPiece.x(i));
            minY = Math.min(minY, nextPiece.y(i));
            maxY = Math.max(maxY, nextPiece.y(i));
        }

        int shapeWidth = maxX - minX + 1;
        int shapeHeight = maxY - minY + 1;

        // Розраховуємо початкову позицію для малювання фігури
        int startX = (getWidth() - (shapeWidth * squareSize)) / 2 - (minX * squareSize);
        int startY = (getHeight() - (shapeHeight * squareSize)) / 2 - (minY * squareSize);

        // Визначаємо координати для фігури
        for (int i = 0; i < 4; i++) {
            int x = nextPiece.x(i) * squareSize + startX;
            int y = nextPiece.y(i) * squareSize + startY;

            drawSquare(g2d, x, y, nextPiece.getShape(), squareSize);
        }
    }

    private void drawSquare(Graphics g, int x, int y, Tetrominoes shape, int size) {
        Color color = colors[shape.ordinal()];
        g.setColor(color);
        g.fillRoundRect(x, y, size, size, 10, 10);

        // Додаємо контур
        g.setColor(color.brighter());
        g.drawRoundRect(x, y, size, size, 10, 10);
    }
}
