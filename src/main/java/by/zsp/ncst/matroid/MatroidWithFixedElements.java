package by.zsp.ncst.matroid;

import by.zsp.ncst.exception.MatroidException;
import by.zsp.ncst.util.CollectionUtils;
import by.zsp.ncst.util.annotation.Immutable;
import by.zsp.ncst.util.annotation.NotEmpty;
import com.google.common.collect.ImmutableSet;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

class MatroidWithFixedElements<E> implements Matroid<E> {

    @NotNull
    private final Matroid<E> matroid;
    @NotNull
    @Immutable
    private final Set<E> fixedElements;
    @NotNull
    @Immutable
    private final Set<E> notFixedElements;

    MatroidWithFixedElements(final @NotNull Matroid<E> matroid, final @NotNull Set<E> fixedElements)
            throws MatroidException {

        if (!matroid.getElements().containsAll(fixedElements)) {
            throw new MatroidException("Matroid doesn't contain all fixed elements");
        }

        if (matroid.findCircuit(fixedElements).isPresent()) {
            throw new MatroidException("Fixed elements aren't independent subset of elements");
        }

        this.matroid = matroid;
        this.fixedElements = ImmutableSet.copyOf(fixedElements);
        this.notFixedElements = CollectionUtils.immutableDifferenceOf(matroid.getElements(), fixedElements);
    }

    @Override
    public @NotNull @Immutable Set<E> getElements() {
        return notFixedElements;
    }

    @Override
    public Optional<@NotEmpty Set<E>> findCircuit(@NotNull Set<E> subset) throws MatroidException {
        final Set<E> subsetWithFixedElements = new HashSet<>(subset);
        subsetWithFixedElements.addAll(fixedElements);
        return matroid.findCircuit(subsetWithFixedElements);
    }
}
