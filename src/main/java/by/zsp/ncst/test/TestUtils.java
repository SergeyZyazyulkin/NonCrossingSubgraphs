package by.zsp.ncst.test;

import by.zsp.ncst.graph.Edge;
import by.zsp.ncst.graph.Graph;
import by.zsp.ncst.graph.Vertex;
import by.zsp.ncst.util.Geometry;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import org.apache.commons.lang3.RandomUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class TestUtils {

    private static final double MIN_COORD = 0;
    private static final double MAX_COORD = 1;
    private static final double MAX_EDGE_LENGTH_SCALING = 3;
    private static final int MAX_GENERATION_ATTEMPTS = 1000;

    private TestUtils() {}

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

    static void generateRandomPolygonGraph(
            final @NotNull Graph<Integer> graph, final @NotNull List<Vertex<Integer>> polygon,
            final int verticesNumber, final int edgesNumber) {

        assert verticesNumber > 1;
        assert verticesNumber % 4 == 0;
        assert edgesNumber > 0;

        final int verticesQuarter = verticesNumber / 4;
        final double scale = 10.0 / verticesQuarter / (verticesQuarter - 1);

        polygon.add(graph.vertexOf(0, 0, 0));
        Coordinate previousCoordinates = polygon.get(0).getCoordinates();


        for (int i = 1; i < verticesQuarter; ++i) {
            final Vertex<Integer> currentVertex = graph.vertexOf(
                    i,
                    previousCoordinates.x + i * scale,
                    previousCoordinates.y + (verticesQuarter - i) * scale);

            polygon.add(currentVertex);
            previousCoordinates = currentVertex.getCoordinates();
        }

        polygon.add(graph.vertexOf(verticesQuarter, 6, 5));
        previousCoordinates = polygon.get(verticesQuarter).getCoordinates();

        for (int i = 1; i < verticesQuarter; ++i) {
            final Vertex<Integer> currentVertex = graph.vertexOf(
                    verticesQuarter + i,
                    previousCoordinates.x + i * scale,
                    previousCoordinates.y - (verticesQuarter - i) * scale);

            polygon.add(currentVertex);
            previousCoordinates = currentVertex.getCoordinates();
        }

        polygon.add(graph.vertexOf(2 * verticesQuarter, 11, -1));
        previousCoordinates = polygon.get(2 * verticesQuarter).getCoordinates();

        for (int i = 1; i < verticesQuarter; ++i) {
            final Vertex<Integer> currentVertex = graph.vertexOf(
                    2 * verticesQuarter + i,
                    previousCoordinates.x - i * scale,
                    previousCoordinates.y - (verticesQuarter - i) * scale);

            polygon.add(currentVertex);
            previousCoordinates = currentVertex.getCoordinates();
        }

        polygon.add(graph.vertexOf(3 * verticesQuarter, 5, -6));
        previousCoordinates = polygon.get(3 * verticesQuarter).getCoordinates();

        for (int i = 1; i < verticesQuarter; ++i) {
            final Vertex<Integer> currentVertex = graph.vertexOf(
                    3 * verticesQuarter + i,
                    previousCoordinates.x - i * scale,
                    previousCoordinates.y + (verticesQuarter - i) * scale);

            polygon.add(currentVertex);
            previousCoordinates = currentVertex.getCoordinates();
        }

        final List<Vertex<Integer>> closedPolygon = new ArrayList<>(polygon);
        closedPolygon.add(closedPolygon.get(0));

        final Coordinate[] coordinates = closedPolygon.stream()
                .map(Vertex::getCoordinates)
                .toArray(Coordinate[]::new);

        final GeometryFactory geometryFactory = new GeometryFactory();
        final LinearRing ring = geometryFactory.createLinearRing(coordinates);
        final Polygon geomPolygon = geometryFactory.createPolygon(ring);

        final List<Edge<Integer>> edges = IntStream.range(0, edgesNumber)
                .mapToObj(i -> {
                    final int first = RandomUtils.nextInt(0, verticesNumber);
                    int second;
                    Edge<Integer> edge;
                    int attempts = 0;

                    do {
                        second = RandomUtils.nextInt(0, verticesNumber);
                        edge = graph.edgeOf(polygon.get(first), polygon.get(second));
                        attempts++;
                    } while (second == first || (attempts < MAX_GENERATION_ATTEMPTS &&
                            !geomPolygon.covers(geometryFactory.createLineString(edge.asCoordinates()))));

                    return edge;
                })
                .collect(Collectors.toList());

        polygon.forEach(graph::addVertex);
        edges.forEach(graph::addEdge);
    }
}
