package by.zsp.ncst.matroid;

import by.zsp.ncst.exception.MatroidException;
import by.zsp.ncst.graph.Graph;
import by.zsp.ncst.graph.Edge;
import by.zsp.ncst.util.Geometry;
import by.zsp.ncst.util.annotation.NotEmpty;
import by.zsp.ncst.util.annotation.Immutable;
import com.google.common.collect.Sets;
import org.jetbrains.annotations.NotNull;

import java.util.*;

class IntersectionMatroid<V> extends GraphMatroid<V> {

    // edges that aren't in the map don't intersect
    @NotNull
    @Immutable
    private final Map<Edge<V>, Integer> intersectionGroups;

    IntersectionMatroid(final @NotNull Graph<V> graph, final boolean validate) throws MatroidException {
        super(graph);
        intersectionGroups = Collections.unmodifiableMap(buildIntersectionGroups(validate));
    }

    private @NotNull Map<Edge<V>, Integer> buildIntersectionGroups(final boolean validate) throws MatroidException {
        final Map<Edge<V>, Integer> intersectionGroups = new HashMap<>();
        final List<Edge<V>> ordered = new ArrayList<>(graph.getEdges());
        int intersectionGroup = 0;

        for (int i = 0; i < ordered.size(); i++) {
            final Edge<V> edge1 = ordered.get(i);

            for (int j = 0; j < i; j++) {
                final Edge<V> edge2 = ordered.get(j);

                if (Geometry.isIntersecting(edge1, edge2)) {
                    final int currentIntersectionGroup;

                    if (!intersectionGroups.containsKey(edge2)) {
                        currentIntersectionGroup = intersectionGroup++;
                        intersectionGroups.put(edge2, currentIntersectionGroup);
                    } else {
                        currentIntersectionGroup = intersectionGroups.get(edge2);
                    }

                    if (!intersectionGroups.containsKey(edge1)) {
                        intersectionGroups.put(edge1, currentIntersectionGroup);

                        if (!validate) {
                            break;
                        }
                    } else if (currentIntersectionGroup != intersectionGroups.get(edge1)) {
                        throw new MatroidException("Intersection matroid can't be built for this graph");
                    }
                }
            }
        }

        return intersectionGroups;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Optional<@NotEmpty Set<Edge<V>>> findCircuitChecked(final @NotNull @NotEmpty Set<Edge<V>> subset) {
        final Map<Integer, Edge<V>> intersectingVisited = new HashMap<>();

        for (final Edge<V> edge : subset) {
            if (intersectionGroups.containsKey(edge)) {
                final int intersectionGroup = intersectionGroups.get(edge);

                if (intersectingVisited.containsKey(intersectionGroup)) {
                    final Edge<V> intersecting = intersectingVisited.get(intersectionGroup);
                    final Set<Edge<V>> circuit = Sets.newHashSet(intersecting, edge);
                    return Optional.of(circuit);
                } else {
                    intersectingVisited.put(intersectionGroup, edge);
                }
            }
        }

        return Optional.empty();
    }
}
