package by.zsp.ncst.graph.impl;

import by.zsp.ncst.exception.GraphException;
import by.zsp.ncst.graph.Edge;
import by.zsp.ncst.graph.Graph;
import by.zsp.ncst.graph.Vertex;
import by.zsp.ncst.util.Geometry;
import by.zsp.ncst.util.annotation.Immutable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class UndirectedGraphWithIntersections<V> implements Graph<V> {

    private final @NotNull Set<Vertex<V>> vertices;
    private final @NotNull Set<Edge<V>> edges;
    private final @NotNull Multimap<Vertex<V>, Vertex<V>> adjacency;
    private final @NotNull Multimap<Edge<V>, Edge<V>> intersections;

    public static <V> @NotNull UndirectedGraphWithIntersections<V> of(final @NotNull Collection<Edge<V>> edges) {
        final UndirectedGraphWithIntersections<V> graph = new UndirectedGraphWithIntersections<>();
        edges.forEach(graph::addEdge);
        return graph;
    }

    public UndirectedGraphWithIntersections() {
        vertices = new HashSet<>();
        edges = new HashSet<>();
        adjacency = HashMultimap.create();
        intersections = HashMultimap.create();
    }

    private UndirectedGraphWithIntersections(
            final @NotNull Set<Vertex<V>> vertices,
            final @NotNull Set<Edge<V>> edges,
            final @NotNull Multimap<Vertex<V>, Vertex<V>> adjacency,
            final @NotNull Multimap<Edge<V>, Edge<V>> intersections) {

        this.vertices = new HashSet<>(vertices);
        this.edges = new HashSet<>(edges);
        this.adjacency = HashMultimap.create(adjacency);
        this.intersections = HashMultimap.create(intersections);
    }

    @Override
    public @NotNull @Immutable Set<Vertex<V>> getVertices() {
        return Collections.unmodifiableSet(vertices);
    }

    @Override
    public int getVerticesNumber() {
        return vertices.size();
    }

    @Override
    public @NotNull @Immutable Set<Edge<V>> getEdges() {
        return Collections.unmodifiableSet(edges);
    }

    @Override
    public int getEdgesNumber() {
        return edges.size();
    }

    @Override
    public boolean addVertex(final @NotNull V id, final double x, final double y) {
        return addVertex(vertexOf(id, x, y));
    }

    @Override
    public boolean addVertex(final @NotNull Vertex<V> vertex) throws GraphException {
        return vertices.add(vertex);
    }

    @Override
    public boolean removeVertex(final @NotNull V id) {
        return removeVertex(vertexOf(id));
    }

    @Override
    public boolean removeVertex(final @NotNull Vertex<V> vertex) {
        for (final Vertex<V> neighbour : adjacency.get(vertex)) {
            removeEdge(vertex, neighbour);
        }

        return vertices.remove(vertex);
    }

    private @NotNull Vertex<V> vertexOf(final @NotNull V id) {
        return vertexOf(id, 0, 0);
    }

    @Override
    public @NotNull Vertex<V> vertexOf(final @NotNull V id, final double x, final double y) {
        return new SimpleVertex<>(id, x, y);
    }

    @Override
    public boolean addEdge(
            final @NotNull V idFrom,
            final double xFrom,
            final double yFrom,
            final @NotNull V idTo,
            final double xTo,
            final double yTo)
            throws GraphException {

        return addEdge(edgeOf(idFrom, xFrom, yFrom, idTo, xTo, yTo));
    }

    @Override
    public boolean addEdge(final @NotNull Vertex<V> from, final @NotNull Vertex<V> to) throws GraphException {
        return addEdge(edgeOf(from, to));
    }

    @Override
    public boolean addEdge(final @NotNull Edge<V> newEdge) throws GraphException {
        if (!edges.contains(newEdge)) {
            final @NotNull ImmutablePair<Vertex<V>, Vertex<V>> vertices = newEdge.asVerticesPair();

            if (Objects.equals(vertices.left, vertices.right)) {
                throw new GraphException("Loops are forbidden");
            }

            addVertex(vertices.left);
            addVertex(vertices.right);
            adjacency.put(vertices.left, vertices.right);
            adjacency.put(vertices.right, vertices.left);

            for (final Edge<V> edge : edges) {
                if (Geometry.isIntersecting(edge, newEdge)) {
                    intersections.put(edge, newEdge);
                    intersections.put(newEdge, edge);
                }
            }

            return edges.add(newEdge);
        } else {
            return false;
        }
    }

    @Override
    public boolean removeEdge(final @NotNull V from, final @NotNull V to) {
        return removeEdge(vertexOf(from), vertexOf(to));
    }

    @Override
    public boolean removeEdge(final @NotNull Vertex<V> from, final @NotNull Vertex<V> to) {
        return removeEdge(edgeOf(from, to));
    }

    @Override
    public boolean removeEdge(final @NotNull Edge<V> edge) {
        final @NotNull ImmutablePair<Vertex<V>, Vertex<V>> vertices = edge.asVerticesPair();
        adjacency.remove(vertices.left, vertices.right);
        adjacency.remove(vertices.right, vertices.left);

        for (final Edge<V> intersecting : intersections.get(edge)) {
            intersections.remove(intersecting, edge);
        }

        intersections.removeAll(edge);
        return edges.remove(edge);
    }

    @Override
    public boolean removeIntersecting(final @NotNull Edge<V> edge) {
        final List<Edge<V>> intersectingEdges = new ArrayList<>(intersections.get(edge));

        for (final Edge<V> intersectingEdge : intersectingEdges) {
            removeEdge(intersectingEdge);
        }

        return intersectingEdges.size() > 0;
    }

    @Override
    public @NotNull Edge<V> edgeOf(
            final @NotNull V idFrom,
            final double xFrom,
            final double yFrom,
            final @NotNull V idTo,
            final double xTo,
            final double yTo) {

        return edgeOf(vertexOf(idFrom, xFrom, yFrom), vertexOf(idTo, xTo, yTo));
    }

    @Override
    public @NotNull Edge<V> edgeOf(final @NotNull Vertex<V> vertex1, final @NotNull Vertex<V> vertex2) {
        return new SimpleUndirectedEdge<>(vertex1, vertex2);
    }

    @Override
    public boolean isDirected() {
        return false;
    }

    @Override
    public @NotNull @Immutable Collection<Vertex<V>> getNeighbours(final @NotNull V id) {
        return getNeighbours(vertexOf(id));
    }

    @Override
    public @NotNull @Immutable Collection<Vertex<V>> getNeighbours(final @NotNull Vertex<V> vertex) {
        return Collections.unmodifiableCollection(adjacency.get(vertex));
    }

    @Override
    public @NotNull Optional<Edge<V>> getMostIntersectingEdge() {
        Edge<V> mostIntersecting = null;

        for (final Edge<V> edge : intersections.keySet()) {
            if (mostIntersecting == null ||
                    intersections.get(mostIntersecting).size() < intersections.get(edge).size()) {

                mostIntersecting = edge;
            }
        }

        return Optional.ofNullable(mostIntersecting);
    }

    @Override
    public int getIntersectionIndex() {
        return getMostIntersectingEdge()
                .map(intersections::get)
                .map(Collection::size)
                .orElse(0);
    }

    @Override
    public boolean isIntersecting() {
        return getIntersectionIndex() > 0;
    }

    @Override
    public boolean isConnected() {
        final Set<Vertex<V>> visited = new HashSet<>();
        final Queue<Vertex<V>> verticesToVisit = new ArrayDeque<>();

        if (!vertices.isEmpty()) {
            final Vertex<V> first = vertices.iterator().next();
            verticesToVisit.add(first);

            while (!verticesToVisit.isEmpty()) {
                final Vertex<V> current = verticesToVisit.remove();

                if (!visited.contains(current)) {
                    visited.add(current);
                    verticesToVisit.addAll(getNeighbours(current));
                }
            }

            return vertices.size() == visited.size();
        } else {
            return true;
        }
    }

    @Override
    public @NotNull @Immutable List<Graph<V>> getConnectedComponents() {
        if (!vertices.isEmpty()) {
            final Set<Vertex<V>> visited = new HashSet<>();
            final List<Graph<V>> connectedComponents = new ArrayList<>();

            for (final Vertex<V> vertex : vertices) {
                if (!visited.contains(vertex)) {
                    final Set<Vertex<V>> connectedComponentVertices = new HashSet<>();
                    final Queue<Vertex<V>> verticesToVisit = new ArrayDeque<>();
                    verticesToVisit.add(vertex);

                    while (!verticesToVisit.isEmpty()) {
                        final Vertex<V> current = verticesToVisit.remove();

                        if (!visited.contains(current)) {
                            visited.add(current);
                            connectedComponentVertices.add(current);
                            verticesToVisit.addAll(getNeighbours(current));
                        }
                    }

                    connectedComponents.add(subGraph(connectedComponentVertices));
                }
            }

            return Collections.unmodifiableList(connectedComponents);
        } else {
            return Collections.singletonList(new UndirectedGraphWithIntersections<>());
        }
    }

    private @NotNull UndirectedGraphWithIntersections<V> subGraph(final @NotNull Set<Vertex<V>> vertices) {
        final UndirectedGraphWithIntersections<V> subGraph = new UndirectedGraphWithIntersections<>();
        vertices.forEach(subGraph::addVertex);

        for (final Vertex<V> vertex : vertices) {
            for (final Vertex<V> neighbour : getNeighbours(vertex)) {
                if (vertices.contains(neighbour)) {
                    subGraph.addEdge(edgeOf(vertex, neighbour));
                }
            }
        }

        return subGraph;
    }

    @Override
    public @NotNull @Immutable List<Edge<V>> findBridges() {
        if (!vertices.isEmpty()) {
            final List<Edge<V>> bridges = new ArrayList<>();

            bridgeDfs(
                    bridges,
                    new MutableInt(0),
                    vertices.iterator().next(),
                    null,
                    new HashSet<>(),
                    new HashMap<>(),
                    new HashMap<>());

            return Collections.unmodifiableList(bridges);
        } else {
            return Collections.emptyList();
        }
    }

    private void bridgeDfs(
            final @NotNull List<Edge<V>> bridges,
            final @NotNull MutableInt time,
            final @NotNull Vertex<V> visiting,
            final @Nullable Vertex<V> parent,
            final @NotNull Set<Vertex<V>> visited,
            final @NotNull Map<Vertex<V>, Integer> inTime,
            final @NotNull Map<Vertex<V>, Integer> upTime) {

        visited.add(visiting);
        time.increment();
        inTime.put(visiting, time.getValue());
        upTime.put(visiting, time.getValue());

        for (final Vertex<V> child : getNeighbours(visiting)) {
            if (!Objects.equals(child, parent)) {
                if (visited.contains(child)) {
                    upTime.put(visiting, Math.min(upTime.get(visiting), inTime.get(child)));
                } else {
                    bridgeDfs(bridges, time, child, visiting, visited, inTime, upTime);
                    upTime.put(visiting, Math.min(upTime.get(visiting), upTime.get(child)));

                    if (upTime.get(child) > inTime.get(visiting)) {
                        bridges.add(edgeOf(visiting, child));
                    }
                }
            }
        }
    }

    @Override
    public @NotNull Graph<V> copy() {
        return new UndirectedGraphWithIntersections<>(vertices, edges, adjacency, intersections);
    }

    @Override
    public String toString() {
        return String.format(
                "n = %d%nm = %d%nintersection index = %d%nintersections number = %d",
                getVerticesNumber(), getEdgesNumber(), getIntersectionIndex(), intersections.size() / 2);
    }
}
