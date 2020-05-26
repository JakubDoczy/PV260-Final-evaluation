import Analysis.Methods.Analyser;
import Analysis.Methods.AnalyticalMethod;
import Analysis.Data.Dataset;
import Analysis.DatasetFilter;
import Writer.ReportWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import Writer.ReportableAnalyticalMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates list of runnables from methods that are subscribed to data analysis.
 * The main idea is that multiple data analysis methods can be subscribed to
 * the same analyser which iterates through the data and gives it to methods.
 *
 * Analyser calls methods in undefined order. It does not (have to) wait for the first
 * method to finish. If we do not call any filtering methods, only one analyser
 * is needed. Analyser is implemented this way so we can minimize number of
 * operations on dataset.
 *
 * FAQ:
 * Q: why not give dataset to method directly? A: We would have to iterate multiple
 * times through data - each method would iterate over data independently. By using
 * Analysis.Methods.Analyser we can (partially) avoid this - AnalyticalMethods subscribe callbacks
 * for data.
 * Q: But what about parallelism? A: Analyser can still make it possible. Either by doing
 * multiple iterations, each in different thread and sending data in different threads to
 * methods or it could divide the dataset and each thread would analyse one part. I
 * haven't implemented these parallel analysers but I'm pretty sure it is possible.
 *
 * @param <T> Analysis.Data Data type
 * @param <A> Analysis.Methods.Analyser Analyser type.
 */
public class ActionManager <T, A extends Analyser<T, Dataset<T>>> {

    private final static Logger log = LoggerFactory.getLogger(ActionManager.class);

    private Dataset<T> dataset;
    private List<Runnable> analysisActions;
    private ReportWriter reportWriter;
    private Supplier<A> analyserSupplier;

    private A analyser;

    /**
     * Created action manager that uses and changes dataset.
     *
     * @param dataset Analysis.Data for analysis.
     * @param analyserSupplier Analysis.Methods.Analyser factory.
     * @param reportWriter Writes report.
     */
    public ActionManager(Dataset<T> dataset, Supplier<A> analyserSupplier, ReportWriter reportWriter) {
        this.dataset = dataset;
        analysisActions = new ArrayList<>();
        this.reportWriter = reportWriter;
        this.analyserSupplier = analyserSupplier;
        analyser = null;
    }

    /**
     * Adds filtering method. Order of methods is important!
     *
     * @param datasetFilter Filtering method.
     */
    public void addFilterMethod(DatasetFilter<T, Dataset<T>> datasetFilter) {
        log.debug("Adding filter " + datasetFilter.toString() + " to action manager.");
        analyser = null;

        analysisActions.add(new Runnable() {
            @Override
            public void run() {
                datasetFilter.filter(dataset);
            }
        });
    }

    /**
     * Adds data analysis method. Order of methods is important!
     *
     * @param method Analysis method.
     */
    public void addAnalyticalMethod(ReportableAnalyticalMethod<T, A> method) {
        log.debug("Adding analytical method " + method.toString() + " to action manager.");

        if (analyser == null) {
            analyser = analyserSupplier.get();
            analysisActions.add(new AnalysisRunner(analyser, dataset));
        }

        log.debug("Adding analytical method " + method.toString() + " to analyser " + analyser.toString());
        method.register(analyser);
        reportWriter.addReportCreator(method);
    }

    /**
     * Runs the whole analysis.
     */
    public void run() {
        log.debug("Analysis started.");
        for (Runnable runnable : analysisActions) {
            runnable.run();
        }

        log.debug("Analysis complete.");
        log.debug("Writing report.");
        reportWriter.writeReport();
    }

    // ad hoc class to run analyser
    // needed because supplier itself would point to wrong analyser
    private class AnalysisRunner implements Runnable {
        A analyser;
        Dataset<T> dataset;

        private AnalysisRunner(A analyser, Dataset<T> dataset) {
            this.analyser = analyser;
            this.dataset = dataset;
        }

        @Override
        public void run() {
            analyser.analyse(dataset);
        }
    }
}
