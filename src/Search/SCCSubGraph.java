package Search;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.util.mxCellRenderer;
import org.jgrapht.Graph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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

    public void printGraph(String path) {
        try {
//            File imgFile = new File("resources/graph.png");
            File imgFile = new File(path);
            imgFile.createNewFile();
            JGraphXAdapter<PositionVertex, UndirectedWeightedEdge> graphAdapter =
                    new JGraphXAdapter<PositionVertex, UndirectedWeightedEdge>(this);
            mxIGraphLayout layout = new mxCircleLayout(graphAdapter);
//            mxHierarchicalLayout layout = new mxHierarchicalLayout(graphAdapter);
//            layout.setInterHierarchySpacing(layout.getInterHierarchySpacing() * 15);
//            layout.setInterRankCellSpacing(layout.getInterRankCellSpacing() * 15);
            layout.execute(graphAdapter.getDefaultParent());
            BufferedImage image =
                    mxCellRenderer.createBufferedImage(graphAdapter, null, 2, Color.WHITE, true, null);
            imgFile = new File(path);
            ImageIO.write(image, "PNG", imgFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}
