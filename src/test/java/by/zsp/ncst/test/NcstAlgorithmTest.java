package by.zsp.ncst.test;

import by.zsp.ncst.NcstAlgorithm;
import by.zsp.ncst.graph.Edge;
import by.zsp.ncst.graph.Graph;
import by.zsp.ncst.graph.Vertex;
import by.zsp.ncst.graph.impl.UndirectedGraphWithIntersections;
import by.zsp.ncst.impl.NcstAlgorithmImpl;
import org.apache.commons.lang3.RandomUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class NcstAlgorithmTest {

    private @NotNull Graph<Integer> graph;
    private @NotNull NcstAlgorithm<Integer> ncstAlgorithm;

    @Before
    public void init() {
        graph = new UndirectedGraphWithIntersections<>();
        ncstAlgorithm = new NcstAlgorithmImpl<>();
    }

    @Test
    public void testEmpty() {
        test(true);
    }

    @Test
    public void testSingleVertex() {
        graph.addVertex(1, 1, 1);
        test(true);
    }

    @Test
    public void testNotConnected() {
        graph.addEdge(1, 1, 1, 2, 2, 2);
        graph.addVertex(3, 3, 3);
        test(false);
    }

    @Test
    public void testStar() {
        graph.addEdge(1, 0, 0, 2, -1, 0);
        graph.addEdge(1, 0, 0, 3, 0, 1);
        graph.addEdge(1, 0, 0, 4, 1, 0);
        graph.addEdge(1, 0, 0, 5, 0, -1);
        test(true);
    }

    @Test
    public void testCycle() {
        graph.addEdge(1, -1, -1, 2, -1, 1);
        graph.addEdge(2, -1, 1, 3, 1, 1);
        graph.addEdge(3, 1, 1, 4, 1, -1);
        graph.addEdge(4, 1, -1, 1, -1, -1);
        test(true);
    }

    @Test
    public void testIntersectionIndex1WithoutNcst() {
        graph.addEdge(1, 0, 0, 4, 1, 1);
        graph.addEdge(2, 0, 1, 3, 1, 0);
        graph.addEdge(3, 1, 0, 4, 1, 1);
        graph.addEdge(4, 1, 1, 5, 2, 1);
        test(false);
    }

    @Test
    public void testIntersectionIndex1WithNcst() {
        graph.addEdge(1, 0, 0, 2, 0, 1);
        graph.addEdge(1, 0, 0, 4, 1, 1);
        graph.addEdge(2, 0, 1, 3, 1, 0);
        graph.addEdge(3, 1, 0, 4, 1, 1);
        graph.addEdge(4, 1, 1, 5, 2, 1);
        test(true);
    }

    @Test
    public void testIntersectionIndex2WithoutNcst() {
        graph.addEdge(1, 0, 0, 4, 1, 1);
        graph.addEdge(1, 0, 0, 5, 2, 1);
        graph.addEdge(2, 0, 1, 3, 1, 0);
        graph.addEdge(3, 1, 0, 4, 1, 1);
        graph.addEdge(4, 1, 1, 5, 2, 1);
        test(false);
    }

    @Test
    public void testIntersectionIndex2WithNcst() {
        graph.addEdge(1, 0, 0, 2, 0, 1);
        graph.addEdge(1, 0, 0, 4, 1, 1);
        graph.addEdge(1, 0, 0, 5, 2, 1);
        graph.addEdge(2, 0, 1, 3, 1, 0);
        graph.addEdge(3, 1, 0, 4, 1, 1);
        graph.addEdge(4, 1, 1, 5, 2, 1);
        test(true);
    }

    @Test
    public void testRandomGraph() {
        final int verticesNumber = 30;
        final int edgesNumber = 200;

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
        System.out.println(graph.getIntersectionIndex());
        test();
    }

    private void test() {
        final Optional<Graph<Integer>> optionalNcst = ncstAlgorithm.findNcst(graph);
        System.out.println(optionalNcst.isPresent());

        if (optionalNcst.isPresent()) {
            check(optionalNcst);
        }
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private void test(final boolean hasNcst) {
        final Optional<Graph<Integer>> optionalNcst = ncstAlgorithm.findNcst(graph);
        Assert.assertEquals(hasNcst, optionalNcst.isPresent());

        if (hasNcst) {
            check(optionalNcst);
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private void check(final @NotNull Optional<Graph<Integer>> optionalNcst) {
        final Graph<Integer> ncst = optionalNcst.get();
        Assert.assertEquals(graph.getVerticesNumber(), ncst.getVerticesNumber());
        Assert.assertEquals(Math.max(graph.getVerticesNumber() - 1, 0), ncst.getEdgesNumber());
        Assert.assertTrue(ncst.isConnected());
        Assert.assertFalse(ncst.isIntersecting());
    }
}
