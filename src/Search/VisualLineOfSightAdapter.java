package Search;

import rlforj.examples.ExampleBoard;
import rlforj.los.*;
import rlforj.math.Point2I;

import java.util.List;

public class VisualLineOfSightAdapter {
    ILosAlgorithm los;

    public VisualLineOfSightAdapter(ILosAlgorithm los) {
        this.los = los;
    }

    public boolean existsLineOfSight(ILosBoard b, int startX, int startY, int x1, int y1, boolean calculateProject) {
        return los.existsLineOfSight(b, startX, startY, x1, y1, calculateProject);
    }

    public List<Point2I> getProjectPath() {
        return los.getProjectPath();
    }

    public ILosAlgorithm getLos() {
        return los;
    }

    public String getLosName() {
        return los.getClass().getSimpleName();
    }

    public void setLos(ILosAlgorithm los) {
        this.los = los;
    }
}
