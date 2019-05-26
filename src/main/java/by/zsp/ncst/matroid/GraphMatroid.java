package by.zsp.ncst.matroid;

import by.zsp.ncst.exception.MatroidException;
import by.zsp.ncst.graph.Graph;
import by.zsp.ncst.graph.Edge;
import by.zsp.ncst.util.annotation.NotEmpty;
import by.zsp.ncst.util.annotation.Immutable;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;

abstract class GraphMatroid<V> implements Matroid<Edge<V>> {

    @NotNull
    @Immutable
    protected final Graph<V> graph;

    protected GraphMatroid(final @NotNull Graph<V> graph) {
        this.graph = graph.copy();
    }

    @Override
    public @NotNull @Immutable Set<Edge<V>> getElements() {
        return graph.getEdges();
    }

    @Override
    public final Optional<@NotEmpty Set<Edge<V>>> findCircuit(final @NotNull Set<Edge<V>> subset)
            throws MatroidException {

        if (!getElements().containsAll(subset)) {
            throw new MatroidException("The given set isn't a subset of matroid elements");
        }

        if (subset.isEmpty()) {
            return Optional.empty();
        } else {
            return findCircuitChecked(subset);
        }
    }

    protected abstract Optional<@NotEmpty Set<Edge<V>>> findCircuitChecked(@NotNull @NotEmpty Set<Edge<V>> subset)
            throws MatroidException;
}
