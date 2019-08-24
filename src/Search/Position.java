package Search;

public class Position {
    private int y;
    private int x;

    /**
     * Default constructor
     * initiated as (-1,-1)
     */
    public Position() {
        y = -1;
        x = -1;
    }

    /**
     * constructor
     */
    public Position(int y, int x) {
        this.y = y;
        this.x = x;
    }

    /**
     * copy constructor
     */
    public Position(Position position) {
        if (position == null) {
            throw new NullPointerException("the position is null");
        }
        y = position.y;
        x = position.x;
    }

    /**
     * get y index
     *
     * @return y
     */
    public int getY() {
        return y;
    }

    /**
     * get x index
     *
     * @return x
     */
    public int getX() {
        return x;
    }

    /**
     * set y
     *
     * @param y - the new y value
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * set x
     *
     * @param x - the new x value
     */
    public void setX(int x) {
        this.x = x;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position)) return false;
        Position position = (Position) o;
        return y == position.y &&
                x == position.x;
    }

    public boolean equals(int x, int y) {
        return this.x == x && this.y == y;
    }

    @Override
    public int hashCode() {
        return (((y + x) * (y + x + 1)) / 2) + y;
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}
