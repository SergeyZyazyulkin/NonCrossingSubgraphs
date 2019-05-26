package by.zsp.ncst.graph;

import by.zsp.ncst.exception.GraphException;
import by.zsp.ncst.util.annotation.Immutable;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

// V - vertex ID type
public interface Graph<V> extends Cloneable {

    @NotNull @Immutable Set<Vertex<V>> getVertices();

    int getVerticesNumber();

    @NotNull @Immutable Set<Edge<V>> getEdges();

    int getEdgesNumber();

    boolean addVertex(@NotNull V id, double x, double y);

    boolean addVertex(@NotNull Vertex<V> vertex);

    boolean removeVertex(@NotNull V id);

    boolean removeVertex(@NotNull Vertex<V> vertex);

    @NotNull Vertex<V> vertexOf(@NotNull V id, double x, double y);

    boolean addEdge(@NotNull V idFrom, double xFrom, double yFrom, @NotNull V idTo, double xTo, double yTo)
            throws GraphException;

    boolean addEdge(@NotNull Vertex<V> from, @NotNull Vertex<V> to) throws GraphException;

    boolean addEdge(@NotNull Edge<V> edge) throws GraphException;

    boolean removeEdge(@NotNull V from, @NotNull V to);

    boolean removeEdge(@NotNull Vertex<V> from, @NotNull Vertex<V> to);

    boolean removeEdge(@NotNull Edge<V> edge);

    boolean removeIntersecting(@NotNull Edge<V> edge);

    @NotNull Edge<V> edgeOf(@NotNull V idFrom, double xFrom, double yFrom, @NotNull V idTo, double xTo, double yTo);

    @NotNull Edge<V> edgeOf(@NotNull Vertex<V> vertex1, @NotNull Vertex<V> vertex2);

    boolean isDirected();

    @NotNull @Immutable Collection<Vertex<V>> getNeighbours(@NotNull V id);

    @NotNull @Immutable Collection<Vertex<V>> getNeighbours(@NotNull Vertex<V> vertex);

    int getIntersectionIndex();

    @NotNull Optional<Edge<V>> getMostIntersectingEdge();

    boolean isIntersecting();

    boolean isConnected();

    @NotNull @Immutable List<Graph<V>> getConnectedComponents();

    @NotNull @Immutable List<Edge<V>> findBridges();

    @NotNull Graph<V> copy();
}
