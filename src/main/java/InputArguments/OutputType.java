package InputArguments;

import Writer.ReportWriter;
import Writer.TxtReportWriter;
import Writer.XmlReportWriter;

import java.util.function.Function;

public enum OutputType {

    XML("xml", XmlReportWriter::new),
    PLAIN("plain", TxtReportWriter::new);

    public String str;
    private Function<String, ReportWriter> reportWriterCreator;

    private OutputType(String str, Function<String, ReportWriter> reportWriterCreator) {
        this.str = str;
        this.reportWriterCreator = reportWriterCreator;
    }

    public static OutputType parse(String str) {
        // simple approach, can be made more efficient with use of map
        for (OutputType type : OutputType.values()) {
            if (type.str.equals(str)) {
                return type;
            }
        }
        throw new RuntimeException("Unrecognised output type " + str);
    }

    public ReportWriter createReportWriter(String outputFilePath) {
        return reportWriterCreator.apply(outputFilePath);
    }

}
