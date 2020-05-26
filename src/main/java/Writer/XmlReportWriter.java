package Writer;

import com.sun.xml.internal.txw2.output.IndentingXMLStreamWriter;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static Writer.TxtReportWriter.CreateOutputStream;

public class XmlReportWriter implements ReportWriter {

    private List<ReporterCreator> reportCreators;
    private String outputFilePath;

    public XmlReportWriter(String outputFilePath) {
        reportCreators = new ArrayList<>();
        this.outputFilePath = outputFilePath;
    }

    @Override
    public void addReportCreator(ReporterCreator reportCreator) {
        reportCreators.add(reportCreator);
    }

    @Override
    public void writeReport() {
        try (OutputStream os = CreateOutputStream(outputFilePath)) {
            XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
            XMLStreamWriter writer = new IndentingXMLStreamWriter(xmlof.createXMLStreamWriter(os));

            for (ReporterCreator reportCreator : reportCreators) {
                reportCreator.createReporter().reportXML(writer);
            }
        } catch (XMLStreamException | IOException e) {
            throw new RuntimeException("Failed to create XML report.", e);
        }
    }
}
