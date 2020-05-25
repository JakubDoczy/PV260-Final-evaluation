package Analysis;

import Analysis.Data.Dataset;
import Writer.Reportable;

/**
 * Can be registered for data analysis and report results
 *
 * @param <T> Type of data.
 */
public interface AnalyticalMethod<T, A extends Analyser<T, Dataset<T>>> extends Reportable {

    void register(A analyser);
}
