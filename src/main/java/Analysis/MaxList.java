package Analysis;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Holds data ordered from max to min. Adding data requires only maxSize
 * number of comparisons in worst case scenario (complexity is constant).
 *
 * @param <T> type of data
 */
public class MaxList<T> {

    private List<T> sortedData;
    private Comparator<T> comparator;
    private int maxSize;

    public MaxList(int maxSize, Comparator<T> comparator) {
        assert maxSize > 0;

        sortedData = new ArrayList<>();
        this.comparator = comparator;
        this.maxSize = maxSize;
    }

    public List<T> getSortedData() {
        return sortedData;
    }

    private void swap(int i, int j) {
        T tmp = sortedData.get(i);
        sortedData.set(i, sortedData.get(j));
        sortedData.set(j, tmp);
    }

    /**
     * Moves last value to correct position.
     */
    private void fixOrder() {
        if (sortedData.isEmpty()) {
            return;
        }

        for (int i = sortedData.size() - 1; i > 1; i--) {
            if (comparator.compare(sortedData.get(i - 1), sortedData.get(i)) >= 0) {
                return;
            }
            swap(i, i - 1);
        }
    }

    /**
     * Adds value if Analysis.MaxList has < maxSize elements.
     * If not but value is larger than currently held minimal element,
     * minimal element is replaced by value.
     *
     * @param val Value to be added.
     */
    public void add(T val) {
        if (sortedData.size() < maxSize) {
            sortedData.add(val);
        } else if (comparator.compare(sortedData.get(sortedData.size()-1), val) < 0) {
            sortedData.set(sortedData.size()-1, val);
        } else {
            return;
        }

        fixOrder();
    }

}
