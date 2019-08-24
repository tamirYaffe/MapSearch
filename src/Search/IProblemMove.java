package Search;

public interface IProblemMove
{

    Position getNewPosition(int x, int y);

    Position getNewPosition(Position newPosition);

    double getCost();
}
