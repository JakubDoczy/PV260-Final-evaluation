package Analysis;

import Analysis.Data.Dataset;

import java.util.Iterator;
import java.util.function.Function;

public class BasicFilter<T> implements DatasetFilter<T, Dataset<T>> {

    private final Function<T, Boolean> predicate;

    BasicFilter(Function<T, Boolean> predicate) {
        this.predicate = predicate;
    }

    @Override
    public void filter(Dataset<T> dataset) {
        Iterator<T> iterator = dataset.iterator();

        while (iterator.hasNext()) {
            T currentData = iterator.next();

            if (!predicate.apply(currentData)) {
                iterator.remove();
            }
        }
    }

    @Override
    public String toString() {
        return "Basic Filter with predicate " + predicate.toString();
    }
}
