package Search;

public class PositionVertex {
    public enum TYPE {PRUNEABLE, UNPRUNNABLE}

    private Position position;
    private TYPE type;

    public PositionVertex(Position position, TYPE type) {
        this.position = position;
        this.type = type;
    }

    public Position getPosition() {
        return position;
    }

    public TYPE getType() {
        return type;
    }

    public void setType(TYPE type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PositionVertex)) return false;
        PositionVertex vertex = (PositionVertex) o;
        return position.equals(vertex.position) &&
                type == vertex.type;
    }

    @Override
    public int hashCode() {
        return position.hashCode();
    }

    @Override
    public String toString() {
        return position.toString() + " type: " + type;
    }
}
