package by.zsp.ncst.impl;

import by.zsp.ncst.NcstAlgorithm;
import by.zsp.ncst.exception.AlgorithmException;
import by.zsp.ncst.graph.Edge;
import by.zsp.ncst.graph.Graph;
import by.zsp.ncst.graph.impl.UndirectedGraphWithIntersections;
import by.zsp.ncst.matroid.Matroid;
import by.zsp.ncst.matroid.Matroids;
import by.zsp.ncst.matroid.algorithm.BaseMatroidIntersectionAlgorithm;
import by.zsp.ncst.matroid.algorithm.MatroidIntersectionAlgorithm;
import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class NcstAlgorithmImpl<V> extends BaseSearchTreeAlgorithm implements NcstAlgorithm<V> {

    @Override
    public @NotNull Optional<Graph<V>> findNcst(final @NotNull Graph<V> graph) throws AlgorithmException {
        return find((nodesCreated, nodesProcessed) ->
                new SearchTreeNode(nodesCreated, nodesProcessed, graph, new IndependentSet<>()));
    }

    private final class SearchTreeNode extends BaseSearchTreeNode<V> {

        private final @NotNull IndependentSet<V> fixed;

        private SearchTreeNode(
                final @NotNull AtomicInteger nodesCreated,
                final @NotNull AtomicInteger nodesProcessed,
                final @NotNull Graph<V> graph,
                final @NotNull IndependentSet<V> fixed) {

            this(nodesCreated, nodesProcessed, 0, "f", graph, fixed);
        }

        private SearchTreeNode(
                final @NotNull AtomicInteger nodesCreated,
                final @NotNull AtomicInteger nodesProcessed,
                final int depth,
                final @NotNull String index,
                final @NotNull Graph<V> graph,
                final @NotNull IndependentSet<V> fixed) {

            super(nodesCreated, nodesProcessed, depth, index, graph);
            this.fixed = fixed;
        }

        @Override
        @SuppressWarnings({ "OptionalGetWithoutIsPresent", "Duplicates" })
        protected @NotNull Optional<Graph<V>> _compute() {
            if (graph.getVerticesNumber() <= 1) {
                logNode(String.format("vertices number = %d", graph.getVerticesNumber()), true, depth, index);
                return Optional.of(graph.copy());
            } else if (!graph.isConnected()) {
                logNode("not connected", true, depth, index);
                return Optional.empty();
            } else if (!graph.isIntersecting()) {
                logNode("non-crossing", true, depth, index);
                fixed.addAll(graph.getEdges());
                return Optional.of(fixed.toGraph());
            } else if (graph.getIntersectionIndex() == 1) {
                logNode("intersection index 1", true, depth, index);
                final Matroid<Edge<V>> cycleMatroid = Matroids.cycleMatroidWithFixedEdgesOf(graph, fixed.getEdges());

                final Matroid<Edge<V>> intersectionMatroid =
                        Matroids.intersectionMatroidWithFixedEdgesOf(graph, fixed.getEdges());

                final MatroidIntersectionAlgorithm<Edge<V>> matroidIntersectionAlgorithm =
                        new BaseMatroidIntersectionAlgorithm<>();

                final Set<Edge<V>> nonCrossingEdges =
                        matroidIntersectionAlgorithm.findIntersection(cycleMatroid, intersectionMatroid);

                if (nonCrossingEdges.size() == graph.getVerticesNumber() - 1) {
                    return Optional.of(UndirectedGraphWithIntersections.of(nonCrossingEdges));
                } else {
                    return Optional.empty();
                }
            } else {
                final List<Edge<V>> bridges = graph.findBridges();

                if (!bridges.isEmpty()) {
                    final Graph<V> graphCopy = graph.copy();
                    int removedBridges = 0;

                    for (final Edge<V> bridge : bridges) {
                        graphCopy.removeIntersecting(bridge);
                        removedBridges += graphCopy.removeEdge(bridge) ? 1 : 0;
                    }

                    if (removedBridges < bridges.size()) {
                        logNode("splitting by bridges is impossible: bridges intersect each other", true, depth, index);
                        return Optional.empty();
                    }

                    final List<Graph<V>> connectedComponents = graphCopy.getConnectedComponents();

                    if (connectedComponents.size() == bridges.size() + 1) {
                        logNode("splitting by bridges", false, depth, index);

                        final List<SearchTreeNode> tasks = IntStream.rangeClosed(0, bridges.size())
                                .mapToObj(i -> new SearchTreeNode(
                                        nodesCreated, nodesProcessed,
                                        depth + 1, index + "b" + (i + 1),
                                        connectedComponents.get(i),
                                        fixed.filter(connectedComponents.get(i).getVertices())))
                                .collect(ImmutableList.toImmutableList());

                        final List<Graph<V>> subTrees = invokeAll(tasks).stream()
                                .map(SearchTreeNode::join)
                                .filter(Optional::isPresent)
                                .map(Optional::get)
                                .collect(Collectors.toList());

                        if (subTrees.size() == bridges.size() + 1) {
                            final Graph<V> tree = UndirectedGraphWithIntersections.of(bridges);

                            for (final Graph<V> subTree : subTrees) {
                                subTree.getEdges()
                                        .forEach(tree::addEdge);
                            }

                            return Optional.of(tree);
                        } else {
                            return Optional.empty();
                        }
                    } else if (connectedComponents.size() > bridges.size() + 1) {
                        logNode(
                                "splitting by bridges is impossible: too many connected components",
                                true, depth, index);

                        return Optional.empty();
                    } else {
                        // should never happen
                        throw new RuntimeException();
                    }
                } else {
                    final List<SearchTreeNode> tasks = new ArrayList<>();
                    final Edge<V> mostIntersecting = graph.getMostIntersectingEdge().get();

                    Graph<V> graphCopy = graph.copy();
                    graphCopy.removeEdge(mostIntersecting);
                    IndependentSet<V> fixedCopy = fixed.copy();

                    tasks.add(new SearchTreeNode(
                            nodesCreated, nodesProcessed, depth + 1, index + "e", graphCopy, fixedCopy));

                    if (fixed.canBeAdded(mostIntersecting)) {
                        logNode("splitting by most intersecting edge, most intersecting edge is included",
                                false, depth, index);

                        graphCopy = graph.copy();
                        graphCopy.removeIntersecting(mostIntersecting);
                        fixedCopy = fixed.copy();
                        fixedCopy.add(mostIntersecting);

                        tasks.add(new SearchTreeNode(
                                nodesCreated, nodesProcessed, depth + 1, index + "i", graphCopy, fixedCopy));
                    } else {
                        logNode("splitting by most intersecting edge, most intersecting edge isn't included",
                                false, depth, index);
                    }

                    return invokeAll(tasks).stream()
                            .map(SearchTreeNode::join)
                            .filter(Optional::isPresent)
                            .findAny()
                            .orElse(Optional.empty());
                }
            }
        }
    }
}
