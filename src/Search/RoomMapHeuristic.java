package Search;

public class RoomMapHeuristic implements IHeuristic {
    @Override
    public double getHeuristic(IProblemState problemState) {
        if (problemState instanceof RoomMapState) {
            RoomMapState s = (RoomMapState) problemState;
            RoomMap r = (RoomMap) s.getProblem();
            return (double) (r.getNumberOfPositions() - s.getSeen().size()) / (r.getRoomMap().length + r.getRoomMap()[0].length - 2);
        } else return Double.MAX_VALUE / 2;
    }
}
