package by.zsp.ncst.test;

import by.zsp.ncst.matroid.algorithm.BaseMatroidIntersectionAlgorithm;
import by.zsp.ncst.matroid.algorithm.MatroidIntersectionAlgorithm;
import by.zsp.ncst.graph.Edge;
import by.zsp.ncst.graph.Graph;
import by.zsp.ncst.graph.Vertex;
import by.zsp.ncst.graph.impl.UndirectedGraphWithIntersections;
import by.zsp.ncst.matroid.Matroid;
import by.zsp.ncst.matroid.Matroids;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

import java.util.Set;
import java.util.function.Consumer;

public class BaseMatroidIntersectionAlgorithmTest {

    @Test
    public void oneVertex() {
        test(graph -> graph.addVertex(1, 0, 0), 0);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void oneEdge() {
        test(graph -> graph.addEdge(1, 0, 0, 2, 0, 0), 1);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void noIntersections() {
        test(
                graph -> {
                    Vertex<Integer> vertex1 = graph.vertexOf(1, 0, 2);
                    Vertex<Integer> vertex2 = graph.vertexOf(2, 2, 2);
                    Vertex<Integer> vertex3 = graph.vertexOf(3, 1, 1);
                    Vertex<Integer> vertex4 = graph.vertexOf(4, 0, 0);
                    Vertex<Integer> vertex5 = graph.vertexOf(5, 2, 0);

                    graph.addEdge(vertex1, vertex2);
                    graph.addEdge(vertex1, vertex3);
                    graph.addEdge(vertex1, vertex4);
                    graph.addEdge(vertex2, vertex3);
                    graph.addEdge(vertex2, vertex5);
                    graph.addEdge(vertex3, vertex4);
                    graph.addEdge(vertex3, vertex5);
                    graph.addEdge(vertex4, vertex5);
                },
                4);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void intersections() {
        test(
                graph -> {
                    Vertex<Integer> vertex1 = graph.vertexOf(1, 1, 3);
                    Vertex<Integer> vertex2 = graph.vertexOf(2, 3, 3);
                    Vertex<Integer> vertex3 = graph.vertexOf(3, 0, 1);
                    Vertex<Integer> vertex4 = graph.vertexOf(4, 4, 1);
                    Vertex<Integer> vertex5 = graph.vertexOf(5, 1, 0);
                    Vertex<Integer> vertex6 = graph.vertexOf(6, 3, 0);

                    graph.addEdge(vertex1, vertex2);
                    graph.addEdge(vertex1, vertex6);
                    graph.addEdge(vertex2, vertex5);
                    graph.addEdge(vertex3, vertex4);
                    graph.addEdge(vertex3, vertex5);
                    graph.addEdge(vertex4, vertex6);
                },
                4);
    }

    private void test(@NotNull Consumer<Graph<Integer>> graphBuilder, int expectedIntersectionSize) {
        final Graph<Integer> graph = new UndirectedGraphWithIntersections<>();
        graphBuilder.accept(graph);
        final Set<Edge<Integer>> intersection = findIntersection(graph);
        Assert.assertEquals(expectedIntersectionSize, intersection.size());
    }

    private <V> Set<Edge<V>> findIntersection(final @NotNull Graph<V> graph) {
        final Matroid<Edge<V>> cycleMatroid = Matroids.cycleMatroidOf(graph);
        final Matroid<Edge<V>> intersectionMatroid = Matroids.intersectionMatroidOf(graph);

        final MatroidIntersectionAlgorithm<Edge<V>> matroidIntersectionAlgorithm =
                new BaseMatroidIntersectionAlgorithm<>();

        return matroidIntersectionAlgorithm.findIntersection(cycleMatroid, intersectionMatroid);
    }
}
