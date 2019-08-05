package Search;

import rlforj.los.ILosAlgorithm;
import rlforj.los.ILosBoard;
import rlforj.math.Point2I;
import rlforj.util.BresenhamLine;

import java.util.List;
import java.util.Vector;

public class EightWayLos implements ILosAlgorithm {

    private Vector<Point2I> path;

    public boolean existsLineOfSight(ILosBoard b, int startX, int startY,
                                     int x1, int y1, boolean calculateProject)
    {
        int dx=startX-x1, dy=startY-y1;
        int adx=dx>0?dx:-dx, ady=dy>0?dy:-dy;
        int len=(adx>ady?adx:ady) + 1;//Max number of points on the path.

        if(calculateProject)
            path=new Vector<Point2I>(len);

        // array to store path.
        int[] px=new int[len], py=new int[len];

        //Start to finish path
        BresenhamLine.plot(startX, startY, x1, y1, px, py);

        boolean los=false;
        for(int i=0; i<len; i++) {
            if(calculateProject){
                path.add(new Point2I(px[i], py[i]));
            }
            if(px[i]==x1 && py[i]==y1) {
                los=true;
                break;
            }
            if(b.isObstacle(px[i], py[i]))
                break;
        }
        return los;
    }

    public List<Point2I> getProjectPath()
    {
        return path;
    }

}
