package by.zsp.ncst.matroid.algorithm;

import by.zsp.ncst.exception.AlgorithmException;
import by.zsp.ncst.matroid.Matroid;
import by.zsp.ncst.util.CollectionUtils;
import by.zsp.ncst.util.annotation.Immutable;
import by.zsp.ncst.util.annotation.Mutable;
import by.zsp.ncst.util.annotation.NotEmpty;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.collections.SetUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class BaseMatroidIntersectionAlgorithm<E> implements MatroidIntersectionAlgorithm<E> {

    @SuppressWarnings("StatementWithEmptyBody")
    public @NotNull Set<E> findIntersection(final @NotNull Matroid<E> matroid1, final @NotNull Matroid<E> matroid2)
            throws AlgorithmException {

        if (!SetUtils.isEqualSet(matroid1.getElements(), matroid2.getElements())) {
            throw new AlgorithmException("Matroids have different elements");
        }

        final Set<E> intersection = new HashSet<>();

        try {
            while (expand(matroid1, matroid2, intersection));
        } catch (Exception e) {
            throw new AlgorithmException("Internal error", e);
        }

        return intersection;
    }

    private boolean expand(
            final @NotNull Matroid<E> matroid1,
            final @NotNull Matroid<E> matroid2,
            final @NotNull @Mutable Set<E> intersection) {

        assert SetUtils.isEqualSet(matroid1.getElements(), matroid2.getElements());

        // parts and adjacency list of the utility bipartite graph
        final Queue<E> vertices1 = new ArrayDeque<>();
        final Set<E> vertices2 = new HashSet<>();
        final Multimap<E, E> adjacency = HashMultimap.create();

        // ancestors to restore the expanding path
        // null value is used to represent the start point of a path
        final Map<E, @Nullable E> ancestors = new HashMap<>();

        final @NotNull @Immutable Set<E> elements = matroid1.getElements();

        final @NotNull @Immutable Set<E> iteratingElements =
                CollectionUtils.immutableDifferenceOf(elements, intersection);

        for (final E candidate : iteratingElements) {
            intersection.add(candidate);

            final Optional<@NotEmpty Set<E>> circuit1 = matroid1.findCircuit(intersection);
            final Optional<@NotEmpty Set<E>> circuit2 = matroid2.findCircuit(intersection);
            final boolean independentInMatroid1 = !circuit1.isPresent();
            final boolean independentInMatroid2 = !circuit2.isPresent();

            if (independentInMatroid1 && independentInMatroid2) {
                return true;
            }

            if (independentInMatroid1) {
                vertices1.add(candidate);
                ancestors.put(candidate, null);
            } else {
                for (final E element : circuit1.get()) {
                    if (!Objects.equals(element, candidate)) {
                        adjacency.put(element, candidate);
                    }
                }
            }

            if (independentInMatroid2) {
                vertices2.add(candidate);
            } else {
                for (final E element : circuit2.get()) {
                    if (!Objects.equals(element, candidate)) {
                        adjacency.put(candidate, element);
                    }
                }
            }

            intersection.remove(candidate);
        }

        while (!vertices1.isEmpty()) {
            final E visiting = vertices1.remove();

            for (final E neighbour : adjacency.get(visiting)) {
                if (!ancestors.containsKey(neighbour)) {
                    ancestors.put(neighbour, visiting);
                    vertices1.add(neighbour);

                    if (vertices2.contains(neighbour)) {
                        expand(intersection, neighbour, ancestors);
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private void expand(
            final @NotNull @Mutable Set<E> intersection,
            final @NotNull E lastVertex,
            final @NotNull Map<E, E> ancestors) {

        final int initialIntersectionSize = intersection.size();
        final List<E> path = new ArrayList<>();
        E current = lastVertex;

        while (current != null) {
            path.add(current);
            current = ancestors.get(current);
        }

        assert (path.size() & 1) == 1;

        for (int i = path.size() - 1; i >= 0; --i) {
            final E processing = path.get(i);

            if ((i & 1) == 0) {
                final boolean added = intersection.add(processing);
                assert added;
            } else {
                final boolean removed = intersection.remove(processing);
                assert removed;
            }
        }

        assert intersection.size() > initialIntersectionSize;
    }
}
