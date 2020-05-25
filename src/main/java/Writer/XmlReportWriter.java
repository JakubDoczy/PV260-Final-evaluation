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

    private List<Reportable> reportables;
    private String outputFilePath;

    public XmlReportWriter(String outputFilePath) {
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
            XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
            XMLStreamWriter writer = new IndentingXMLStreamWriter(xmlof.createXMLStreamWriter(os));

            for (Reportable reportable : reportables) {
                reportable.reportXML(writer);
            }
        } catch (XMLStreamException | IOException e) {
            throw new RuntimeException("Failed to create XML report.", e);
        }
    }
}
