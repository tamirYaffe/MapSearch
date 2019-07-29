package Search;

import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

public class RoomMapNonAdmissibleHeuristic implements IHeuristic {
    @Override
    public double getHeuristic(IProblemState problemState) {
        if (problemState instanceof RoomMapState) {
            RoomMapState s = (RoomMapState) problemState;
            RoomMap r = (RoomMap) s.getProblem();
            //double h1=(r.totalRoomWeight - s.getSeenTotalWeight()) / r.maxPositionWeight ;
            double h=computeH2(r,s);
//            return (double) (r.getNumberOfPositions() - s.getSeen().size()) / (r.getRoomMap().length + r.getRoomMap()[0].length - 2);
            return h;
        } else return Double.MAX_VALUE / 2;
    }

    private double computeH2(RoomMap r, RoomMapState s) {
        double h=0;
        TreeMap<Position, HashSet<Position>> watchedDictionary=r.getWatchedDictionary();
        for (Map.Entry<Position, HashSet<Position>> entry : watchedDictionary.entrySet()) {
            Position nextRarePosition=entry.getKey();
            HashSet<Position> positions=entry.getValue();
            double weight=(double) 1/positions.size();
            if(weight>=0.2 ){
                if(!s.getSeen().contains(nextRarePosition))
                    h+= minDistance(positions,s.getPosition());
            }
            else
                break;
        }
        return h;
    }

    private double minDistance(HashSet<Position> positions, Position currPosition) {
        Position distantPosition=null;
        double minDistance=Double.MAX_VALUE;
        for(Position position : positions){
            double distance=euclideanDistance(position,currPosition);
            if(distance<minDistance)
                minDistance=distance;
        }
        return minDistance;
    }

    private double euclideanDistance(Position p1, Position p2){
        return Math.sqrt(Math.abs(p1.getY()-p2.getY())+Math.abs(p1.getX()-p2.getX()));
    }
}
