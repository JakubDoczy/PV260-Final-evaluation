package Analysis.Methods;

import ReportFormatter.DefaultMapReportCreator;
import ReportFormatter.TableFormatter;
import Writer.Reporter;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class AvgOrderPriceReporter implements Reporter {

    private AvgOrderPrice avgOrderPrice;

    public AvgOrderPriceReporter(AvgOrderPrice avgOrderPrice) {
        this.avgOrderPrice = avgOrderPrice;
    }

    @Override
    public void reportTXT(OutputStream os) throws IOException {
        String header = "Average " + avgOrderPrice.getDesiredStatus().name() + " order prices" + System.lineSeparator();
        String[] columns = {"Year", "Average order price"};

        // assuming year is always <= 4 chars long
        // average order price is log10 + 1 + decimal mark + 2 decimal points
        int[] columnLengths = {4, (int) Math.log10(avgOrderPrice.getMaxAvgPrice() + 1) + 4};
        if (columnLengths[1] < columns[1].length()) {
            columnLengths[1] = columns[1].length();
        }

        DefaultMapReportCreator reportCreator = DefaultMapReportCreator.Builder.<Long, Double>newInstance()
                .setData(avgOrderPrice.getAveragePrices())
                .setColumnNames(columns)
                .setColumnLengths(columnLengths)
                .setKeyFormatter(TableFormatter.FORMATTER_LONG)
                .setValueFormatter(TableFormatter.FORMATTER_DOUBLE)
                .build();

        os.write(header.getBytes());
        reportCreator.defaultMapReport(os);
    }

    @Override
    public void reportXML(XMLStreamWriter writer) throws XMLStreamException {

        writer.writeStartElement("average_" + avgOrderPrice.getDesiredStatus().str + "_order_price");

        for (Map.Entry<Long, Double> entry : avgOrderPrice.getAveragePrices().entrySet()) {
            writer.writeStartElement(entry.getKey().toString());
            writer.writeCharacters(entry.getValue().toString());
            writer.writeEndElement();
        }

        writer.writeEndElement();
    }
}
