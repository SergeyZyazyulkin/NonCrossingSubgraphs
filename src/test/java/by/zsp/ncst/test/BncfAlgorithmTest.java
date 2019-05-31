package by.zsp.ncst.test;

import by.zsp.ncst.BncfAlgorithm;
import by.zsp.ncst.graph.Graph;
import by.zsp.ncst.graph.impl.UndirectedGraphWithIntersections;
import by.zsp.ncst.impl.BncfAlgorithmImpl;
import by.zsp.ncst.util.Visualizer;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

public class BncfAlgorithmTest {

    @Rule
    public TestName testName = new TestName();

    private @NotNull Graph<Integer> graph;
    private @NotNull BncfAlgorithm<Integer> bncfAlgorithm;

    @Before
    public void init() {
        graph = new UndirectedGraphWithIntersections<>();
        bncfAlgorithm = new BncfAlgorithmImpl<>();
    }

    @Test
    public void testEmpty() {
        test(0);
    }

    @Test
    public void testSingleVertex() {
        graph.addVertex(1, 1, 1);
        test(0);
    }

    @Test
    public void testNotConnected() {
        graph.addEdge(1, 1, 1, 2, 2, 2);
        graph.addVertex(3, 3, 3);
        test(1);
    }

    @Test
    public void testStar() {
        graph.addEdge(1, 0, 0, 2, -1, 0);
        graph.addEdge(1, 0, 0, 3, 0, 1);
        graph.addEdge(1, 0, 0, 4, 1, 0);
        graph.addEdge(1, 0, 0, 5, 0, -1);
        test(4);
    }

    @Test
    public void testCycle() {
        graph.addEdge(1, -1, -1, 2, -1, 1);
        graph.addEdge(2, -1, 1, 3, 1, 1);
        graph.addEdge(3, 1, 1, 4, 1, -1);
        graph.addEdge(4, 1, -1, 1, -1, -1);
        test(3);
    }

    @Test
    public void testIntersectionIndex1WithoutNcst() {
        graph.addEdge(1, 0, 0, 4, 1, 1);
        graph.addEdge(2, 0, 1, 3, 1, 0);
        graph.addEdge(3, 1, 0, 4, 1, 1);
        graph.addEdge(4, 1, 1, 5, 2, 1);
        test(3);
    }

    @Test
    public void testIntersectionIndex1WithNcst() {
        graph.addEdge(1, 0, 0, 2, 0, 1);
        graph.addEdge(1, 0, 0, 4, 1, 1);
        graph.addEdge(2, 0, 1, 3, 1, 0);
        graph.addEdge(3, 1, 0, 4, 1, 1);
        graph.addEdge(4, 1, 1, 5, 2, 1);
        test(4);
    }

    @Test
    public void testIntersectionIndex2WithoutNcst() {
        graph.addEdge(1, 0, 0, 4, 1, 1);
        graph.addEdge(1, 0, 0, 5, 2, 1);
        graph.addEdge(2, 0, 1, 3, 1, 0);
        graph.addEdge(3, 1, 0, 4, 1, 1);
        graph.addEdge(4, 1, 1, 5, 2, 1);
        test(3);
    }

    @Test
    public void testIntersectionIndex2WithNcst() {
        graph.addEdge(1, 0, 0, 2, 0, 1);
        graph.addEdge(1, 0, 0, 4, 1, 1);
        graph.addEdge(1, 0, 0, 5, 2, 1);
        graph.addEdge(2, 0, 1, 3, 1, 0);
        graph.addEdge(3, 1, 0, 4, 1, 1);
        graph.addEdge(4, 1, 1, 5, 2, 1);
        test(4);
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
        test(12);
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
        test(11);
    }

    @Test
    public void testRandomGraph() {
        TestUtils.generateRandomGraph(graph, 40, 150);
        test();
    }

    private void test() {
        test(null);
    }

    private void test(final Integer size) {
        System.out.println(graph);
        final Graph<Integer> bncf = bncfAlgorithm.findBncf(graph);
        visualize(bncf);
        Assert.assertFalse(bncf.isIntersecting());

        if (size != null) {
            Assert.assertEquals(size.intValue(), bncf.getEdgesNumber());
        } else {
            System.out.println("BNCSG size: " + bncf.getEdgesNumber());
        }
    }

    private void visualize(final @NotNull Graph<?> result) {
        Visualizer.visualize("bncf_" + testName.getMethodName() + "_graph", graph);
        Visualizer.visualize("bncf_" + testName.getMethodName() + "_bncf", result);
    }
}
