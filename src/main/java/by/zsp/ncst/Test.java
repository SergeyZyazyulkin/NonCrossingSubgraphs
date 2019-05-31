package by.zsp.ncst;

import by.zsp.ncst.graph.Vertex;
import by.zsp.ncst.graph.impl.UndirectedGraphWithIntersections;
import by.zsp.ncst.util.Visualizer;
import com.google.common.io.Files;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.jetbrains.annotations.NotNull;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.stream.Stream;

public class Test {

    public static void main(final @NotNull String[] args) {
        drawConcaveGeometricGraph();
    }

    private static void drawGraphWithIntersectionMatroid() {
        final UndirectedGraphWithIntersections<Integer> g = new UndirectedGraphWithIntersections<>();
        g.addEdge(1, 0, 0, 5, 4, 4);
        g.addEdge(5, 4, 4, 6, 7, 4);
        g.addEdge(6, 7, 4, 8, 6, 6);
        g.addEdge(8, 6, 6, 4, 5, 2);
        g.addEdge(2, 6, 0, 7, 1, 5);
        g.addEdge(4, 5, 2, 3, 0, 2);
        g.addEdge(4, 5, 2, 2, 6, 0);
        Visualizer.visualize("graph_with_intersection_matroid", g);
    }

    private static void drawGraphWithIntersectionIndex1() {
        final UndirectedGraphWithIntersections<Integer> g = new UndirectedGraphWithIntersections<>();
        g.addEdge(1, 0, 0, 5, 4, 4);
        g.addEdge(5, 4, 4, 6, 7, 4);
        g.addEdge(6, 7, 4, 8, 6, 6);
        g.addEdge(8, 6, 6, 4, 5, 2);
        g.addEdge(3, 0, 2, 4, 5, 2);
        g.addEdge(4, 5, 2, 2, 6, 0);
        g.addEdge(6, 7, 4, 9, 3, -2);
        Visualizer.visualize("graph_with_intersection_index_1", g);
    }

    private static void drawConcaveGeometricGraph() {
        final XYSeriesCollection data = new XYSeriesCollection();
        addSeries(data, new ImmutablePair<>(0., 0.));
        addSeries(data, new ImmutablePair<>(4., 4.));
        addSeries(data, new ImmutablePair<>(6., 0.));
        addSeries(data, new ImmutablePair<>(0., 2.));
        addSeries(data, new ImmutablePair<>(5., 2.));
        addSeries(data, new ImmutablePair<>(7., 4.));
        addSeries(data, new ImmutablePair<>(1., 5.));
        addSeries(data, new ImmutablePair<>(6., 6.));
        addSeries(data, new ImmutablePair<>(0., 0.), new ImmutablePair<>(4., 4.));
        addSeries(data, new ImmutablePair<>(4., 4.), new ImmutablePair<>(7., 4.));
        addSeries(data, new ImmutablePair<>(7., 4.), new ImmutablePair<>(6., 6.));
        addSeries(data, new ImmutablePair<>(6., 6.), new ImmutablePair<>(5., 2.));
        addSeries(data, new ImmutablePair<>(6., 0.), new ImmutablePair<>(1., 5.));
        addSeries(data, new ImmutablePair<>(5., 2.), new ImmutablePair<>(0., 2.));
        addSeries(data, new ImmutablePair<>(5., 2.), new ImmutablePair<>(6., 0.));
        addSeries(data, new ImmutablePair<>(0., 0.), new ImmutablePair<>(6., 0.));
        addSeries(data, new ImmutablePair<>(0., 0.), new ImmutablePair<>(0., 2.));
        addSeries(data, new ImmutablePair<>(0., 2.), new ImmutablePair<>(1., 5.));
        addSeries(data, new ImmutablePair<>(1., 5.), new ImmutablePair<>(4., 4.));
        addSeries(data, new ImmutablePair<>(4., 4.), new ImmutablePair<>(6., 6.));
        addSeries(data, new ImmutablePair<>(7., 4.), new ImmutablePair<>(5., 2.));

        final JFreeChart chart = ChartFactory.createXYLineChart(
                null, "x", "y", data, PlotOrientation.VERTICAL, false, false, false);

        final XYPlot plot = chart.getXYPlot();
        plot.setBackgroundAlpha(0);
        plot.getDomainAxis().setRange(-1, 8);
        plot.getDomainAxis().setVisible(false);
        plot.getRangeAxis().setRange(-1, 7);
        plot.getRangeAxis().setVisible(false);

        final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        for (int i = 0; i < data.getSeriesCount(); ++i) {
            renderer.setSeriesPaint(i, Color.BLACK);
            renderer.setSeriesShapesVisible(i, true);
            renderer.setSeriesShapesFilled(i, true);

            renderer.setSeriesShape(i, new Ellipse2D.Double(
                    -6, -6, 2 * 6, 2 * 6));

            renderer.setSeriesFillPaint(i, Color.BLACK);

            if (i < 15) {
                renderer.setSeriesStroke(i, new BasicStroke(2));
            } else {
                renderer.setSeriesStroke(
                        i,
                        new BasicStroke(
                                2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                                1.0f, new float[] {6.0f, 6.0f}, 0.0f
                        )
                );
            }
        }

        plot.setRenderer(renderer);

        final double xSize = 9;
        final double ySize = 8;
        final int MAX_CHART_SIZE = 800;
        final int width = xSize < ySize ? (int) (xSize / ySize * MAX_CHART_SIZE) : MAX_CHART_SIZE;
        final int height = xSize < ySize ? MAX_CHART_SIZE : (int) (ySize / xSize * MAX_CHART_SIZE);

        File xyChart = new File(Paths.get("charts", "concave_geometric_graph" + ".png").toString());

        try {
            Files.createParentDirs(xyChart);
            ChartUtilities.saveChartAsPNG(xyChart, chart, width, height);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SafeVarargs
    private static void addSeries(
            final @NotNull XYSeriesCollection collection,
            final @NotNull ImmutablePair<Double, Double>... points) {

        final XYSeries series = new XYSeries(UUID.randomUUID());

        for (final ImmutablePair<Double, Double> point : points) {
            series.add(point.left, point.right);
        }

        collection.addSeries(series);
    }
}
