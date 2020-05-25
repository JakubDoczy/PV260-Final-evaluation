package Analysis;

import Analysis.Data.Order;
import Analysis.Data.OrderStatus;

import java.util.function.Supplier;

public enum OrderAnalyticalMethods {

    TOP_CUSTOMERS("top3", new Supplier<AnalyticalMethod<Order, OrderAnalyser>>() {
        @Override
        public AnalyticalMethod<Order, OrderAnalyser> get() {
            return new TopCustomers();
        }
    }),
    TOTAL_PRICE("total_price_pa", new Supplier<AnalyticalMethod<Order, OrderAnalyser>>() {
        @Override
        public AnalyticalMethod<Order, OrderAnalyser> get() {
            return new TotalPrice(OrderStatus.PAID);
        }
    }),
    AVERAGE_PAID_ORDER_PRICE("average_paid_price", new Supplier<AnalyticalMethod<Order, OrderAnalyser>>() {
        @Override
        public AnalyticalMethod<Order, OrderAnalyser> get() {
            return new AvgOrderPrice(OrderStatus.PAID);
        }
    }),
    AVERAGE_UNPAID_ORDER_PRICE("average_unpaid_price", new Supplier<AnalyticalMethod<Order, OrderAnalyser>>() {
        @Override
        public AnalyticalMethod<Order, OrderAnalyser> get() {
            return new AvgOrderPrice(OrderStatus.UNPAID);
        }
    });

    public String str;
    private Supplier<AnalyticalMethod<Order, OrderAnalyser>> supplier;

    private OrderAnalyticalMethods(String str, Supplier<AnalyticalMethod<Order, OrderAnalyser>> supplier) {
        this.str = str;
        this.supplier = supplier;
    }

    public AnalyticalMethod<Order, OrderAnalyser> create() {
        return supplier.get();
    }


}
