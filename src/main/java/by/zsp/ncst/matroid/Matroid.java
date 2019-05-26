package by.zsp.ncst.matroid;

import by.zsp.ncst.util.annotation.NotEmpty;
import by.zsp.ncst.util.annotation.Immutable;
import by.zsp.ncst.exception.MatroidException;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;

@Immutable
public interface Matroid<E> {

    @NotNull @Immutable Set<E> getElements();

    Optional<@NotEmpty Set<E>> findCircuit(@NotNull Set<E> subset) throws MatroidException;
}
