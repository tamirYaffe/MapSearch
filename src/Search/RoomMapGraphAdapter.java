package Search;

//import com.mxgraph.layout.mxCircleLayout;
//import com.mxgraph.layout.mxIGraphLayout;
//import com.mxgraph.util.mxCellRenderer;
//import javax.imageio.ImageIO;
//import java.awt.*;
//import java.awt.image.BufferedImage;
//import java.io.File;
//import java.io.IOException;
//import org.jgrapht.ext.JGraphXAdapter;

import javafx.geometry.Pos;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.tour.HeldKarpTSP;
import org.jgrapht.graph.AbstractBaseGraph;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.util.FibonacciHeap;
import org.jgrapht.util.FibonacciHeapNode;
import org.jgrapht.util.VertexToIntegerMapping;

import java.lang.reflect.Array;
import java.util.*;
import java.util.List;

public class RoomMapGraphAdapter {
    private Graph<PositionVertex, UndirectedWeightedEdge> graph;
    private HashMap<Position, PositionVertex> prunnableVertices;
    private HashMap<Position, PositionVertex> unPrunnableVertices;
    private static final int HUGE_DOUBLE_VALUE = 0x7fffff00;
    static double distanceFactor = 2;

    public RoomMapGraphAdapter(TreeMap<Position, HashSet<Position>> watchedDictionary, RoomMapState roomMapJumpState, boolean isForHeuristic) {
        graph = new DefaultUndirectedWeightedGraph<>(UndirectedWeightedEdge.class);
        prunnableVertices = new HashMap<>();
        unPrunnableVertices = new HashMap<>();
        addVerticesToGraph(watchedDictionary, roomMapJumpState, isForHeuristic);
        addAgentToGraph(roomMapJumpState, (RoomMap.HEURISTIC_GRAPH_METHOD.equals("Front Frontiers") || RoomMap.HEURISTIC_GRAPH_METHOD.equals("Farther Frontiers")) & !isForHeuristic);
    }

    private void addAgentToGraph(RoomMapState roomMapJumpState, boolean isFrontFrontiers) {
        if (isFrontFrontiers) {
            HashMap<Position, HashSet<Position>> gettableWatchedDictionary = new HashMap<>(((RoomMap) roomMapJumpState.getProblem()).getWatchedDictionary());
            Position agentPosition = roomMapJumpState.getPosition();
            PositionVertex agentVertex = new PositionVertex(agentPosition, PositionVertex.TYPE.UNPRUNNABLE);
            graph.addVertex(agentVertex);
            unPrunnableVertices.put(agentPosition, agentVertex);
            for (Map.Entry<Position, PositionVertex> unprunnableVertice : unPrunnableVertices.entrySet()) {
                Position watchedPosition = unprunnableVertice.getKey();
                if (watchedPosition.equals(agentPosition)) continue;
                HashSet<Position> prunnableSet = new HashSet<>(gettableWatchedDictionary.get(watchedPosition));
                prunnableSet.retainAll(prunnableVertices.keySet());
                for (Position prunnablePosition : prunnableSet) {
                    GraphPath<Position, UndirectedWeightedEdge> path = DistanceService.getPath(agentPosition, prunnablePosition);
                    if (path.getLength() > 1 && Collections.disjoint(path.getVertexList().subList(1, path.getLength()), gettableWatchedDictionary.get(watchedPosition))) {
                        UndirectedWeightedEdge edge = graph.addEdge(agentVertex, prunnableVertices.get(prunnablePosition));
                        if (edge != null)
                            graph.setEdgeWeight(edge, path.getWeight());
                    } else {
                        graph.removeVertex(prunnableVertices.remove(prunnablePosition));

                    }
                }
            }
        } else addAgentToGraph(roomMapJumpState);
    }

    public void pruneGraph() {
        for (PositionVertex prunedVertex : prunnableVertices.values()) {
            ArrayList<UndirectedWeightedEdge> edges = new ArrayList<>(graph.edgesOf(prunedVertex));
            for (UndirectedWeightedEdge edge1 : edges) {
                PositionVertex vertex1 = getOtherEdgeSide(edge1, prunedVertex);
                for (UndirectedWeightedEdge edge2 : edges) {
                    PositionVertex vertex2 = getOtherEdgeSide(edge2, prunedVertex);
                    if (vertex1.getPosition().equals(vertex2.getPosition())) continue;
                    edgeRemovalUpdate(vertex1, vertex2, edge1, edge2);
                }
            }
            graph.removeVertex(prunedVertex);
        }
    }

    private void edgeRemovalUpdate(PositionVertex vertex1, PositionVertex vertex2, UndirectedWeightedEdge edge1, UndirectedWeightedEdge edge2) {
        UndirectedWeightedEdge oldEdge = graph.getEdge(vertex1, vertex2);
        if (oldEdge == null) {
            UndirectedWeightedEdge newEdge = graph.addEdge(vertex1, vertex2);
            graph.setEdgeWeight(newEdge, edge1.getWeight() + edge2.getWeight());
        } else {
            graph.setEdgeWeight(oldEdge, Math.min(edge1.getWeight() + edge2.getWeight(), oldEdge.getWeight()));
        }
        graph.removeEdge(edge1);
        graph.removeEdge(edge2);
    }

    private PositionVertex getOtherEdgeSide(UndirectedWeightedEdge edge, PositionVertex value1) {
        return edge.getSource().equals(value1) ? edge.getTarget() : edge.getSource();
    }


    private void addAgentToGraph(RoomMapState s) {
        Position agentPosition = s.getPosition();
        PositionVertex agentVertex = new PositionVertex(agentPosition, PositionVertex.TYPE.UNPRUNNABLE);
        graph.addVertex(agentVertex);
        for (Map.Entry<Position, PositionVertex> entry1 : prunnableVertices.entrySet()) {
            Position key1 = entry1.getKey();
            PositionVertex value1 = entry1.getValue();
            UndirectedWeightedEdge edge = graph.addEdge(agentVertex, value1);
            graph.setEdgeWeight(edge, DistanceService.getWeight(agentPosition, key1));
        }
    }

    private void addVerticesToGraph(TreeMap<Position, HashSet<Position>> watchedDictionary, RoomMapState roomMapJumpState, boolean addUnprunnables) {
        HashSet<Position> toRemove = new HashSet<>();
        HashSet<Position> pathsSet = new HashSet<>();
        HashSet<Position> visualSet = new HashSet<>(watchedDictionary.keySet());
        HashMap<Position, HashSet<Position>> gettableWatchedDictionary = new HashMap<>(watchedDictionary);
        Position agentPosition = roomMapJumpState.getPosition();
        PositionVertex agentVertex = new PositionVertex(agentPosition, PositionVertex.TYPE.UNPRUNNABLE);

        for (Map.Entry<Position, HashSet<Position>> entry : watchedDictionary.entrySet()) {
            Position watchedPosition = entry.getKey();
            if (roomMapJumpState.getSeen().contains(watchedPosition)) continue;
            HashSet<Position> watchersSet = entry.getValue();
            if (!Collections.disjoint(watchersSet, prunnableVertices.keySet()) || prunnableVertices.containsKey(watchedPosition)
                    || (RoomMap.HEURISTIC_GRAPH_METHOD.equals("Farther Frontiers") && checkIfIsntFarther(pathsSet, agentPosition, watchedPosition, watchersSet)))
                continue;
            PositionVertex watchedVertex = new PositionVertex(watchedPosition, PositionVertex.TYPE.UNPRUNNABLE);
            unPrunnableVertices.put(watchedPosition, watchedVertex);
            if (addUnprunnables)
                graph.addVertex(watchedVertex);
            addPrunabbleVeticesToSingleUnprunnable(watchedVertex, watchersSet, visualSet, toRemove, addUnprunnables);
        }
        for (Position position : toRemove) {
            prunnableVertices.remove(position);
        }
        if (RoomMap.HEURISTIC_GRAPH_METHOD.equals("Farther Frontiers") && !Collections.disjoint(pathsSet, prunnableVertices.keySet())) {
            HashSet<PositionVertex> removedVertices = new HashSet<>();
            for (Position position : unPrunnableVertices.keySet()) {
                if (!Collections.disjoint(pathsSet, gettableWatchedDictionary.get(position))) {
                    removedVertices.add(unPrunnableVertices.get(position));
                    for (Position position1 : gettableWatchedDictionary.get(position)) {
                        //consider adding try and catch
                        graph.removeVertex(prunnableVertices.remove(position1));
                    }
                }
            }
            graph.removeAllVertices(removedVertices);
            for (PositionVertex removedVertex : removedVertices) {
                unPrunnableVertices.remove(removedVertex.getPosition());
            }
        }
        if (addUnprunnables)
            connectPrunnableVerticesInGraph();
            // for "Jump (Bounded)"
        else if (RoomMap.MOVEMENT_METHOD.endsWith(")")) {
            toRemove.clear();
//            distanceFactor = (Math.log(unPrunnableVertices.size()) / Math.log(2)) + 1;
            double closerDistance = HUGE_DOUBLE_VALUE;
            // check distance to each pivot and cutoff all the far pivots
            for (Position pivot : unPrunnableVertices.keySet()) {
                double dist = DistanceService.getWeight(agentPosition, pivot);
                closerDistance = Math.min(dist, closerDistance);
                if (dist > distanceFactor * closerDistance) {
                    toRemove.add(pivot);
                    for (Position watcher : gettableWatchedDictionary.get(pivot)) {
                        if (prunnableVertices.containsKey(watcher))
                            graph.removeVertex(prunnableVertices.remove(watcher));
                    }
                }
            }
            for (Position position : toRemove) {
                unPrunnableVertices.remove(position);
            }
            for (Position pivot : unPrunnableVertices.keySet()) {
                double dist = DistanceService.getWeight(agentPosition, pivot);
                if (dist > distanceFactor * closerDistance) {
                    toRemove.add(pivot);
                    for (Position watcher : gettableWatchedDictionary.get(pivot)) {
                        if (prunnableVertices.containsKey(watcher))
                            graph.removeVertex(prunnableVertices.remove(watcher));
                    }
                }
            }

        }
    }

    private boolean checkIfIsntFarther(HashSet<Position> pathsSet, Position agentPosition, Position watchedPosition, HashSet<Position> watchersSet) {
        // if already passing in a cell that sees that pivot, continue.
        if (!Collections.disjoint(pathsSet, watchersSet))
            return true;
        // else, it a farther (for now), add the path to it's shortest path watcher to the paths set
        GraphPath<Position, UndirectedWeightedEdge> newPath = DistanceService.getPath(agentPosition, watchedPosition);
        List<Position> newPathList = newPath.getVertexList();
        newPathList.removeAll(watchersSet);
        //add the new path to the Paths set
        pathsSet.addAll(newPathList);
        return false;
    }

    private void connectPrunnableVerticesInGraph() {
        for (Map.Entry<Position, PositionVertex> entry1 : prunnableVertices.entrySet()) {
            Position key1 = entry1.getKey();
            PositionVertex value1 = entry1.getValue();
            for (Map.Entry<Position, PositionVertex> entry2 : prunnableVertices.entrySet()) {
                Position key2 = entry2.getKey();
                PositionVertex value2 = entry2.getValue();
                if (key1.equals(key2) || graph.containsEdge(value2, value1)) continue;
                UndirectedWeightedEdge edge = graph.addEdge(value1, value2);
                graph.setEdgeWeight(edge, DistanceService.getWeight(key1, key2));
            }
        }
    }

    private void addPrunabbleVeticesToSingleUnprunnable(PositionVertex unprunnableVertex, HashSet<Position> prunnableSet, HashSet<Position> visualSet, HashSet<Position> toRemove, boolean addEdgeToUnprunnableVertex) {
        for (Position position : prunnableSet) {
            PositionVertex prunnableVertex = new PositionVertex(position, PositionVertex.TYPE.PRUNNABLE);
            prunnableVertices.put(position, prunnableVertex);
            if (!unPrunnableVertices.containsKey(position) && (RoomMap.HEURISTIC_GRAPH_METHOD.equals("All") || isFrontier(position, prunnableSet, visualSet))) {
                graph.addVertex(prunnableVertex);
                if (addEdgeToUnprunnableVertex) {
                    UndirectedWeightedEdge edge = graph.addEdge(prunnableVertex, unprunnableVertex);
                    graph.setEdgeWeight(edge, 0);
                }
            } else toRemove.add(position);
        }
    }

    private boolean isFrontier(Position key, HashSet<Position> prunnableSet, HashSet<Position> visualSet) {
        boolean ans = false;
        Position checkPosition = new Position(key.getY(), key.getX() - 1);
        if (!prunnableSet.contains(checkPosition) && visualSet.contains(checkPosition)) {
            ans = true;
        }
        checkPosition = new Position(key.getY(), key.getX() + 1);
        if (!ans && !prunnableSet.contains(checkPosition) && visualSet.contains(checkPosition)) {
            ans = true;
        }
        checkPosition = new Position(key.getY() - 1, key.getX());
        if (!ans && !prunnableSet.contains(checkPosition) && visualSet.contains(checkPosition)) {
            ans = true;
        }
        checkPosition = new Position(key.getY() + 1, key.getX());
        if (!ans && !prunnableSet.contains(checkPosition) && visualSet.contains(checkPosition)) {
            ans = true;
        }
        return ans;
    }

    public Graph<PositionVertex, UndirectedWeightedEdge> getGraph() {
        return graph;
    }

    public double getPrimMSTWeight() {
        return getSpanningTreeWeight();
    }

    public double getTSPWeight(Position startPosition) {
        PositionVertex start = new PositionVertex(new Position(), PositionVertex.TYPE.PRUNNABLE);
        graph.addVertex(start);
        for (PositionVertex vertex : graph.vertexSet()) {
            if (vertex.getPosition().equals(startPosition)) {
                UndirectedWeightedEdge edge = graph.addEdge(start, vertex);
                graph.setEdgeWeight(edge, 0);
                continue;
            }
            UndirectedWeightedEdge edge = graph.addEdge(start, vertex);
            graph.setEdgeWeight(edge, HUGE_DOUBLE_VALUE);
        }
        graph.removeEdge(start, start);
//        TwoOptHeuristicTSP<PositionVertex, UndirectedWeightedEdge> heldKarpTSP = new TwoOptHeuristicTSP<>();
        HeldKarpTSP<PositionVertex, UndirectedWeightedEdge> heldKarpTSP = new HeldKarpTSP<>();
        double h = heldKarpTSP.getTour(graph).getWeight();
        return h > 0 ? h - HUGE_DOUBLE_VALUE : h;

    }


    private double getSpanningTreeWeight() {
//        Set<UndirectedWeightedEdge> minimumSpanningTreeEdgeSet = new HashSet<>(graph.vertexSet().size());
        double spanningTreeWeight = 0d;

        final int N = graph.vertexSet().size();

        /*
         * Normalize the graph by mapping each vertex to an integer.
         */
        VertexToIntegerMapping<PositionVertex> vertexToIntegerMapping = Graphs.getVertexToIntegerMapping(graph);
        Map<PositionVertex, Integer> vertexMap = vertexToIntegerMapping.getVertexMap();
        List<PositionVertex> indexList = vertexToIntegerMapping.getIndexList();

        VertexInfo[] vertices = (VertexInfo[]) Array.newInstance(VertexInfo.class, N);
        FibonacciHeapNode<VertexInfo>[] fibNodes =
                (FibonacciHeapNode<VertexInfo>[]) Array.newInstance(FibonacciHeapNode.class, N);
        FibonacciHeap<VertexInfo> fibonacciHeap = new FibonacciHeap<>();

        for (int i = 0; i < N; i++) {
            vertices[i] = new VertexInfo();
            vertices[i].id = i;
            vertices[i].distance = Double.MAX_VALUE;
            fibNodes[i] = new FibonacciHeapNode<>(vertices[i]);

            fibonacciHeap.insert(fibNodes[i], vertices[i].distance);
        }

        while (!fibonacciHeap.isEmpty()) {
            FibonacciHeapNode<VertexInfo> fibNode = fibonacciHeap.removeMin();
            VertexInfo vertexInfo = fibNode.getData();

            PositionVertex p = indexList.get(vertexInfo.id);
            vertexInfo.spanned = true;

            // Add the edge from its parent to the spanning tree (if it exists)
            if (vertexInfo.edgeFromParent != null) {
//                minimumSpanningTreeEdgeSet.add(vertexInfo.edgeFromParent);
                spanningTreeWeight += graph.getEdgeWeight(vertexInfo.edgeFromParent);
            }

            // update all (unspanned) neighbors of p
            for (UndirectedWeightedEdge e : graph.edgesOf(p)) {
                PositionVertex q = Graphs.getOppositeVertex(graph, e, p);
                int id = vertexMap.get(q);

                // if the vertex is not explored and we found a better edge, then update the info
                if (!vertices[id].spanned) {
                    double cost = graph.getEdgeWeight(e);

                    if (cost < vertices[id].distance) {
                        vertices[id].distance = cost;
                        vertices[id].edgeFromParent = e;

                        fibonacciHeap.decreaseKey(fibNodes[id], cost);
                    }
                }
            }
        }
        return spanningTreeWeight;
    }

    public double getSimplePathWeight(RoomMapState s) {
        double mspWeight = HUGE_DOUBLE_VALUE;
        PositionVertex agentVertex = new PositionVertex(s.getPosition(), PositionVertex.TYPE.UNPRUNNABLE);
        ArrayList<UndirectedWeightedEdge> edges = new ArrayList<>();
        edges.addAll(graph.edgesOf(agentVertex));
        if (edges.isEmpty()) return 0;
        Graph<PositionVertex, UndirectedWeightedEdge> graphCopy = (Graph<PositionVertex, UndirectedWeightedEdge>) ((AbstractBaseGraph) graph).clone();
        for (UndirectedWeightedEdge edge : edges) {
            double tmpMSPWeight = edge.getWeight();
            PositionVertex vertex = Graphs.getOppositeVertex(graph, edge, agentVertex);
            graph.removeVertex(agentVertex);
            tmpMSPWeight = getSimplePathWeight(vertex, tmpMSPWeight);
            mspWeight = Math.min(tmpMSPWeight, mspWeight);
            graph = (Graph<PositionVertex, UndirectedWeightedEdge>) ((AbstractBaseGraph) graphCopy).clone();
        }
        return mspWeight;
    }

    private double getSimplePathWeight(PositionVertex vertex, double mspWeight) {
        PriorityQueue<UndirectedWeightedEdge> edgesHeap = new PriorityQueue<>(((o1, o2) -> {
            if (o1.getWeight() > o2.getWeight()) return 1;
            if (o1.getWeight() < o2.getWeight()) return -1;
            return 0;
        }));
        HashSet<PositionVertex> vertices = new HashSet<>();
        edgesHeap.addAll(graph.edgesOf(vertex));
        if (edgesHeap.isEmpty()) {
            return mspWeight;
        } else {
            PositionVertex pivot = Graphs.getOppositeVertex(graph, edgesHeap.poll(), vertex);
            edgesHeap.addAll(graph.edgesOf(pivot));
            HashSet<UndirectedWeightedEdge> edgesSet = new HashSet<>(edgesHeap);
            while (!edgesHeap.isEmpty() && edgesHeap.peek().getWeight() == 0) {
                UndirectedWeightedEdge edge = edgesHeap.poll();
                PositionVertex otherVertex = Graphs.getOppositeVertex(graph, edge, pivot);
                for (UndirectedWeightedEdge undirectedWeightedEdge : graph.edgesOf(otherVertex)) {
                    if (!edgesSet.contains(undirectedWeightedEdge)) {
                        edgesSet.add(undirectedWeightedEdge);
                        edgesHeap.add(undirectedWeightedEdge);
                    }
                }
            }
            vertices.addAll(Graphs.neighborSetOf(graph, pivot));
            UndirectedWeightedEdge edge = edgesHeap.poll();
            while (edge != null && vertices.contains(edge.getSource()) && vertices.contains(edge.getTarget()))
                edge = edgesHeap.poll();
            if (edge == null) return mspWeight;
            mspWeight += edge.getWeight();
            PositionVertex next = edge.getSource();
            if (vertices.contains(next))
                next = edge.getTarget();
            graph.removeAllVertices(vertices);
            graph.removeVertex(pivot);
            return getSimplePathWeight(next, mspWeight);
        }
    }

    private class VertexInfo {
        int id;
        boolean spanned;
        double distance;
        UndirectedWeightedEdge edgeFromParent;
    }

//    public void printGraph(String path) {
//        try {
////            File imgFile = new File("resources/graph.png");
//            File imgFile = new File(path);
//            boolean newFileMade = imgFile.createNewFile();
//            if (newFileMade) System.out.println("Created Graph image in: " + path);
//            JGraphXAdapter<PositionVertex, UndirectedWeightedEdge> graphAdapter = new JGraphXAdapter<>(graph);
//            mxIGraphLayout layout = new mxCircleLayout(graphAdapter);
////            mxHierarchicalLayout layout = new mxHierarchicalLayout(graphAdapter);
////            layout.setInterHierarchySpacing(layout.getInterHierarchySpacing() * 15);
////            layout.setInterRankCellSpacing(layout.getInterRankCellSpacing() * 15);
//            layout.execute(graphAdapter.getDefaultParent());
//            BufferedImage image =
//                    mxCellRenderer.createBufferedImage(graphAdapter, null, 2, Color.WHITE, true, null);
//            imgFile = new File(path);
//            ImageIO.write(image, "PNG", imgFile);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        System.exit(0);
//    }

}
