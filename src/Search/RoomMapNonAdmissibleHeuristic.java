package Search;

import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

import static Search.DistanceService.minDistance;

public class RoomMapNonAdmissibleHeuristic implements IHeuristic {
    @Override
    public double getHeuristic(IProblemState problemState) {
        if (problemState instanceof RoomMapState) {
            RoomMapState s = (RoomMapState) problemState;
            RoomMap r = (RoomMap) s.getProblem();
            return computeH2(r,s);
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

}
