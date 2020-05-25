package Writer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TxtReportWriter implements ReportWriter {



    static OutputStream CreateOutputStream(String filePath) {
        File file = new File(filePath);

        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException("Failed to create file (" + filePath + ") for report.", e);
        }

        try {
            return new FileOutputStream(filePath);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Failed to open file (" + filePath + ") for writing report.", e);
        }
    }

    private List<Reportable> reportables;
    private String outputFilePath;

    public TxtReportWriter(String outputFilePath) {
        reportables = new ArrayList<>();
        this.outputFilePath = outputFilePath;
    }

    @Override
    public void addReportable(Reportable reportable) {
        reportables.add(reportable);
    }

    @Override
    public void writeReport() {
        try (OutputStream os = CreateOutputStream(outputFilePath)) {
            for (Reportable reportable : reportables) {
                reportable.reportTXT(os);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create text report.");
        }
    }
}
