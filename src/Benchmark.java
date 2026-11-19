import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * Chay benchmark cutoff va ghi CSV. Co the truyen: <duong-dan-CSV> <cutoff1,cutoff2,...>.
 * Do chi tinh thoi gian sort, khong tinh tao/copy input hay ghi file.
 */
public final class Benchmark {
    private static final int WARMUP_RUNS = 5;
    private static final int MEASURED_RUNS = 15;
    private static final int[] CUTOFFS = {0, 2, 4, 6, 7, 8, 10, 12, 16, 20, 24, 32};
    private static final int[] SIZES = {1_000, 10_000, 100_000};
    private static final long SEED = 20261103L;

    private Benchmark() {
    }

    public static void main(String[] args) throws IOException {
        Path output = args.length >= 1 ? Path.of(args[0]) : Path.of("results", "results.csv");
        int[] cutoffs = args.length >= 2 ? parseCutoffs(args[1]) : CUTOFFS;
        Files.createDirectories(output.getParent());

        try (BufferedWriter writer = Files.newBufferedWriter(output)) {
            writer.write("pattern,size,cutoff,warmup_runs,measured_runs,mean_ms,median_ms,min_ms,max_ms\n");
            for (DataGenerator.Pattern pattern : DataGenerator.Pattern.values()) {
                for (int size : SIZES) {
                    Integer[] source = DataGenerator.create(size, pattern, SEED + size);
                    for (int cutoff : cutoffs) {
                        Result result = measure(source, cutoff);
                        writer.write(String.format(
                                "%s,%d,%d,%d,%d,%.4f,%.4f,%.4f,%.4f%n",
                                pattern, size, cutoff, WARMUP_RUNS, MEASURED_RUNS,
                                result.meanMillis, result.medianMillis,
                                result.minMillis, result.maxMillis));
                        System.out.printf(
                                "%s n=%d cutoff=%d -> mean %.4f ms, median %.4f ms%n",
                                pattern, size, cutoff, result.meanMillis, result.medianMillis);
                    }
                }
            }
        }

        System.out.println("Da ghi ket qua vao " + output);
    }

    private static int[] parseCutoffs(String text) {
        String[] parts = text.split(",");
        int[] cutoffs = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            cutoffs[i] = Integer.parseInt(parts[i].trim());
            if (cutoffs[i] < 0) {
                throw new IllegalArgumentException("CUTOFF must be >= 0");
            }
        }
        return cutoffs;
    }

    private static Result measure(Integer[] source, int cutoff) {
        for (int run = 0; run < WARMUP_RUNS; run++) {
            Integer[] working = Arrays.copyOf(source, source.length);
            MergeSortWithCutoff.sort(working, cutoff);
            requireSorted(working);
        }

        long[] durations = new long[MEASURED_RUNS];
        for (int run = 0; run < MEASURED_RUNS; run++) {
            Integer[] working = Arrays.copyOf(source, source.length);
            long start = System.nanoTime();
            MergeSortWithCutoff.sort(working, cutoff);
            long end = System.nanoTime();
            requireSorted(working);
            durations[run] = end - start;
        }
        return Result.fromNanoseconds(durations);
    }

    @SuppressWarnings("unchecked")
    private static void requireSorted(Comparable[] a) {
        for (int i = 1; i < a.length; i++) {
            if (a[i].compareTo(a[i - 1]) < 0) {
                throw new IllegalStateException("Sort sai tai chi so " + i);
            }
        }
    }

    private static final class Result {
        private final double meanMillis;
        private final double medianMillis;
        private final double minMillis;
        private final double maxMillis;

        private Result(double meanMillis, double medianMillis, double minMillis, double maxMillis) {
            this.meanMillis = meanMillis;
            this.medianMillis = medianMillis;
            this.minMillis = minMillis;
            this.maxMillis = maxMillis;
        }

        private static Result fromNanoseconds(long[] durations) {
            long[] sorted = Arrays.copyOf(durations, durations.length);
            Arrays.sort(sorted);
            double mean = 0;
            for (long duration : sorted) {
                mean += duration;
            }
            mean /= sorted.length;
            double median = sorted.length % 2 == 0
                    ? (sorted[sorted.length / 2 - 1] + sorted[sorted.length / 2]) / 2.0
                    : sorted[sorted.length / 2];
            return new Result(mean / 1_000_000.0, median / 1_000_000.0,
                    sorted[0] / 1_000_000.0, sorted[sorted.length - 1] / 1_000_000.0);
        }
    }
}
