package Search;

import org.jgrapht.graph.DefaultWeightedEdge;

public class UndirectedWeightedEdge extends DefaultWeightedEdge {

    public PositionVertex getSource() {
        return (PositionVertex) super.getSource();
    }


    public PositionVertex getTarget() {
        return (PositionVertex) super.getTarget();
    }

    public double getWeight() {
        return super.getWeight();
    }

    @Override
    public String toString() {
        return super.toString() + "\nweight: " + super.getWeight();
    }
}
