package Analysis;

import Analysis.Data.Order;
import Analysis.Data.OrderStatus;
import ReportFormatter.DefaultMapReportCreator;
import ReportFormatter.TableFormatter;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class TotalPrice implements AnalyticalMethod<Order, OrderAnalyser> {

    private OrderStatus desiredStatus;
    private Map<Long, Long> prices;

    public TotalPrice(OrderStatus desiredStatus) {
        this.desiredStatus = desiredStatus;
        prices = new HashMap<>();
    }

    public Map<Long, Long> getPrices() {
        return prices;
    }

    private Long findMaxPrice() {
        Long maxPrice = 0L;

        for (Map.Entry<Long, Long> entry : prices.entrySet()) {
            if (entry.getValue() > maxPrice) {
                maxPrice = entry.getValue();
            }
        }
        return maxPrice;
    }

    @Override
    public void register(OrderAnalyser analyser) {
        analyser.addYearlyAnalysisConsumer(new BiConsumer<Long, Order>() {
            @Override
            public void accept(Long year, Order order) {
                if (order.getOrderStatus() == desiredStatus) {
                    if (prices.containsKey(year)) {
                        prices.put(year, prices.get(year) + order.getTotalPrice());
                    } else {
                        prices.put(year, (long) order.getTotalPrice());
                    }
                }
            }
        });
    }

    @Override
    public void reportTXT(OutputStream os) throws IOException {
        if (prices.isEmpty()) {
            throw new IllegalStateException("Calling reportTXT before analysis.");
        }

        String header = "Total " + desiredStatus.name() + " order prices";
        String[] columns = {"Year", "Total order price"};

        int[] columnLengths = {4, (int) Math.log10(findMaxPrice() + 1) + 1};
        if (columnLengths[1] < columns[1].length()) {
            columnLengths[1] = columns[1].length();
        }

        DefaultMapReportCreator reportCreator = DefaultMapReportCreator.Builder.<Long, Long>newInstance()
                .setData(prices)
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
        if (prices.isEmpty()) {
            throw new IllegalStateException("Calling reportXML before analysis.");
        }

        writer.writeStartElement("total_" + desiredStatus.str + "_order_price");

        for (Map.Entry<Long, Long> entry : prices.entrySet()) {
            writer.writeStartElement(entry.getKey().toString());
            writer.writeCharacters(entry.getValue().toString());
            writer.writeEndElement();
        }

        writer.writeEndElement();
    }

    @Override
    public String toString() {
        return "total " + desiredStatus.str + " order price calculation";
    }
}
