package by.zsp.ncst.graph;

import by.zsp.ncst.util.annotation.Immutable;
import com.google.common.collect.Lists;
import com.vividsolutions.jts.triangulate.Segment;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

// V - vertex ID type
// directed and undirected edges (v1, v2) and (v1, v2) must be equal
// undirected edges (v1, v2) and (v2, v1) must be equal
@Immutable
public interface Edge<V> {

    @NotNull ImmutablePair<Vertex<V>, Vertex<V>> asVerticesPair();

    default @NotNull @Immutable Segment asSegment() {
        final ImmutablePair<Vertex<V>, Vertex<V>> verticesPair = asVerticesPair();
        return new Segment(verticesPair.left.getCoordinates(), verticesPair.right.getCoordinates());
    }

    default @NotNull Stream<Vertex<V>> asStream() {
        final ImmutablePair<Vertex<V>, Vertex<V>> vertices = asVerticesPair();
        return Lists.newArrayList(vertices.left, vertices.right).stream();
    }
}
