package Analysis.Methods;

import Analysis.Data.Customer;
import Analysis.Data.Order;
import Analysis.MaxList;
import Analysis.OrderAnalyser;
import Writer.ReportableAnalyticalMethod;
import Writer.Reporter;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class TopCustomers implements ReportableAnalyticalMethod<Order, OrderAnalyser> {

    private static final Comparator<Map.Entry<Customer, Long>> ENTRY_COMPARATOR = new Comparator<Map.Entry<Customer, Long>>() {
        @Override
        public int compare(Map.Entry<Customer, Long> l, Map.Entry<Customer, Long> r) {
            return Long.compare(l.getValue(), r.getValue());
        }
    };

    private Map<Customer, Long> customerOrders;
    private MaxList<Map.Entry<Customer, Long>> maxList;

    public Map<Customer, Long> getCustomerOrders() {
        return customerOrders;
    }

    public MaxList<Map.Entry<Customer, Long>> getMaxList() {
        return maxList;
    }

    public TopCustomers(int n) {
        customerOrders = new HashMap<>();
        maxList = new MaxList<>(n, ENTRY_COMPARATOR);
    }

    public TopCustomers() {
        this(3);
    }

    private void calculate() {
        for (Map.Entry<Customer, Long> entry : customerOrders.entrySet()) {
            maxList.add(entry); // this should be pretty fast
        }
    }

    @Override
    public void register(OrderAnalyser analyser) {
        analyser.addAnalysisConsumer(new Consumer<Order>() {
            @Override
            public void accept(Order order) {
                Customer customer = order.getCustomer();

                if (customerOrders.containsKey(customer)) {
                    customerOrders.put(customer, customerOrders.get(customer) + 1);
                } else {
                    customerOrders.put(customer, 1L);
                }
            }
        });
    }

    @Override
    public Reporter createReporter() {
        if (customerOrders.isEmpty()) {
            throw new IllegalStateException("Creating reporter before analysis.");
        }

        if (maxList.getSortedData().isEmpty()) {
            calculate();
        }

        return new TopCustomersReporter(this);
    }

    @Override
    public String toString() {
        return "calculation of top customers";
    }
}
