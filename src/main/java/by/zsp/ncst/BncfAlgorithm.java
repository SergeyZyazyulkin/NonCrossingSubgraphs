package by.zsp.ncst;

import by.zsp.ncst.exception.AlgorithmException;
import by.zsp.ncst.graph.Graph;
import by.zsp.ncst.util.annotation.Immutable;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@Immutable
public interface BncfAlgorithm<V> {

    @NotNull Graph<V> findBncf(final @NotNull Graph<V> graph) throws AlgorithmException;
}
