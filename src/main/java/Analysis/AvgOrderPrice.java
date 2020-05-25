package Analysis;

import Analysis.Data.Order;
import Analysis.Data.OrderStatus;
import ReportFormatter.DefaultMapReportCreator;
import ReportFormatter.TableFormatter;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.function.BiConsumer;


public class AvgOrderPrice implements AnalyticalMethod<Order, OrderAnalyser> {

    private OrderStatus desiredStatus;
    private Map<Long, Double> avgPrices;
    private Map<Long, Long> numberOfOrdersPA; // Total number of (relevant) orders for each year
    private TotalPrice totalPriceMethod;
    private Double maxAvgPrice;

    public AvgOrderPrice(OrderStatus desiredStatus) {
        this.desiredStatus = desiredStatus;
        maxAvgPrice = 0D;
        avgPrices = new HashMap<>();
        numberOfOrdersPA = new HashMap<>();
    }

    /**
     * Computes averages from total sum of prices and number of orders and
     * sets maxAvgPrice to largest average order price.
     */
    private void computeAverage() {
        for (Map.Entry<Long, Long> yearPriceEntry : totalPriceMethod.getPrices().entrySet()) {
            Double avgPrice = (yearPriceEntry.getValue() * 1D) / numberOfOrdersPA.get(yearPriceEntry.getKey());
            if (maxAvgPrice < avgPrice) {
                maxAvgPrice = avgPrice;
            }
            avgPrices.put(yearPriceEntry.getKey(), avgPrice);
        }
    }

    /**
     * If we want to calculate total price independently (because for example user wants to know it)
     * , we can use results for calculation of average price.
     *
     * @param totalPriceMethod Observer that calculates total price of orders for each year.
     */
    public void setTotalPriceMethod(TotalPrice totalPriceMethod) {
        this.totalPriceMethod = totalPriceMethod;
    }

    @Override
    public void register(OrderAnalyser analyser) {
        if (totalPriceMethod == null) {
            totalPriceMethod = new TotalPrice(desiredStatus);
        }
        totalPriceMethod.register(analyser);

        analyser.addYearlyAnalysisConsumer(new BiConsumer<Long, Order>() {
            @Override
            public void accept(Long year, Order order) {
                if (order.getOrderStatus() != desiredStatus) {
                    return;
                }
                if (numberOfOrdersPA.containsKey(year)) {
                    numberOfOrdersPA.put(year, numberOfOrdersPA.get(year) + 1);
                } else {
                    numberOfOrdersPA.put(year, 1L);
                }
            }
        });
    }

    @Override
    public void reportTXT(OutputStream os) throws IOException {
        if (totalPriceMethod == null || numberOfOrdersPA == null) {
            throw new IllegalStateException("Calling reportTXT before analysis.");
        }
        computeAverage();

        String header = "Average " + desiredStatus.name() + " order prices" + System.lineSeparator();
        String[] columns = {"Year", "Average order price"};

        // assuming year is always <= 4 chars long
        // average order price is log10 + 1 + decimal mark + 2 decimal points
        int[] columnLengths = {4, (int) Math.log10(maxAvgPrice + 1) + 4};
        if (columnLengths[1] < columns[1].length()) {
            columnLengths[1] = columns[1].length();
        }

        DefaultMapReportCreator reportCreator = DefaultMapReportCreator.Builder.<Long, Double>newInstance()
                .setData(avgPrices)
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
        if (totalPriceMethod == null || numberOfOrdersPA == null) {
            throw new IllegalStateException("Calling reportXML before analysis.");
        }
        computeAverage();

        writer.writeStartElement("average_" + desiredStatus.str + "_order_price");

        for (Map.Entry<Long, Double> entry : avgPrices.entrySet()) {
            writer.writeStartElement(entry.getKey().toString());
            writer.writeCharacters(entry.getValue().toString());
            writer.writeEndElement();
        }

        writer.writeEndElement();
    }

    @Override
    public String toString() {
        return "analysis of average price of " + desiredStatus + " orders";
    }
}
