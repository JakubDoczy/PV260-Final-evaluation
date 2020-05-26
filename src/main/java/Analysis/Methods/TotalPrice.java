package Analysis.Methods;

import Analysis.Data.Order;
import Analysis.Data.OrderStatus;
import Analysis.OrderAnalyser;
import Writer.ReportableAnalyticalMethod;
import Writer.Reporter;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class TotalPrice implements ReportableAnalyticalMethod<Order, OrderAnalyser> {

    private OrderStatus desiredStatus;
    private Map<Long, Long> prices;

    public TotalPrice(OrderStatus desiredStatus) {
        this.desiredStatus = desiredStatus;
        prices = new HashMap<>();
    }

    public Map<Long, Long> getPrices() {
        return prices;
    }

    public OrderStatus getDesiredStatus() {
        return desiredStatus;
    }

    public Long findMaxPrice() {
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
    public Reporter createReporter() {
        if (prices.isEmpty()) {
            throw new IllegalStateException("Creating reporter before analysis.");
        }

        return new TotalPriceReporter(this);
    }

    @Override
    public String toString() {
        return "total " + desiredStatus.str + " order price calculation";
    }
}
