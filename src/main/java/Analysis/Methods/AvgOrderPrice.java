package Analysis.Methods;

import Analysis.Data.Order;
import Analysis.Data.OrderStatus;
import Analysis.OrderAnalyser;
import Writer.ReportableAnalyticalMethod;
import Writer.Reporter;

import java.util.*;
import java.util.function.BiConsumer;


public class AvgOrderPrice implements ReportableAnalyticalMethod<Order, OrderAnalyser> {

    private OrderStatus desiredStatus;
    private Map<Long, Double> avgPrices;
    private Map<Long, Long> numberOfOrdersPA; // Total number of (relevant) orders for each year
    private TotalPrice totalPriceMethod;
    private Double maxAvgPrice;

    public OrderStatus getDesiredStatus() {
        return desiredStatus;
    }

    public Map<Long, Double> getAveragePrices() {
        return avgPrices;
    }

    public Double getMaxAvgPrice() {
        return maxAvgPrice;
    }

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
    public Reporter createReporter() {
        if (totalPriceMethod == null || numberOfOrdersPA == null) {
            throw new IllegalStateException("Creating reporter before analysis.");
        }
        computeAverage();

        return new AvgOrderPriceReporter(this);
    }

    @Override
    public String toString() {
        return "analysis of average price of " + desiredStatus + " orders";
    }
}
