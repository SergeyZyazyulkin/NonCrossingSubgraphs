package by.zsp.ncst.util;

import by.zsp.ncst.graph.Graph;
import by.zsp.ncst.graph.Vertex;
import com.google.common.io.Files;
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

public class Visualizer {

    private static final String DIRECTORY = "charts";
    private static final int MAX_CHART_SIZE = 800;
    private static final int POINT_RADIUS = 4;
    private static final int LINE_WIDTH = 2;

    public static void visualize(final @NotNull String name, final @NotNull Graph<?> graph) {
        final XYSeriesCollection xySeriesCollection = new XYSeriesCollection();

        graph.getVertices().stream()
                .map(v -> buildXYSeries(Stream.of(v)))
                .forEach(xySeriesCollection::addSeries);

        graph.getEdges().stream()
                .map(e -> buildXYSeries(e.asStream()))
                .forEach(xySeriesCollection::addSeries);

        final double minX = graph.getVertices().stream()
                .map(Vertex::getCoordinates)
                .mapToDouble(c -> c.x)
                .min()
                .orElse(0);

        final double maxX = graph.getVertices().stream()
                .map(Vertex::getCoordinates)
                .mapToDouble(c -> c.x)
                .max()
                .orElse(1);

        final double minY = graph.getVertices().stream()
                .map(Vertex::getCoordinates)
                .mapToDouble(c -> c.y)
                .min()
                .orElse(0);

        final double maxY = graph.getVertices().stream()
                .map(Vertex::getCoordinates)
                .mapToDouble(c -> c.y)
                .max()
                .orElse(1);

        final double xSize = Math.max(maxX - minX, 0.1);
        final double ySize = Math.max(maxY - minY, 0.1);

        final JFreeChart chart = ChartFactory.createXYLineChart(
                null, "x", "y", xySeriesCollection, PlotOrientation.VERTICAL, false, false, false);

        final XYPlot plot = chart.getXYPlot();
        plot.getDomainAxis().setRange(minX - 0.1 * xSize, maxX + 0.1 * xSize);
        plot.getRangeAxis().setRange(minY - 0.1 * ySize, maxY + 0.1 * ySize);

        final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        for (int i = 0; i < xySeriesCollection.getSeriesCount(); ++i) {
            renderer.setSeriesPaint(i, Color.BLACK);
            renderer.setSeriesShapesVisible(i, true);
            renderer.setSeriesShapesFilled(i, true);

            renderer.setSeriesShape(i, new Ellipse2D.Double(
                    -POINT_RADIUS, -POINT_RADIUS, 2 * POINT_RADIUS, 2 * POINT_RADIUS));

            renderer.setSeriesFillPaint(i, Color.BLACK);
            renderer.setSeriesStroke(i, new BasicStroke(LINE_WIDTH));
        }

        plot.setRenderer(renderer);

        final int width = xSize < ySize ? (int) (xSize / ySize * MAX_CHART_SIZE) : MAX_CHART_SIZE;
        final int height = xSize < ySize ? MAX_CHART_SIZE : (int) (ySize / xSize * MAX_CHART_SIZE);

        File xyChart = new File(Paths.get(DIRECTORY, name + ".png").toString());

        try {
            Files.createParentDirs(xyChart);
            ChartUtilities.saveChartAsPNG(xyChart, chart, width, height);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static @NotNull XYSeries buildXYSeries(final @NotNull Stream<? extends Vertex<?>> vertices) {
        final XYSeries series = new XYSeries(UUID.randomUUID());

        vertices.map(Vertex::getCoordinates)
                .forEach(c -> series.add(c.x, c.y));

        return series;
    }
}
