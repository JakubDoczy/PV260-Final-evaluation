package Analysis.Methods;

import ReportFormatter.DefaultMapReportCreator;
import ReportFormatter.TableFormatter;
import Writer.Reporter;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class TotalPriceReporter implements Reporter {

    public TotalPrice totalPrice;

    public TotalPriceReporter(TotalPrice totalPrice) {
        this.totalPrice = totalPrice;
    }

    @Override
    public void reportTXT(OutputStream os) throws IOException {
        if (totalPrice.getPrices().isEmpty()) {
            throw new IllegalStateException("Calling reportTXT before analysis.");
        }

        String header = "Total " + totalPrice.getDesiredStatus().name() + " order prices" + System.lineSeparator();
        String[] columns = {"Year", "Total order price"};

        int[] columnLengths = {4, (int) Math.log10(totalPrice.findMaxPrice() + 1) + 1};
        if (columnLengths[1] < columns[1].length()) {
            columnLengths[1] = columns[1].length();
        }

        DefaultMapReportCreator reportCreator = DefaultMapReportCreator.Builder.<Long, Long>newInstance()
                .setData(totalPrice.getPrices())
                .setColumnNames(columns)
                .setColumnLengths(columnLengths)
                .setKeyFormatter(TableFormatter.FORMATTER_LONG)
                .setValueFormatter(TableFormatter.FORMATTER_LONG)
                .build();

        os.write(header.getBytes());
        reportCreator.defaultMapReport(os);
    }

    @Override
    public void reportXML(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeStartElement("total_" + totalPrice.getDesiredStatus().str + "_order_price");

        for (Map.Entry<Long, Long> entry : totalPrice.getPrices().entrySet()) {
            writer.writeStartElement(entry.getKey().toString());
            writer.writeCharacters(entry.getValue().toString());
            writer.writeEndElement();
        }

        writer.writeEndElement();
    }
}
