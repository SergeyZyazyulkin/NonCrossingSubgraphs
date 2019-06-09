package by.zsp.ncst.impl;

import by.zsp.ncst.PolygonNcstAlgorithm;
import by.zsp.ncst.exception.AlgorithmException;
import by.zsp.ncst.graph.Edge;
import by.zsp.ncst.graph.Graph;
import by.zsp.ncst.graph.Vertex;
import by.zsp.ncst.graph.impl.UndirectedGraphWithIntersections;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class PolygonNcstAlgorithmImpl<V> implements PolygonNcstAlgorithm<V> {

    @Override
    public Optional<Graph<V>> findNcst(
            final @NotNull Graph<V> graph, final @NotNull List<Vertex<V>> polygon)
            throws AlgorithmException {

        try {
            assert graph.getVerticesNumber() == polygon.size();
            assert graph.getVertices().containsAll(polygon);

            if (graph.getVerticesNumber() <= 1) {
                return Optional.of(graph);
            } else if (graph.getVerticesNumber() == 2) {
                final List<Vertex<V>> vertices = new ArrayList<>(graph.getVertices());

                if (graph.getEdges().contains(graph.edgeOf(vertices.get(0), vertices.get(1)))) {
                    return Optional.of(graph);
                } else {
                    return Optional.empty();
                }
            } else {
                final Set<Edge<V>> forbiddenEdges = buildForbiddenEdges(graph, polygon);
                final NcstState[][] ncstTable = fillNcstTable(graph, polygon, forbiddenEdges);

                if (ncstTable[0][polygon.size() - 1] == NcstState.NCST) {
                    final Graph<V> ncst = new UndirectedGraphWithIntersections<>();

                    buildNcst(graph, polygon, ncstTable)
                            .forEach(ncst::addEdge);

                    assert ncst.getVerticesNumber() == graph.getVerticesNumber();
                    assert ncst.getEdgesNumber() == ncst.getVerticesNumber() - 1;
                    assert ncst.isConnected();
                    assert !ncst.isIntersecting();
                    return Optional.of(ncst);
                } else {
                    return Optional.empty();
                }
            }
        } catch (final Exception e) {
            throw new AlgorithmException(e);
        }
    }

    private Set<Edge<V>> buildForbiddenEdges(
            final @NotNull Graph<V> graph, final @NotNull List<Vertex<V>> polygon)
            throws AlgorithmException {

        final List<Vertex<V>> closed = new ArrayList<>(polygon);

        if (!Objects.equals(closed.get(0), closed.get(closed.size() - 1))) {
            closed.add(closed.get(0));
        }

        final Coordinate[] coordinates = closed.stream()
                .map(Vertex::getCoordinates)
                .toArray(Coordinate[]::new);

        final GeometryFactory geometryFactory = new GeometryFactory();
        final LinearRing ring = geometryFactory.createLinearRing(coordinates);

        final List<Vertex<V>> vertices = new ArrayList<>(graph.getVertices());
        final Set<Edge<V>> edges = new HashSet<>();

        for (int i = 0; i < vertices.size(); ++i) {
            for (int j = i + 1; j < vertices.size(); ++j) {
                edges.add(graph.edgeOf(vertices.get(i), vertices.get(j)));
            }
        }

        return edges.stream()
                .filter(edge -> !ring.covers(geometryFactory.createLineString(edge.asCoordinates())))
                .collect(Collectors.toSet());
    }

    private @NotNull NcstState[][] fillNcstTable(
            final @NotNull Graph<V> graph, final @NotNull List<Vertex<V>> polygon, final Set<Edge<V>> forbiddenEdges) {

        final NcstState[][] ncstTable = new NcstState[polygon.size()][polygon.size()];

        for (int i = polygon.size() - 2; i >= 0; ++i) {
            ncstTable[i][i + 1] = graph.getEdges().contains(graph.edgeOf(polygon.get(i), polygon.get(i + 1)))
                    ? NcstState.NCST
                    : NcstState.FOREST;

            for (int j = i + 2; j < polygon.size(); ++j) {
                if (forbiddenEdges.contains(graph.edgeOf(polygon.get(i), polygon.get(j)))) {
                    ncstTable[i][j] = NcstState.INVALID;
                } else {
                    ncstTable[i][j] = findBestSplit(graph, polygon, ncstTable, i, j).right;
                }
            }
        }

        return ncstTable;
    }

    private @NotNull Set<Edge<V>> buildNcst(
            final @NotNull Graph<V> graph,
            final @NotNull List<Vertex<V>> polygon,
            final @NotNull NcstState[][] ncstTable) {

        final Set<Edge<V>> ncstEdges = new HashSet<>();
        processSubgraph(graph, polygon, ncstTable, ncstEdges, 0, polygon.size() - 1);
        return ncstEdges;
    }

    private void processSubgraph(
            final @NotNull Graph<V> graph,
            final @NotNull List<Vertex<V>> polygon,
            final @NotNull NcstState[][] ncstTable,
            final @NotNull Set<Edge<V>> ncstEdges,
            final int from,
            final int to) {

        if (from + 1 == to && graph.getEdges().contains(graph.edgeOf(polygon.get(from), polygon.get(to)))) {
            ncstEdges.add(graph.edgeOf(polygon.get(from), polygon.get(to)));
        } else {
            final ImmutablePair<Integer, NcstState> bestSplit = findBestSplit(graph, polygon, ncstTable, from, to);

            if (bestSplit.right == NcstState.NCST &&
                    (ncstTable[from][bestSplit.left] == NcstState.FOREST ||
                            ncstTable[bestSplit.left][to] == NcstState.FOREST)) {

                ncstEdges.add(graph.edgeOf(polygon.get(from), polygon.get(to)));
            }

            processSubgraph(graph, polygon, ncstTable, ncstEdges, from, bestSplit.left);
            processSubgraph(graph, polygon, ncstTable, ncstEdges, bestSplit.left, to);
        }
    }

    private @NotNull ImmutablePair<Integer, NcstState> findBestSplit(
            final @NotNull Graph<V> graph,
            final @NotNull List<Vertex<V>> polygon,
            final NcstState[][] ncstTable,
            final int from,
            final int to) {

        NcstState bestState = NcstState.INVALID;
        int bestSplit = from + 1;
        final boolean ijEdge = graph.getEdges().contains(graph.edgeOf(polygon.get(from), polygon.get(to)));

        for (int i = from + 1; i < to; ++i) {
            NcstState currentState;

            if (ncstTable[from][i] == NcstState.INVALID || ncstTable[i][to] == NcstState.INVALID) {
                currentState = NcstState.INVALID;
            } else if (ncstTable[from][i] == NcstState.NCST && ncstTable[i][to] == NcstState.NCST) {
                currentState = NcstState.NCST;
            } else if ((ncstTable[from][i] == NcstState.NCST && ncstTable[i][to] == NcstState.FOREST ||
                    ncstTable[from][i] == NcstState.FOREST && ncstTable[i][to] == NcstState.NCST)) {

                currentState = ijEdge ? NcstState.NCST : NcstState.FOREST;
            } else {
                currentState = NcstState.NO_FOREST;
            }

            final NcstState newBestState = NcstState.max(bestState, currentState);

            if (bestState != newBestState) {
                bestState = newBestState;
                bestSplit = i;
            }
        }

        return new ImmutablePair<>(bestSplit, bestState);
    }

    private enum NcstState {

        INVALID,
        NO_FOREST,
        FOREST,
        NCST;

        public static @NotNull NcstState max(final @NotNull NcstState ns1, final @NotNull NcstState ns2) {
            return ns1.ordinal() >= ns2.ordinal() ? ns1 : ns2;
        }
    }
}
