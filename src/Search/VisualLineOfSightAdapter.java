package Search;

import rlforj.los.*;

public class VisualLineOfSightAdapter {
    private ILosAlgorithm los;

    public VisualLineOfSightAdapter(ILosAlgorithm los) {
        this.los = los;
    }

    public boolean existsLineOfSight(ILosBoard b, int startX, int startY, int x1, int y1) {
        return los.existsLineOfSight(b, startX, startY, x1, y1, true);
    }

    public String getLosName() {
        return los.getClass().getSimpleName();
    }

}
