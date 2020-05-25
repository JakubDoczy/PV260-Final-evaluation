package Analysis;

import Analysis.Data.Dataset;

public interface DatasetFilter<T, D extends Dataset<T>> {
    void filter(D dataset);
}
