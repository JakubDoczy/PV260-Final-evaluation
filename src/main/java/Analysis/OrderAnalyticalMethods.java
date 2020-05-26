package Analysis;

import Analysis.Data.Order;
import Analysis.Data.OrderStatus;
import Analysis.Methods.AnalyticalMethod;
import Analysis.Methods.AvgOrderPrice;
import Analysis.Methods.TopCustomers;
import Analysis.Methods.TotalPrice;
import Writer.ReportableAnalyticalMethod;

import java.util.function.Supplier;

public enum OrderAnalyticalMethods {

    TOP_CUSTOMERS("top3", TopCustomers::new),
    TOTAL_PRICE("total_price_pa", () -> {
        return new TotalPrice(OrderStatus.PAID);
    }),
    AVERAGE_PAID_ORDER_PRICE("average_paid_price", () -> {
        return new AvgOrderPrice(OrderStatus.PAID);
    }),
    AVERAGE_UNPAID_ORDER_PRICE("average_unpaid_price", () -> {
        return new AvgOrderPrice(OrderStatus.UNPAID);
    });

    public String str;
    private Supplier<ReportableAnalyticalMethod<Order, OrderAnalyser>> supplier;

    OrderAnalyticalMethods(String str, Supplier<ReportableAnalyticalMethod<Order, OrderAnalyser>> supplier) {
        this.str = str;
        this.supplier = supplier;
    }

    public ReportableAnalyticalMethod<Order, OrderAnalyser> create() {
        return supplier.get();
    }


}
