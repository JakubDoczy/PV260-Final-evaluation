package Writer;

public interface ReportWriter {

    void addReportCreator(ReporterCreator reportCreator);

    void writeReport();
}
