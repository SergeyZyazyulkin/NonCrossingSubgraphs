package by.zsp.ncst.graph;

import by.zsp.ncst.util.annotation.Immutable;
import com.vividsolutions.jts.geom.Coordinate;
import org.jetbrains.annotations.NotNull;

// Vertices with the same ID must be equal
@Immutable
public interface Vertex<V> {

    @NotNull @Immutable V getId();

    // (x, y)
    @NotNull @Immutable Coordinate getCoordinates();
}
