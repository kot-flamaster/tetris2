package name.embers.tetris2;

public class Shape {
    private Tetrominoes pieceShape;
    private int coords[][];
    private int[][][] coordsTable;

    public Shape() {
        coords = new int[4][2];
        setShape(Tetrominoes.NoShape);
    }

    public void setShape(Tetrominoes shape) {
        coordsTable = new int[][][] {
                // Координати для кожної фігури
                { { 0, 0 }, { 0, 0 }, { 0, 0 }, { 0, 0 } }, // NoShape
                { { 0,-1 }, { 0, 0 }, {-1, 0 }, {-1, 1 } }, // ZShape
                { { 0,-1 }, { 0, 0 }, { 1, 0 }, { 1, 1 } }, // SShape
                { { 0,-1 }, { 0, 0 }, { 0, 1 }, { 0, 2 } }, // LineShape
                { {-1, 0 }, { 0, 0 }, { 1, 0 }, { 0, 1 } }, // TShape
                { { 0, 0 }, { 1, 0 }, { 0, 1 }, { 1, 1 } }, // SquareShape
                { {-1,-1 }, { 0,-1 }, { 0, 0 }, { 0, 1 } }, // LShape
                { { 1,-1 }, { 0,-1 }, { 0, 0 }, { 0, 1 } }  // MirroredLShape
        };

        for (int i = 0; i < 4 ; i++) {
            for (int j = 0; j < 2; ++j) {
                coords[i][j] = coordsTable[shape.ordinal()][i][j];
            }
        }
        pieceShape = shape;
    }

    // Методи для отримання координат
    public int x(int index) { return coords[index][0]; }
    public int y(int index) { return coords[index][1]; }
    public Tetrominoes getShape() { return pieceShape; }

    // Поворот фігури
    public Shape rotateLeft() {
        if (pieceShape == Tetrominoes.SquareShape)
            return this;

        Shape result = new Shape();
        result.pieceShape = pieceShape;

        for (int i = 0; i < 4; ++i) {
            result.coords[i][0] = y(i);
            result.coords[i][1] = -x(i);
        }
        return result;
    }

    public Shape rotateRight() {
        if (pieceShape == Tetrominoes.SquareShape)
            return this;

        Shape result = new Shape();
        result.pieceShape = pieceShape;

        for (int i = 0; i < 4; ++i) {
            result.coords[i][0] = -y(i);
            result.coords[i][1] = x(i);
        }
        return result;
    }
}
