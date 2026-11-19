import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Tao bieu do SVG tu results.csv, khong can cai them thu vien. */
public final class ChartGenerator {
    private static final String[] COLORS = {"#7c3aed", "#06b6d4", "#f59e0b"};

    private ChartGenerator() {
    }

    public static void main(String[] args) throws IOException {
        Path input = args.length >= 1 ? Path.of(args[0]) : Path.of("results", "results.csv");
        Path output = args.length >= 2 ? Path.of(args[1]) : Path.of("results", "random-cutoff-chart.svg");
        Map<Integer, List<Point>> bySize = readRandomMedians(input);
        writeSvg(output, bySize);
        System.out.println("Da tao " + output);
    }

    private static Map<Integer, List<Point>> readRandomMedians(Path input) throws IOException {
        Map<Integer, List<Point>> bySize = new HashMap<>();
        try (BufferedReader reader = Files.newBufferedReader(input)) {
            reader.readLine(); // header
            String line;
            while ((line = reader.readLine()) != null) {
                String[] cells = line.split(",");
                if (!"RANDOM".equals(cells[0])) {
                    continue;
                }
                int size = Integer.parseInt(cells[1]);
                int cutoff = Integer.parseInt(cells[2]);
                double medianMillis = Double.parseDouble(cells[6]);
                bySize.computeIfAbsent(size, ignored -> new ArrayList<>())
                        .add(new Point(cutoff, medianMillis));
            }
        }
        for (List<Point> points : bySize.values()) {
            points.sort(Comparator.comparingInt(point -> point.cutoff));
        }
        return bySize;
    }

    private static void writeSvg(Path output, Map<Integer, List<Point>> bySize) throws IOException {
        int width = 1000;
        int height = 620;
        int left = 95;
        int right = 40;
        int top = 75;
        int bottom = 100;
        int chartWidth = width - left - right;
        int chartHeight = height - top - bottom;

        List<Integer> sizes = new ArrayList<>(bySize.keySet());
        sizes.sort(Integer::compareTo);
        int minCutoff = Integer.MAX_VALUE;
        int maxCutoff = Integer.MIN_VALUE;
        double maxMedian = 0.0;
        for (List<Point> points : bySize.values()) {
            for (Point point : points) {
                minCutoff = Math.min(minCutoff, point.cutoff);
                maxCutoff = Math.max(maxCutoff, point.cutoff);
                maxMedian = Math.max(maxMedian, point.medianMillis);
            }
        }

        try (BufferedWriter writer = Files.newBufferedWriter(output)) {
            writer.write("<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"1000\" height=\"620\" viewBox=\"0 0 1000 620\">\n");
            writer.write("<rect width=\"100%\" height=\"100%\" fill=\"#101827\" rx=\"18\"/>\n");
            writer.write("<text x=\"95\" y=\"42\" fill=\"#f8fafc\" font-family=\"sans-serif\" font-size=\"25\" font-weight=\"bold\">Mergesort CUTOFF benchmark — random input</text>\n");
            writer.write("<text x=\"95\" y=\"65\" fill=\"#94a3b8\" font-family=\"sans-serif\" font-size=\"14\">Lower median time is better</text>\n");

            for (int step = 0; step <= 5; step++) {
                double value = maxMedian * step / 5.0;
                double y = top + chartHeight - chartHeight * step / 5.0;
                writer.write(String.format(Locale.US, "<line x1=\"%d\" y1=\"%.1f\" x2=\"%d\" y2=\"%.1f\" stroke=\"#334155\"/>%n", left, y, width - right, y));
                writer.write(String.format(Locale.US, "<text x=\"12\" y=\"%.1f\" fill=\"#cbd5e1\" font-family=\"sans-serif\" font-size=\"13\">%.2f ms</text>%n", y + 5, value));
            }

            for (int cutoff = minCutoff; cutoff <= maxCutoff; cutoff += 4) {
                double x = scale(cutoff, minCutoff, maxCutoff, left, left + chartWidth);
                writer.write(String.format(Locale.US, "<text x=\"%.1f\" y=\"545\" fill=\"#cbd5e1\" font-family=\"sans-serif\" font-size=\"12\" text-anchor=\"middle\">%d</text>%n", x, cutoff));
            }
            writer.write("<text x=\"500\" y=\"585\" fill=\"#e2e8f0\" font-family=\"sans-serif\" font-size=\"15\" text-anchor=\"middle\">CUTOFF</text>\n");

            for (int index = 0; index < sizes.size(); index++) {
                int size = sizes.get(index);
                String color = COLORS[index % COLORS.length];
                List<Point> points = bySize.get(size);
                StringBuilder path = new StringBuilder();
                for (int pointIndex = 0; pointIndex < points.size(); pointIndex++) {
                    Point point = points.get(pointIndex);
                    double x = scale(point.cutoff, minCutoff, maxCutoff, left, left + chartWidth);
                    double y = scale(point.medianMillis, 0, maxMedian, top + chartHeight, top);
                    path.append(pointIndex == 0 ? "M" : " L").append(String.format(Locale.US, "%.1f %.1f", x, y));
                }
                writer.write(String.format("<path d=\"%s\" fill=\"none\" stroke=\"%s\" stroke-width=\"3\"/>%n", path, color));
                double legendX = 650 + index * 105;
                writer.write(String.format("<line x1=\"%.0f\" y1=\"40\" x2=\"%.0f\" y2=\"40\" stroke=\"%s\" stroke-width=\"4\"/>%n", legendX, legendX + 20, color));
                writer.write(String.format("<text x=\"%.0f\" y=\"45\" fill=\"#e2e8f0\" font-family=\"sans-serif\" font-size=\"12\">n=%d</text>%n", legendX + 26, size));
            }
            writer.write("</svg>\n");
        }
    }

    private static double scale(double value, double sourceMin, double sourceMax, double targetMin, double targetMax) {
        if (sourceMax == sourceMin) {
            return (targetMin + targetMax) / 2.0;
        }
        return targetMin + (value - sourceMin) * (targetMax - targetMin) / (sourceMax - sourceMin);
    }

    private static final class Point {
        private final int cutoff;
        private final double medianMillis;

        private Point(int cutoff, double medianMillis) {
            this.cutoff = cutoff;
            this.medianMillis = medianMillis;
        }
    }
}
