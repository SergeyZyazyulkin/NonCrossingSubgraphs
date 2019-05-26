package by.zsp.ncst.util;

import by.zsp.ncst.util.annotation.Immutable;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CollectionUtils {

    private CollectionUtils() {}

    public static <E> @NotNull Set<E> differenceOf(final @NotNull Set<E> minuend, final @NotNull Set<E> subtrahend) {
        final Set<E> result = new HashSet<>(minuend);
        result.removeAll(subtrahend);
        return result;
    }

    public static <E> @NotNull @Immutable Set<E> immutableDifferenceOf(
            final @NotNull Set<E> minuend, final @NotNull Set<E> subtrahend) {

        return Collections.unmodifiableSet(differenceOf(minuend, subtrahend));
    }
}
