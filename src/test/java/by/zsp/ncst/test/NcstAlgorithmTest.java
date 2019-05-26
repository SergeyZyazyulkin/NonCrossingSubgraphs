package by.zsp.ncst.test;

import by.zsp.ncst.NcstAlgorithm;
import by.zsp.ncst.graph.Graph;
import by.zsp.ncst.graph.impl.UndirectedGraphWithIntersections;
import by.zsp.ncst.impl.NcstAlgorithmImpl;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

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
    public void testBridgesWithNcst() {
        graph.addEdge(0, 0, 0, 1, 2, 0);
        graph.addEdge(1, 2, 0, 2, 5, 0);
        graph.addEdge(1, 2, 0, 3, 2, 2);
        graph.addEdge(3, 2, 2, 4, 1, 2);
        graph.addEdge(4, 1, 2, 5, 1, -2);
        graph.addEdge(5, 1, -2, 6, 2, -2);
        graph.addEdge(6, 2, -2, 11, 2, -1);
        graph.addEdge(11, 2, -1, 1, 2, 0);
        graph.addEdge(11, 2, -1, 12, 0, -1);
        graph.addEdge(2, 5, 0, 7, 5, 2);
        graph.addEdge(7, 5, 2, 8, 4, 2);
        graph.addEdge(8, 4, 2, 9, 4, -2);
        graph.addEdge(9, 4, -2, 10, 5, -2);
        graph.addEdge(10, 5, -2, 2, 5, 0);
        test(true);
    }

    @Test
    public void testBridgesWithoutNcst() {
        graph.addEdge(0, 0, 0, 1, 2, 0);
        graph.addEdge(1, 2, 0, 2, 5, 0);
        graph.addEdge(1, 2, 0, 3, 2, 2);
        graph.addEdge(3, 2, 2, 4, 1, 2);
        graph.addEdge(4, 1, 2, 5, 1, -2);
        graph.addEdge(5, 1, -2, 6, 2, -2);
        graph.addEdge(6, 2, -2, 11, 2, -1);
        graph.addEdge(11, 2, -1, 12, 0, -1);
        graph.addEdge(2, 5, 0, 7, 5, 2);
        graph.addEdge(7, 5, 2, 8, 4, 2);
        graph.addEdge(8, 4, 2, 9, 4, -2);
        graph.addEdge(9, 4, -2, 10, 5, -2);
        graph.addEdge(10, 5, -2, 2, 5, 0);
        test(false);
    }

    @Test
    public void testRandomGraph() {
        TestUtils.generateRandomGraph(graph, 30, 150);
        test();
    }

    private void test() {
        System.out.println("Intersection index: " + graph.getIntersectionIndex());
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
