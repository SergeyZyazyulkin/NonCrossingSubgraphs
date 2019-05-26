package by.zsp.ncst.graph.impl;

import by.zsp.ncst.graph.Edge;
import by.zsp.ncst.graph.Vertex;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

class SimpleUndirectedEdge<V> implements Edge<V> {

    @NotNull
    private final Vertex<V> vertex1;
    @NotNull
    private final Vertex<V> vertex2;

    SimpleUndirectedEdge(final @NotNull Vertex<V> vertex1, final @NotNull Vertex<V> vertex2) {
        this.vertex1 = vertex1;
        this.vertex2 = vertex2;
    }

    @Override
    public @NotNull ImmutablePair<Vertex<V>, Vertex<V>> asVerticesPair() {
        return ImmutablePair.of(vertex1, vertex2);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        @NotNull SimpleUndirectedEdge<?> anotherEdge = (SimpleUndirectedEdge<?>) o;

        return Objects.equals(vertex1, anotherEdge.vertex1) && Objects.equals(vertex2, anotherEdge.vertex2) ||
                Objects.equals(vertex1, anotherEdge.vertex2) && Objects.equals(vertex2, anotherEdge.vertex1);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vertex1, vertex2) * Objects.hash(vertex2, vertex1);
    }
}
