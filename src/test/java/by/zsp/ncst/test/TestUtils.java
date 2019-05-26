package by.zsp.ncst.test;

import by.zsp.ncst.graph.Edge;
import by.zsp.ncst.graph.Graph;
import by.zsp.ncst.graph.Vertex;
import org.apache.commons.lang3.RandomUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class TestUtils {

    private TestUtils () {}

    public static void generateRandomGraph(
            final @NotNull Graph<Integer> graph, final int verticesNumber, final int edgesNumber) {

        final List<Vertex<Integer>> vertices = IntStream.range(0, verticesNumber)
                .mapToObj(i -> graph.vertexOf(i, RandomUtils.nextDouble(0, 1), RandomUtils.nextDouble(0, 1)))
                .collect(Collectors.toList());

        final List<Edge<Integer>> edges = IntStream.range(0, edgesNumber)
                .mapToObj(i -> {
                    final int first = RandomUtils.nextInt(0, verticesNumber);
                    int second;

                    do {
                        second = RandomUtils.nextInt(0, verticesNumber);
                    } while (second == first);

                    return graph.edgeOf(vertices.get(first), vertices.get(second));
                })
                .collect(Collectors.toList());

        vertices.forEach(graph::addVertex);
        edges.forEach(graph::addEdge);
    }
}
