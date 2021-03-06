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

import static Search.DistanceService.getPathWeight;

public class RoomMapGraphAdapter {
    private Graph<PositionVertex, UndirectedWeightedEdge> graph;
    HashSet<Position> reachablePrunnableVertices;
    private HashMap<Position, PositionVertex> prunnableVertices;
    private HashMap<Position, PositionVertex> unPrunnableVertices;
    private Map<PositionVertex, PositionVertex> watchingDictionary;
    private static final int HUGE_DOUBLE_VALUE = 0x7fffff00;
    static double distanceFactor = 2;
    HashSet<Position> whiteCells = new HashSet<>();
    // Flags
    static boolean includeWhiteCells = true;
    static boolean withFarthest = false;
    static boolean withDistanceFactor = false;

    public RoomMapGraphAdapter(TreeMap<Position,
            HashSet<Position>> watchedDictionary,
                               RoomMapState roomMapState,
                               boolean isForHeuristic) {
        watchingDictionary = new HashMap<>();
        graph = new DefaultUndirectedWeightedGraph<>(UndirectedWeightedEdge.class);
        prunnableVertices = new HashMap<>();
        unPrunnableVertices = new HashMap<>();
        reachablePrunnableVertices = new HashSet<>();
        addVerticesToGraph(watchedDictionary, roomMapState, isForHeuristic);
        if (!withFarthest) // adding farthest to Heuristic in a optimal and costly manner.(optimal paper)
            addAgentToGraph(roomMapState, watchedDictionary);
        else
            addAgentToGraph(roomMapState);
        if (includeWhiteCells)
            reachablePrunnableVertices.addAll(whiteCells);
        // for "Jump (Bounded)"
        if (withDistanceFactor) {
            HashSet<Position> toRemove = new HashSet<>();
            Position agentPosition = roomMapState.getPosition();
            double closerDistance = HUGE_DOUBLE_VALUE;
            // check distance to each prunnable vertice and cutoff all the far ones
            for (Position watcher : reachablePrunnableVertices) {
                double dist = DistanceService.getWeight(agentPosition, watcher);
                closerDistance = Math.min(dist, closerDistance);
            }
            for (Position watcher : reachablePrunnableVertices) {
                double dist = DistanceService.getWeight(agentPosition, watcher);
                if (dist > distanceFactor * closerDistance) {
                    toRemove.add(watcher);
                }
            }
            for (Position position : toRemove) {
                reachablePrunnableVertices.remove(position);
            }
        }
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
        makeGraphComplete();
    }

    private void makeGraphComplete() {
        //for every node in g check its connecred to all others, if not find the shortest path between them and add edge
        for (PositionVertex unprunedVertex1 : unPrunnableVertices.values()) {
            for (PositionVertex unprunedVertex2 : unPrunnableVertices.values()) {
                if (!unprunedVertex1.equals(unprunedVertex2) && !graph.containsEdge(unprunedVertex1, unprunedVertex2)) {
                    double weight = getPathWeight(graph, unprunedVertex1, unprunedVertex2);
                    UndirectedWeightedEdge newEdge = graph.addEdge(unprunedVertex1, unprunedVertex2);
                    graph.setEdgeWeight(newEdge, weight);
                }
            }
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
//            GraphPath<Position, UndirectedWeightedEdge> graphPath = DistanceService.getPath(agentPosition, key1);
//            List<Position> path = graphPath.getVertexList();
            PathFindingPath findingPath = DistanceService.getPathFind(agentPosition, key1);
            List<Position> path = findingPath.getPath();
            if(path.size() < 2)
                System.out.println("yay");
            if (Collections.disjoint(path.subList(1, path.size() - 1), prunnableVertices.keySet())) {
                UndirectedWeightedEdge edge = graph.addEdge(agentVertex, value1);
                double w = findingPath.getPathCost();
                graph.setEdgeWeight(edge, w);
                reachablePrunnableVertices.add(key1);
            }
//            UndirectedWeightedEdge edge = graph.addEdge(agentVertex, value1);
//            graph.setEdgeWeight(edge, DistanceService.getWeight(agentPosition, key1));
        }
    }


    private void addAgentToGraph(RoomMapState s, TreeMap<Position, HashSet<Position>> watchedDictionary) {
        HashMap<Position, HashSet<Position>> gettableWatchedDictionary = new HashMap<>(watchedDictionary);
        HashSet<PositionVertex> reachableUnPrunnableVertices = new HashSet<>();
        HashMap<PositionVertex, HashSet<PositionVertex>> unReachableUnPrunnableVertices = new HashMap<>();
        Position agentPosition = s.getPosition();
        PositionVertex agentVertex = new PositionVertex(agentPosition, PositionVertex.TYPE.UNPRUNNABLE);
        graph.addVertex(agentVertex);
        for (Map.Entry<Position, PositionVertex> entry1 : prunnableVertices.entrySet()) {
            Position key = entry1.getKey();
            PositionVertex value = entry1.getValue();
//            List<Position> path = DistanceService.getPath(agentPosition, key).getVertexList();
            PathFindingPath findingPath = DistanceService.getPathFind(agentPosition, key);
            List<Position> path = findingPath.getPath();

            //check that it isn't itself that gets in the way
            List<Position> pathSet = new ArrayList<>(path.subList(1, path.size() - 1));
            HashSet<Position> watchers = gettableWatchedDictionary.get(watchingDictionary.get(value).getPosition());
            pathSet.removeAll(watchers);

            if (Collections.disjoint(pathSet, prunnableVertices.keySet())) {
                UndirectedWeightedEdge edge = graph.addEdge(agentVertex, value);
                double w = path.size() - 1;
                graph.setEdgeWeight(edge, w);
                reachableUnPrunnableVertices.add(watchingDictionary.get(value));
                if (Collections.disjoint(path.subList(1, path.size() - 1), prunnableVertices.keySet()))
                    reachablePrunnableVertices.add(key);
            } else {
                //keep only prunnableVertices of the path
                List<Position> intermediatesPrunnables = pathSet;
                intermediatesPrunnables.retainAll(prunnableVertices.keySet());
                PositionVertex unReachableUnPrunnableVertex = watchingDictionary.get(value);
                HashSet<PositionVertex> intermediatesUnPrunnables = new HashSet<>();
                for (Position intermediatesPrunnable : intermediatesPrunnables)
                    intermediatesUnPrunnables.add(watchingDictionary.get(new PositionVertex(intermediatesPrunnable, PositionVertex.TYPE.PRUNNABLE)));
                if (!unReachableUnPrunnableVertices.containsKey(unReachableUnPrunnableVertex)) {
                    unReachableUnPrunnableVertices.put(watchingDictionary.get(value), intermediatesUnPrunnables);
                } else {
                    unReachableUnPrunnableVertices.get(unReachableUnPrunnableVertex).addAll(intermediatesUnPrunnables);
                }
            }
        }
        for (Map.Entry<PositionVertex, HashSet<PositionVertex>> entry : unReachableUnPrunnableVertices.entrySet()) {
            PositionVertex unReachableUnPrunnableVertex = entry.getKey();
            HashSet<PositionVertex> intermediatesUnPrunnables = entry.getValue();
            if (reachableUnPrunnableVertices.contains(unReachableUnPrunnableVertex)) continue;
            for (PositionVertex intermediateUnPrunnable : intermediatesUnPrunnables)
                removeUnPrunnable(intermediateUnPrunnable, gettableWatchedDictionary);
            //connectAgentTo UnReachableUnPrunnables
            for (Position position : gettableWatchedDictionary.get(unReachableUnPrunnableVertex.getPosition())) {
                PositionVertex value = new PositionVertex(position, PositionVertex.TYPE.PRUNNABLE);
                if (!prunnableVertices.containsKey(position)) continue;
//                List<Position> path = DistanceService.getPath(agentPosition, position).getVertexList();
                PathFindingPath findingPath = DistanceService.getPathFind(agentPosition, position);
                List<Position> path = findingPath.getPath();
                if (Collections.disjoint(path.subList(1, path.size() - 1), prunnableVertices.keySet())) {
                    UndirectedWeightedEdge edge = graph.addEdge(agentVertex, value);
                    double w = path.size() - 1;
                    graph.setEdgeWeight(edge, w);
                }
            }
            //connect UnReachableUnPrunnables to rest of g
            for (Position key1 : gettableWatchedDictionary.get(unReachableUnPrunnableVertex.getPosition())) {
                PositionVertex value1 = new PositionVertex(key1, PositionVertex.TYPE.PRUNNABLE);
                if (!prunnableVertices.containsKey(key1)) continue;
                for (Map.Entry<Position, PositionVertex> entry2 : prunnableVertices.entrySet()) {
                    Position key2 = entry2.getKey();
                    PositionVertex value2 = entry2.getValue();
                    if (key1.equals(key2) || graph.containsEdge(value2, value1) || isSameComponent(value1, value2))
                        continue;
//                    List<Position> path = DistanceService.getPath(key1, key2).getVertexList();
                    PathFindingPath findingPath = DistanceService.getPathFind(key1, key2);
                    List<Position> path = findingPath.getPath();
                    if (!path.contains(s.getPosition()) && Collections.disjoint(path.subList(1, path.size() - 1), prunnableVertices.keySet())) {
                        UndirectedWeightedEdge edge = graph.addEdge(value1, value2);
                        double w = path.size() - 1;
                        graph.setEdgeWeight(edge, w);
                    }
                }
            }
        }
    }

    private void removeUnPrunnable(PositionVertex unPrunnable, HashMap<Position, HashSet<Position>> gettableWatchedDictionary) {
        if (!unPrunnableVertices.containsKey(unPrunnable.getPosition()))
            return;
        for (Position position : gettableWatchedDictionary.get(unPrunnable.getPosition())) {
            graph.removeVertex(prunnableVertices.remove(position));
        }
        unPrunnableVertices.remove(unPrunnable.getPosition());
        graph.removeVertex(unPrunnable);//TO Do: check if all other connections to the vertex are also removed.
    }

    private void addVerticesToGraph(TreeMap<Position, HashSet<Position>> watchedDictionary, RoomMapState roomMapState, boolean addUnprunnables) {
        HashSet<Position> toRemove = new HashSet<>();
        HashSet<Position> pathsSet = new HashSet<>();
        HashSet<Position> allWatchers = new HashSet<>();
        HashSet<Position> visualSet = new HashSet<>(watchedDictionary.keySet());
        HashMap<Position, HashSet<Position>> gettableWatchedDictionary = new HashMap<>(watchedDictionary);
        Position agentPosition = roomMapState.getPosition();
        List<Position> whiteCellsPivots = new ArrayList<>();

        for (Map.Entry<Position, HashSet<Position>> entry : watchedDictionary.entrySet()) {
            Position watchedPosition = entry.getKey();
            if (roomMapState.getSeen().contains(watchedPosition)) continue;
            HashSet<Position> watchersSet = entry.getValue();
            if (prunnableVertices.containsKey(watchedPosition) || unPrunnableVertices.containsKey(watchedPosition))
                continue;
            if (!Collections.disjoint(watchersSet, prunnableVertices.keySet())) {
                if (includeWhiteCells)
                    whiteCellsPivots.add(watchedPosition);
                continue;
            }
            if (withFarthest) {
                if (checkIfIsntFarther(pathsSet, agentPosition, watchedPosition, watchersSet)) {
                    continue;
                }
            }
            if (includeWhiteCells)
                allWatchers.addAll(watchersSet);
            PositionVertex watchedVertex = new PositionVertex(watchedPosition, PositionVertex.TYPE.UNPRUNNABLE);
            unPrunnableVertices.put(watchedPosition, watchedVertex);
            if (addUnprunnables)
                graph.addVertex(watchedVertex);
            addPrunabbleVeticesToSingleUnprunnable(watchedVertex, watchersSet, visualSet, toRemove, addUnprunnables);

        }
        for (Position position : toRemove) {
            prunnableVertices.remove(position);
        }

        if (addUnprunnables)
            connectPrunnableVerticesInGraph(roomMapState);
        if (includeWhiteCells)
            addWhiteCells(allWatchers, visualSet, gettableWatchedDictionary, whiteCellsPivots);
    }

    private void addWhiteCells(HashSet<Position> allWatchers, HashSet<Position> visualSet, HashMap<Position, HashSet<Position>> gettableWatchedDictionary, List<Position> whiteCellsPivots) {
        if (whiteCellsPivots == null)
            return;
        for (int i = 0; i < whiteCellsPivots.size(); i++) {
            Position whiteCellPivot = whiteCellsPivots.get(i);
            if (whiteCells.contains(whiteCellPivot) || allWatchers.contains(whiteCellPivot))
                continue;
            allWatchers.addAll(gettableWatchedDictionary.get(whiteCellPivot));
            for (Position position : gettableWatchedDictionary.get(whiteCellPivot)) {
                if (!unPrunnableVertices.containsKey(position) && (RoomMap.HEURISTIC_GRAPH_METHOD.equals("All") || isFrontier(position, gettableWatchedDictionary.get(whiteCellPivot), visualSet))) {
                    whiteCells.add(position);
                }
            }
        }
    }

    private boolean checkIfIsntFarther(HashSet<Position> pathsSet, Position agentPosition, Position watchedPosition, HashSet<Position> watchersSet) {
        // if already passing in a cell that sees that pivot, continue.
        if (!Collections.disjoint(pathsSet, watchersSet))
            return true;
        // else, it a farther (for now), add the path to it's shortest path watcher to the paths set
//        GraphPath<Position, UndirectedWeightedEdge> newPath = DistanceService.getPath(agentPosition, watchedPosition);
//        List<Position> newPathList = newPath.getVertexList();
        PathFindingPath newPath = DistanceService.getPathFind(agentPosition, watchedPosition);
        List<Position> newPathList = newPath.getPath();
        newPathList.removeAll(watchersSet);
        //add the new path to the Paths set
        pathsSet.addAll(newPathList);
        return false;
    }

    private void connectPrunnableVerticesInGraph(RoomMapState roomMapState) {
        for (Map.Entry<Position, PositionVertex> entry1 : prunnableVertices.entrySet()) {
            Position key1 = entry1.getKey();
            PositionVertex value1 = entry1.getValue();
            for (Map.Entry<Position, PositionVertex> entry2 : prunnableVertices.entrySet()) {
                Position key2 = entry2.getKey();
                PositionVertex value2 = entry2.getValue();
                if (key1.equals(key2) || graph.containsEdge(value2, value1) || isSameComponent(value1, value2))
                    continue;
//                GraphPath<Position, UndirectedWeightedEdge> graphPath = DistanceService.getPath(key1, key2);
//                List<Position> path = graphPath.getVertexList();
                PathFindingPath findingPath = DistanceService.getPathFind(key1, key2);
                List<Position> path = findingPath.getPath();
                if (!path.contains(roomMapState.getPosition()) && Collections.disjoint(path.subList(1, path.size() - 1), prunnableVertices.keySet())) {
                    UndirectedWeightedEdge edge = graph.addEdge(value1, value2);
                    double w = findingPath.getPathCost();
                    graph.setEdgeWeight(edge, w);
                }
            }
        }
    }

    private boolean isSameComponent(PositionVertex v1, PositionVertex v2) {
        if (watchingDictionary.get(v1).equals(watchingDictionary.get(v2)))
            return true;
        return false;
    }

    private void addInvertedIndex(PositionVertex unprunnableVertex, PositionVertex prunnableVertex) {
        watchingDictionary.put(prunnableVertex, unprunnableVertex);
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
                addInvertedIndex(unprunnableVertex, prunnableVertex);
            } else {
                toRemove.add(position);
//                prunnableVertices.remove(position);
            }
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
        GraphPath<PositionVertex, UndirectedWeightedEdge> tour = heldKarpTSP.getTour(graph);
        double h = tour.getWeight();
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

    public void abstractGraph(TreeMap<Position, HashSet<Position>> watchedDictionary, Position agentPosition) {
        Graph<PositionVertex, UndirectedWeightedEdge> abstractGraph = new DefaultUndirectedWeightedGraph<>(UndirectedWeightedEdge.class);
        for (PositionVertex unprunedVertex : unPrunnableVertices.values()) {
            abstractGraph.addVertex(unprunedVertex);
        }
        for (PositionVertex unprunedVertex1 : unPrunnableVertices.values()) {
            for (PositionVertex unprunedVertex2 : unPrunnableVertices.values()) {
                if (!unprunedVertex1.equals(unprunedVertex2) &&
                        !abstractGraph.containsEdge(unprunedVertex1, unprunedVertex2)
                        && !abstractGraph.containsEdge(unprunedVertex1, unprunedVertex2)) {
                    double weight = minAbstractedPathWeight(unprunedVertex1, unprunedVertex2, watchedDictionary);
                    UndirectedWeightedEdge newEdge = abstractGraph.addEdge(unprunedVertex1, unprunedVertex2);
                    abstractGraph.setEdgeWeight(newEdge, weight);
                }
            }
        }
        PositionVertex agentPositionVertex = new PositionVertex(agentPosition, PositionVertex.TYPE.UNPRUNNABLE);
        abstractGraph.addVertex(agentPositionVertex);
        for (PositionVertex unprunedVertex : unPrunnableVertices.values()) {
            double weight = minAgentAbstractedPathWeight(agentPositionVertex, unprunedVertex, watchedDictionary);
            UndirectedWeightedEdge newEdge = abstractGraph.addEdge(agentPositionVertex, unprunedVertex);
            abstractGraph.setEdgeWeight(newEdge, weight);
        }
        graph = abstractGraph;
    }

    private double minAgentAbstractedPathWeight(PositionVertex agentPositionVertex, PositionVertex unprunedVertex, TreeMap<Position, HashSet<Position>> watchedDictionary) {
        double abstractedPathWeight = HUGE_DOUBLE_VALUE;
        HashMap<Position, HashSet<Position>> gettableWatchedDictionary = new HashMap<>(watchedDictionary);
        List<Position> group = new ArrayList<>(gettableWatchedDictionary.get(unprunedVertex.getPosition()));
        for (Position position : group) {
            double pathWeight = DistanceService.getPathFind(agentPositionVertex.getPosition(), position).getPathCost();
            abstractedPathWeight = Math.min(abstractedPathWeight, pathWeight);
        }
        return abstractedPathWeight;
    }

    private double minAbstractedPathWeight(PositionVertex unprunedVertex1, PositionVertex unprunedVertex2,
                                           TreeMap<Position, HashSet<Position>> watchedDictionary) {
        double abstractedPathWeight = HUGE_DOUBLE_VALUE;
        HashMap<Position, HashSet<Position>> gettableWatchedDictionary = new HashMap<>(watchedDictionary);
        List<Position> group1 = new ArrayList<>(gettableWatchedDictionary.get(unprunedVertex1.getPosition()));
        List<Position> group2 = new ArrayList<>(gettableWatchedDictionary.get(unprunedVertex2.getPosition()));
        for (Position pos1 : group1) {
            for (Position pos2 : group2) {
                if (pos1.equals(pos2))
                    return 0;
                double pathWeight = DistanceService.getPathFind(pos1, pos2).getPathCost();
                abstractedPathWeight = Math.min(abstractedPathWeight, pathWeight);
            }
        }
        return abstractedPathWeight;
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

    public HashSet<Position> getReachablePrunnableVertices() {
        return reachablePrunnableVertices;
    }
}
