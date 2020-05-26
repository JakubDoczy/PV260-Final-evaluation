package Analysis.Methods;

import Analysis.Data.Dataset;
import Writer.Reporter;

/**
 * Can be registered for data analysis and report results
 *
 * @param <T> Type of data.
 */
public interface AnalyticalMethod<T, A extends Analyser<T, Dataset<T>>> {

    void register(A analyser);
}
