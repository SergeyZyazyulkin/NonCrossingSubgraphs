package by.zsp.ncst.test;

import by.zsp.ncst.graph.Edge;
import by.zsp.ncst.graph.Graph;
import by.zsp.ncst.graph.Vertex;
import by.zsp.ncst.util.Geometry;
import org.apache.commons.lang3.RandomUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class TestUtils {

    private static final double MIN_COORD = 0;
    private static final double MAX_COORD = 1;
    private static final double MAX_EDGE_LENGTH_SCALING = 3;
    private static final int MAX_GENERATION_ATTEMPTS = 1000;

    private TestUtils () {}

    static void generateRandomGraph(
            final @NotNull Graph<Integer> graph, final int verticesNumber, final int edgesNumber) {

        final List<Vertex<Integer>> vertices = IntStream.range(0, verticesNumber)
                .mapToObj(i -> graph.vertexOf(
                        i, RandomUtils.nextDouble(MIN_COORD, MAX_COORD), RandomUtils.nextDouble(MIN_COORD, MAX_COORD)))
                .collect(Collectors.toList());

        final List<Edge<Integer>> edges = IntStream.range(0, edgesNumber)
                .mapToObj(i -> {
                    final int first = RandomUtils.nextInt(0, verticesNumber);
                    int second;
                    int attempts = 0;

                    do {
                        second = RandomUtils.nextInt(0, verticesNumber);
                        attempts++;
                    } while (second == first || (attempts < MAX_GENERATION_ATTEMPTS &&
                            Geometry.distance(vertices.get(first), vertices.get(second)) >
                                    (MAX_COORD - MIN_COORD) / verticesNumber * MAX_EDGE_LENGTH_SCALING));

                    return graph.edgeOf(vertices.get(first), vertices.get(second));
                })
                .collect(Collectors.toList());

        vertices.forEach(graph::addVertex);
        edges.forEach(graph::addEdge);
    }
}
