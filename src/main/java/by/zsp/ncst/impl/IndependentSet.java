package by.zsp.ncst.impl;

import by.zsp.ncst.exception.AlgorithmException;
import by.zsp.ncst.graph.Edge;
import by.zsp.ncst.graph.Graph;
import by.zsp.ncst.graph.Vertex;
import by.zsp.ncst.graph.impl.UndirectedGraphWithIntersections;
import by.zsp.ncst.util.annotation.Fluent;
import by.zsp.ncst.util.annotation.Immutable;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

class IndependentSet<V> {

    private final @NotNull Map<Vertex<V>, Vertex<V>> verticesGroups;
    private final @NotNull Set<Edge<V>> edges;

    public IndependentSet() {
        verticesGroups = new HashMap<>();
        edges = new HashSet<>();
    }

    private IndependentSet(final @NotNull Map<Vertex<V>, Vertex<V>> verticesGroups, final @NotNull Set<Edge<V>> edges) {
        this.verticesGroups = verticesGroups;
        this.edges = edges;
    }

    public @NotNull Graph<V> toGraph() {
        return UndirectedGraphWithIntersections.of(edges);
    }

    public @NotNull @Immutable Set<Edge<V>> getEdges() {
        return ImmutableSet.copyOf(edges);
    }

    @Fluent
    public IndependentSet<V> addAll(final @NotNull Collection<Edge<V>> edges) {
        for (final Edge<V> edge : edges) {
            if (canBeAdded(edge)) {
                add(edge);
            }
        }

        return this;
    }

    @Fluent
    public IndependentSet<V> add(final @NotNull Edge<V> edge) throws AlgorithmException {
        if (canBeAdded(edge)) {
            edges.add(edge);
            final ImmutablePair<Vertex<V>, Vertex<V>> vertices = edge.asVerticesPair();
            verticesGroups.put(getGroup(vertices.left), getGroup(vertices.right));
            return this;
        } else {
            throw new AlgorithmException("Edge connects vertices from the same group");
        }
    }

    public boolean canBeAdded(final @NotNull Edge<V> edge) {
        final ImmutablePair<Vertex<V>, Vertex<V>> vertices = edge.asVerticesPair();
        return !Objects.equals(getGroup(vertices.left), getGroup(vertices.right));
    }

    private @NotNull Vertex<V> getGroup(final @NotNull Vertex<V> vertex) {
        if (verticesGroups.containsKey(vertex)) {
            final Vertex<V> parent = verticesGroups.get(vertex);

            if (Objects.equals(vertex, parent)) {
                return vertex;
            } else {
                final Vertex<V> group = getGroup(parent);
                verticesGroups.put(vertex, group);
                return group;
            }
        } else {
            verticesGroups.put(vertex, vertex);
            return vertex;
        }
    }

    public @NotNull IndependentSet<V> copy() {
        return new IndependentSet<>(new HashMap<>(verticesGroups), new HashSet<>(edges));
    }

    public @NotNull IndependentSet<V> filter(final @NotNull Collection<Vertex<V>> vertices) {
        final IndependentSet<V> filtered = new IndependentSet<>();

        edges.stream()
                .filter(edge -> vertices.containsAll(edge.asStream().collect(Collectors.toList())))
                .forEach(filtered::add);

        return filtered;
    }
}
