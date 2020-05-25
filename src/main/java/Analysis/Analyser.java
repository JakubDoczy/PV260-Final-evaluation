package Analysis;

import Analysis.Data.Dataset;

/**
 * Analyses dataset. Implementation can subscribe AnalyticalMethods for analysis
 *
 * @param <T> Type of data.
 * @param <D> Type of dataset Dataset.
 */
public interface Analyser<T, D extends Dataset<T>> {

    void analyse(D dataset);
}
