package by.zsp.ncst.graph.impl;

import by.zsp.ncst.graph.Vertex;
import by.zsp.ncst.util.annotation.Immutable;
import com.vividsolutions.jts.geom.Coordinate;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

class SimpleVertex<V> implements Vertex<V> {

    @NotNull
    private final V id;
    @NotNull
    private final Coordinate coordinates;

    SimpleVertex(@NotNull V id, double x, double y) {
        this(id, new Coordinate(x, y));
    }

    SimpleVertex(@NotNull V id, @NotNull Coordinate coordinates) {
        this.id = id;
        this.coordinates = (Coordinate) coordinates.clone();
    }

    @Override
    public @NotNull @Immutable V getId() {
        return id;
    }

    @Override
    public @NotNull @Immutable Coordinate getCoordinates() {
        return (Coordinate) coordinates.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleVertex<?> that = (SimpleVertex<?>) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
