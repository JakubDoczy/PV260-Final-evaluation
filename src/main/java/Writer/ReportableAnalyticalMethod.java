package Writer;

import Analysis.Data.Dataset;
import Analysis.Methods.Analyser;
import Analysis.Methods.AnalyticalMethod;

public interface ReportableAnalyticalMethod<T, A extends Analyser<T, Dataset<T>>> extends AnalyticalMethod<T, A>, ReporterCreator {
}
