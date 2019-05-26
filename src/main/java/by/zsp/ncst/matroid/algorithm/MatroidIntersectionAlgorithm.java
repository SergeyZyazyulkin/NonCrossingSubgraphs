package by.zsp.ncst.matroid.algorithm;

import by.zsp.ncst.exception.AlgorithmException;
import by.zsp.ncst.matroid.Matroid;
import by.zsp.ncst.util.annotation.Immutable;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

@Immutable
public interface MatroidIntersectionAlgorithm<E> {

    @NotNull Set<E> findIntersection(final @NotNull Matroid<E> matroid1, final @NotNull Matroid<E> matroid2)
            throws AlgorithmException;
}
