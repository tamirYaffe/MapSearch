package Search;

import rlforj.los.*;
import java.lang.Math;

public class VisualLineOfSightAdapter {
    private ILosAlgorithm los;

    public VisualLineOfSightAdapter(ILosAlgorithm los) {
        this.los = los;
    }

    public boolean existsLineOfSight(ILosBoard b, int startX, int startY, int x1, int y1) {
//        return los.existsLineOfSight(b, startX, startY, x1, y1, true);
        boolean los_exists = los.existsLineOfSight(b, startX, startY, x1, y1, true);
        boolean in_radius = Math.sqrt(Math.pow(startX - x1, 2) + Math.pow(startY - y1, 2)) <= 20;
        return los_exists && in_radius;
    }

    public String getLosName() {
        return los.getClass().getSimpleName();
    }

}
