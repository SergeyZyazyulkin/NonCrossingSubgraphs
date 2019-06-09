package by.zsp.ncst.test;

import by.zsp.ncst.PolygonNcstAlgorithm;
import by.zsp.ncst.graph.Graph;
import by.zsp.ncst.graph.Vertex;
import by.zsp.ncst.graph.impl.UndirectedGraphWithIntersections;
import by.zsp.ncst.impl.PolygonNcstAlgorithmImpl;
import by.zsp.ncst.util.Visualizer;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PolygonNcstAlgorithmTest {

    @Rule
    public TestName testName = new TestName();

    private @NotNull Graph<Integer> graph;
    private @NotNull List<Vertex<Integer>> polygon;
    private @NotNull PolygonNcstAlgorithm<Integer> ncstAlgorithm;

    @Before
    public void init() {
        graph = new UndirectedGraphWithIntersections<>();
        polygon = new ArrayList<>();
        ncstAlgorithm = new PolygonNcstAlgorithmImpl<>();
    }

    @Test
    public void testEmpty() {
        test(true);
    }

    @Test
    public void testSingleVertex() {
        graph.addVertex(1, 1, 1);
        polygon.add(graph.vertexOf(1, 1, 1));
        test(true);
    }

    @Test
    public void testNotConnected() {
        graph.addEdge(1, 1, 1, 2, 2, 2);
        graph.addVertex(3, 3, 2);

        polygon.add(graph.vertexOf(1, 1, 1));
        polygon.add(graph.vertexOf(2, 2, 2));
        polygon.add(graph.vertexOf(3, 3, 2));

        test(false);
    }

    @Test
    public void testDiamond() {
        graph.addEdge(1, -1, 0, 2, 0, 1);
        graph.addEdge(2, 0, 1, 3, 1, 0);
        graph.addEdge(3, 1, 0, 4, 0, -1);
        graph.addEdge(4, 0, -1, 1, -1, 0);

        polygon.add(graph.vertexOf(1, -1, 0));
        polygon.add(graph.vertexOf(2, 0, 1));
        polygon.add(graph.vertexOf(3, 1, 0));
        polygon.add(graph.vertexOf(4, 0, -1));

        test(true);
    }

    @Test
    public void testIntersectionIndex1WithoutNcst() {
        graph.addEdge(1, 0, 0, 4, 1, 1);
        graph.addEdge(2, 0, 1, 3, 1, 0);
        graph.addEdge(3, 1, 0, 4, 1, 1);
        graph.addEdge(4, 1, 1, 5, 2, 1);

        polygon.add(graph.vertexOf(1, 0, 0));
        polygon.add(graph.vertexOf(2, 0, 1));
        polygon.add(graph.vertexOf(4, 1, 1));
        polygon.add(graph.vertexOf(5, 2, 1));
        polygon.add(graph.vertexOf(3, 1, 0));

        test(false);
    }

    @Test
    public void testIntersectionIndex1WithNcst() {
        graph.addEdge(1, 0, 0, 2, 0, 1);
        graph.addEdge(1, 0, 0, 4, 1, 1);
        graph.addEdge(2, 0, 1, 3, 1, 0);
        graph.addEdge(3, 1, 0, 4, 1, 1);
        graph.addEdge(4, 1, 1, 5, 2, 1);

        polygon.add(graph.vertexOf(1, 0, 0));
        polygon.add(graph.vertexOf(2, 0, 1));
        polygon.add(graph.vertexOf(4, 1, 1));
        polygon.add(graph.vertexOf(5, 2, 1));
        polygon.add(graph.vertexOf(3, 1, 0));

        test(true);
    }

    @Test
    public void testIntersectionIndex2WithoutNcst() {
        graph.addEdge(1, 0, 0, 4, 1, 1);
        graph.addEdge(1, 0, 0, 5, 2, 1);
        graph.addEdge(2, 0, 1, 3, 1, 0);
        graph.addEdge(3, 1, 0, 4, 1, 1);
        graph.addEdge(4, 1, 1, 5, 2, 1);

        polygon.add(graph.vertexOf(1, 0, 0));
        polygon.add(graph.vertexOf(2, 0, 1));
        polygon.add(graph.vertexOf(4, 1, 1));
        polygon.add(graph.vertexOf(5, 2, 1));
        polygon.add(graph.vertexOf(3, 1, 0));

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

        polygon.add(graph.vertexOf(1, 0, 0));
        polygon.add(graph.vertexOf(2, 0, 1));
        polygon.add(graph.vertexOf(4, 1, 1));
        polygon.add(graph.vertexOf(5, 2, 1));
        polygon.add(graph.vertexOf(3, 1, 0));

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

        polygon.add(graph.vertexOf(0, 0, 0));
        polygon.add(graph.vertexOf(4, 1, 2));
        polygon.add(graph.vertexOf(3, 2, 2));
        polygon.add(graph.vertexOf(1, 2, 0));
        polygon.add(graph.vertexOf(8, 4, 2));
        polygon.add(graph.vertexOf(7, 5, 2));
        polygon.add(graph.vertexOf(2, 5, 0));
        polygon.add(graph.vertexOf(10, 5, -2));
        polygon.add(graph.vertexOf(9, 4, -2));
        polygon.add(graph.vertexOf(11, 2, -1));
        polygon.add(graph.vertexOf(6, 2, -2));
        polygon.add(graph.vertexOf(5, 1, -2));
        polygon.add(graph.vertexOf(12, 0, -1));

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

        polygon.add(graph.vertexOf(0, 0, 0));
        polygon.add(graph.vertexOf(4, 1, 2));
        polygon.add(graph.vertexOf(3, 2, 2));
        polygon.add(graph.vertexOf(1, 2, 0));
        polygon.add(graph.vertexOf(8, 4, 2));
        polygon.add(graph.vertexOf(7, 5, 2));
        polygon.add(graph.vertexOf(2, 5, 0));
        polygon.add(graph.vertexOf(10, 5, -2));
        polygon.add(graph.vertexOf(9, 4, -2));
        polygon.add(graph.vertexOf(11, 2, -1));
        polygon.add(graph.vertexOf(6, 2, -2));
        polygon.add(graph.vertexOf(5, 1, -2));
        polygon.add(graph.vertexOf(12, 0, -1));

        test(false);
    }

    @Test
    public void testRandomGraph() {
        TestUtils.generateRandomPolygonGraph(graph, polygon, 2000);
        test();
    }

    private void test() {
        test(null);
    }

    private void test(final Boolean hasNcst) {
        System.out.println(graph);
        final Optional<Graph<Integer>> optionalNcst = ncstAlgorithm.findNcst(graph, polygon);

        if (optionalNcst.isPresent()) {
            visualize(optionalNcst.get());
        } else {
            visualize(graph);
        }

        if (hasNcst != null) {
            Assert.assertEquals(hasNcst, optionalNcst.isPresent());
        } else {
            System.out.println(optionalNcst.isPresent());
        }

        optionalNcst.ifPresent(this::check);
    }

    private void check(final @NotNull Graph<Integer> ncst) {
        Assert.assertEquals(graph.getVerticesNumber(), ncst.getVerticesNumber());
        Assert.assertEquals(Math.max(graph.getVerticesNumber() - 1, 0), ncst.getEdgesNumber());
        Assert.assertTrue(ncst.isConnected());
        Assert.assertFalse(ncst.isIntersecting());
    }

    private void visualize(final @NotNull Graph<?> result) {
        Visualizer.visualize("polygon_ncst_" + testName.getMethodName() + "_graph", graph);
        Visualizer.visualize("polygon_ncst_" + testName.getMethodName() + "_ncst", result);
    }
}
