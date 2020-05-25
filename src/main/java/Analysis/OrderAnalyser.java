package Analysis;

import Analysis.Data.Dataset;
import Analysis.Data.Order;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Subject, calls registered Analytical Methods (observers)
 */
public class OrderAnalyser implements Analyser<Order, Dataset<Order>> {

    public class YearlyOrderCallback implements Consumer<Order> {

        private List<BiConsumer<Long, Order>> subCallbacks = new ArrayList<>();
        Calendar calendar = new GregorianCalendar();

        public void addCallback(BiConsumer<Long, Order> consumer) {
            subCallbacks.add(consumer);
        }

        @Override
        public void accept(Order order) {
            calendar.setTime(order.getOrderDate());

            Long year = (long) calendar.get(Calendar.YEAR);

            for (BiConsumer<Long, Order> callback : subCallbacks) {
                callback.accept(year, order);
            }
        }
    }

    private List<Consumer<Order>> callbacks = new ArrayList<>(); // observers
    private YearlyOrderCallback yearlyOrderCallback = null;

    public void analyse(Dataset<Order> dataset) {
        for (Order order : dataset) {
            for (Consumer<Order> consumer : callbacks) {
                consumer.accept(order);
            }
        }
    }

    public void addAnalysisConsumer(Consumer<Order> callback) {
        callbacks.add(callback);
    }

    public void addYearlyAnalysisConsumer(BiConsumer<Long, Order> callback) {
        if (yearlyOrderCallback == null) {
            yearlyOrderCallback = new YearlyOrderCallback();
            addAnalysisConsumer(yearlyOrderCallback);
        }
        yearlyOrderCallback.addCallback(callback);
    }

}
