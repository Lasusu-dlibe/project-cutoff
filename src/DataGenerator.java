import java.util.Random;

/** Tao cac loai input dung chung cho moi cutoff de benchmark cong bang. */
public final class DataGenerator {
    public enum Pattern {
        RANDOM,
        SORTED,
        REVERSED,
        FEW_UNIQUE
    }

    private DataGenerator() {
    }

    public static Integer[] create(int size, Pattern pattern, long seed) {
        if (size < 0) {
            throw new IllegalArgumentException("size must be >= 0");
        }

        Integer[] values = new Integer[size];
        Random random = new Random(seed);

        switch (pattern) {
            case RANDOM:
                for (int index = 0; index < size; index++) {
                    values[index] = random.nextInt();
                }
                break;
            case SORTED:
                for (int index = 0; index < size; index++) {
                    values[index] = index;
                }
                break;
            case REVERSED:
                for (int index = 0; index < size; index++) {
                    values[index] = size - index;
                }
                break;
            case FEW_UNIQUE:
                for (int index = 0; index < size; index++) {
                    values[index] = random.nextInt(10);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown pattern: " + pattern);
        }

        return values;
    }
}
