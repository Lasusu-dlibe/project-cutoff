
public final class MergeSortWithCutoff {
    private MergeSortWithCutoff() {
    }

    public static void sort(Comparable[] a, int CUTOFF) {
        if (a == null) {
            throw new IllegalArgumentException("a must not be null");
        }
        if (CUTOFF < 0) {
            throw new IllegalArgumentException("CUTOFF must be >= 0");
        }

        Comparable[] aux = new Comparable[a.length];
        sort(a, aux, 0, a.length - 1, CUTOFF);
    }

    // Sap xep a[lo..hi], dung aux[] lam mang phu.
    private static void sort(Comparable[] a, Comparable[] aux, int lo, int hi, int CUTOFF) {
        if (hi <= lo) {
            return;
        }

        if (hi <= lo + CUTOFF - 1) {
            insertionSort(a, lo, hi);
            return;
        }

        int mid = lo + (hi - lo) / 2;
        sort(a, aux, lo, mid, CUTOFF);
        sort(a, aux, mid + 1, hi, CUTOFF);

        // Cai tien trong giao trinh: neu hai nua da dung thu tu, khong can merge.
        if (!less(a[mid + 1], a[mid])) {
            return;
        }
        merge(a, aux, lo, mid, hi);
    }

    // Tron hai doan da co thu tu: a[lo..mid] va a[mid+1..hi].
    private static void merge(Comparable[] a, Comparable[] aux, int lo, int mid, int hi) {
        for (int k = lo; k <= hi; k++) {
            aux[k] = a[k];
        }

        int i = lo;
        int j = mid + 1;
        for (int k = lo; k <= hi; k++) {
            if (i > mid) {
                a[k] = aux[j++];
            } else if (j > hi) {
                a[k] = aux[i++];
            } else if (less(aux[j], aux[i])) {
                a[k] = aux[j++];
            } else {
                a[k] = aux[i++];
            }
        }
    }

    // Insertion sort cho doan a[lo..hi].
    private static void insertionSort(Comparable[] a, int lo, int hi) {
        for (int i = lo; i <= hi; i++) {
            for (int j = i; j > lo && less(a[j], a[j - 1]); j--) {
                exch(a, j, j - 1);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static boolean less(Comparable v, Comparable w) {
        return v.compareTo(w) < 0;
    }

    private static void exch(Comparable[] a, int i, int j) {
        Comparable t = a[i];
        a[i] = a[j];
        a[j] = t;
    }
}
