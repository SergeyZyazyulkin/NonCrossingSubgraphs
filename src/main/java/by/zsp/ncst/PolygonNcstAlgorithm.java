package by.zsp.ncst;

import by.zsp.ncst.exception.AlgorithmException;
import by.zsp.ncst.graph.Graph;
import by.zsp.ncst.graph.Vertex;
import by.zsp.ncst.util.annotation.Immutable;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

@Immutable
public interface PolygonNcstAlgorithm<V> {

    Optional<Graph<V>> findNcst(@NotNull Graph<V> graph, @NotNull List<Vertex<V>> polygon) throws AlgorithmException;
}
