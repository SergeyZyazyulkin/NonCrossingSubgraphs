package by.zsp.ncst.util;

import by.zsp.ncst.graph.Edge;
import by.zsp.ncst.graph.Vertex;
import by.zsp.ncst.util.annotation.Immutable;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.triangulate.Segment;
import org.jetbrains.annotations.NotNull;

public class Geometry {

    private Geometry() {}

    public static <V> boolean isIntersecting(@NotNull Edge<V> edge1, @NotNull Edge<V> edge2) {
        final @NotNull @Immutable Segment segment1 = edge1.asSegment();
        final @NotNull @Immutable Segment segment2 = edge2.asSegment();
        final @Immutable Coordinate intersection = segment1.intersection(segment2);

        return intersection != null &&
                !intersection.equals2D(segment1.getStart()) &&
                !intersection.equals2D(segment1.getEnd()) &&
                !intersection.equals2D(segment2.getStart()) &&
                !intersection.equals2D(segment2.getEnd());
    }

    public static double distance(final @NotNull Vertex<?> v1, final @NotNull Vertex<?> v2) {
        return v1.getCoordinates().distance(v2.getCoordinates());
    }
}
