import Analysis.Data.Dataset;
import Analysis.Data.Order;
import Analysis.OrderAnalyser;
import Writer.ReportWriter;

import java.util.function.Supplier;

public class OrderActionManager extends ActionManager<Order, OrderAnalyser> {

    private static Supplier<OrderAnalyser> orderAnalyserSupplier = OrderAnalyser::new;

    public OrderActionManager(Dataset<Order> dataset, Iterable<String> unparsedMethods, ReportWriter reportWriter) {
        super(dataset, orderAnalyserSupplier, reportWriter);
        OrderMethodParser orderMethodParser = new OrderMethodParser(this);
        orderMethodParser.parseMethods(unparsedMethods);
    }

}
