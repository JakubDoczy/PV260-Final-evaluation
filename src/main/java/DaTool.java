import Analysis.Data.Order;
import Analysis.OrderAnalMethods;
import Analysis.OrderAnalyser;
import Analysis.OrderFilters;
import Analysis.Data.OrdersDatasetImpl;
import Writer.ReportWriter;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class DaTool {

    private final static Logger log = LoggerFactory.getLogger(DaTool.class);

    // opens url and calls parse
    private static OrdersDatasetImpl parseOrdersDataset(String inputUrl) {
        OrdersDatasetImpl ordersDataset = new OrdersDatasetImpl();
        OrderParser orderParser = new OrderParser();
        Consumer<Order> orderConsumer = new Consumer<Order>() {
            @Override
            public void accept(Order order) {
                ordersDataset.add(order);
            }
        };

        InputStream is;
        try {
            is = new URL(inputUrl).openStream();
        } catch (MalformedURLException e) {
            throw new RuntimeException("URL seems to be malformed (" + inputUrl + ").", e);
        } catch (IOException e) {
            throw new RuntimeException("Failed to open URL.");
        }

        try {
            orderParser.parseCSV(is, orderConsumer);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse orders from " + inputUrl, e);
        }

        return ordersDataset;
    }

    // creates map of method names and registration actions
    private static Map<String, Runnable> createRegistrationMap(ActionManager<Order, OrderAnalyser> actionManager) {
        Map<String, Runnable> map = new HashMap<>();

        for (OrderAnalMethods orderAnalMethod : OrderAnalMethods.values()) {
            map.put(orderAnalMethod.str, new Runnable() {
                @Override
                public void run() {
                    actionManager.addAnalyticalMethod(orderAnalMethod.create());
                }
            });
        }

        for (OrderFilters orderFilter : OrderFilters.values()) {
            map.put(orderFilter.str, new Runnable() {
                @Override
                public void run() {
                    actionManager.addFilterMethod(orderFilter.filter);
                }
            });
        }

        return map;
    }

    // parses data manipulation methods and registers them in actionManager
    private static void registerOrderManipulationMethods(List<String> unparsedMethods, ActionManager<Order, OrderAnalyser> actionManager)  {
        Map<String, Runnable> registrationMap = createRegistrationMap(actionManager);

        for (String unparsedMethod : unparsedMethods) {
            if (!registrationMap.containsKey(unparsedMethod)) {
                throw new RuntimeException("Unrecognised data manipulation method.");
            }
            registrationMap.get(unparsedMethod).run();
        }
    }

    private static Supplier<OrderAnalyser> orderAnalyserSupplier = new Supplier<OrderAnalyser>() {
        @Override
        public OrderAnalyser get() {
            return new OrderAnalyser();
        }
    };

    public static void main(String[] args) {
        BasicConfigurator.configure();

        if (args.length < 7) {
            System.out.println("Missing arguments.");
            System.out.println("Expecting -d [DATASETLOCATION] -m [MANIPULATIONMETHODS] -o [OUTPUTTYPE] [OUTPUTFILE]");
            return;
        }

        /*args = new String[]{"-m", "missing_address", "total_price_pa", "-d", "file:///home/jakub/University/Projects/da_tool/src/main/resources/test_data.txt"
                , "-o",  "xml",  "/home/jakub/University/Projects/da_tool/results.txt"};*/

        log.debug("Calling Program options to parse command line arguments.");
        ProgramOptions options = new ProgramOptions(args);

        log.debug("Creating output writer that will save report.");
        ReportWriter reportWriter = options.getOutputType().createReportWriter(options.getOutputPath());

        log.debug("Parsing order dataset.");
        OrdersDatasetImpl dataset = parseOrdersDataset(options.getInputPath());

        log.debug("Creating action manager.");
        ActionManager<Order, OrderAnalyser> orderActionManager = new ActionManager<>(dataset, orderAnalyserSupplier, reportWriter);
        registerOrderManipulationMethods(options.getManipulationMethods(), orderActionManager);

        log.debug("Starting data analysis.");
        orderActionManager.run();
    }


}
