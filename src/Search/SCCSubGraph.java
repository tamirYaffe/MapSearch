package Search;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;

import java.util.Set;

public class SCCSubGraph extends DefaultUndirectedWeightedGraph<PositionVertex, UndirectedWeightedEdge> {
    Position rarePoint;
    Position closeCutPoint;
    Position farCutPoint;

    public SCCSubGraph(Class<? extends UndirectedWeightedEdge> edgeClass) {
        super(edgeClass);
    }

    public void setRarePoint(Position rarePoint) {
        this.rarePoint = rarePoint;
    }

    public void setCloseCutPoint(Position closeCutPoint) {
        this.closeCutPoint = closeCutPoint;
    }

    public void setFarCutPoint(Position farCutPoint) {
        this.farCutPoint = farCutPoint;
    }

    public Position getRarePoint() {
        return rarePoint;
    }

    public Position getCloseCutPoint() {
        return closeCutPoint;
    }

    public Position getFarCutPoint() {
        return farCutPoint;
    }

    public void addCutPointTosubGraph(Position cutPoint) {
        PositionVertex cutPointVertex = new PositionVertex(cutPoint, PositionVertex.TYPE.UNPRUNNABLE);
        addVertex(cutPointVertex);
        for (PositionVertex vertex : vertexSet()) {
            if (vertex.equals(cutPointVertex) || vertex.getPosition().equals(rarePoint)) continue;
            UndirectedWeightedEdge edge = addEdge(cutPointVertex, vertex);
            if (edge != null)
                setEdgeWeight(edge, DistanceService.getWeight(cutPointVertex.getPosition(), vertex.getPosition()));
        }
    }
}
