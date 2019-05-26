package by.zsp.ncst.test;

import by.zsp.ncst.matroid.algorithm.BaseMatroidIntersectionAlgorithm;
import by.zsp.ncst.matroid.algorithm.MatroidIntersectionAlgorithm;
import by.zsp.ncst.exception.MatroidException;
import by.zsp.ncst.graph.Edge;
import by.zsp.ncst.graph.Graph;
import by.zsp.ncst.graph.Vertex;
import by.zsp.ncst.graph.impl.UndirectedGraphWithIntersections;
import by.zsp.ncst.matroid.Matroid;
import by.zsp.ncst.matroid.Matroids;
import com.google.common.collect.Sets;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class MatroidWithFixedEdgesTest {

    @Test
    public void noFixedEdges() {
        testGraph(4);
    }

    @Test
    public void oneFixedEdge() {
        testGraph(3, 3);
    }

    @Test
    public void halfOfEdgesFixed() {
        testGraph(2, 4, 5);
    }

    @Test
    public void almostAllEdgesFixedV1() {
        testGraph(1, 0, 4, 5);
    }

    @Test
    public void almostAllEdgesFixedV2() {
        testGraph(1, 0, 1, 5);
    }

    @Test
    public void allEdgesFixed() {
        testGraph(0, 0, 1, 4, 5);
    }

    @Test(expected = MatroidException.class)
    public void crossingFixedEdges() {
        testGraph(-1, 1, 2);
    }

    private void testGraph(int expectedIntersectionSize, @NotNull int... fixedEdgesIndices) {
        test(
                graph -> {
                    Vertex<Integer> vertex1 = graph.vertexOf(1, 1, 3);
                    Vertex<Integer> vertex2 = graph.vertexOf(2, 3, 3);
                    Vertex<Integer> vertex3 = graph.vertexOf(3, 0, 1);
                    Vertex<Integer> vertex4 = graph.vertexOf(4, 4, 1);
                    Vertex<Integer> vertex5 = graph.vertexOf(5, 1, 0);
                    Vertex<Integer> vertex6 = graph.vertexOf(6, 3, 0);

                    List<Edge<Integer>> edges = new ArrayList<>();
                    edges.add(graph.edgeOf(vertex1, vertex2));
                    edges.add(graph.edgeOf(vertex1, vertex6));
                    edges.add(graph.edgeOf(vertex2, vertex5));
                    edges.add(graph.edgeOf(vertex3, vertex4));
                    edges.add(graph.edgeOf(vertex3, vertex5));
                    edges.add(graph.edgeOf(vertex4, vertex6));
                    edges.forEach(graph::addEdge);

                    final Set<Edge<Integer>> fixedEdges = new HashSet<>();

                    for (final int fixEdgeIndex : fixedEdgesIndices) {
                        fixedEdges.add(edges.get(fixEdgeIndex));
                    }

                    return fixedEdges;
                },
                expectedIntersectionSize);
    }

    private void test(
            @NotNull Function<@NotNull Graph<Integer>, @NotNull Set<Edge<Integer>>> testDataBuilder,
            int expectedIntersectionSize) {

        final Graph<Integer> graph = new UndirectedGraphWithIntersections<>();
        final Set<Edge<Integer>> fixedEdges = testDataBuilder.apply(graph);
        final Set<Edge<Integer>> intersection = findIntersection(graph, fixedEdges);
        Assert.assertTrue(Sets.intersection(fixedEdges, intersection).isEmpty());
        Assert.assertEquals(expectedIntersectionSize, intersection.size());
    }

    private <V> Set<Edge<V>> findIntersection(final @NotNull Graph<V> graph, final @NotNull Set<Edge<V>> fixedEdges) {
        final Matroid<Edge<V>> cycleMatroidWithFixedEdges =
                Matroids.cycleMatroidWithFixedEdgesOf(graph, fixedEdges);

        final Matroid<Edge<V>> intersectionMatroidWithFixedEdges =
                Matroids.intersectionMatroidWithFixedEdgesOf(graph, fixedEdges);

        final MatroidIntersectionAlgorithm<Edge<V>> matroidIntersectionAlgorithm =
                new BaseMatroidIntersectionAlgorithm<>();

        return matroidIntersectionAlgorithm.findIntersection(
                cycleMatroidWithFixedEdges, intersectionMatroidWithFixedEdges);
    }
}
