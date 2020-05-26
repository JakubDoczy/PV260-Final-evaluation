import Analysis.Data.Order;
import Analysis.OrderAnalyser;
import Analysis.OrderAnalyticalMethods;
import Analysis.OrderFilters;

import java.util.HashMap;
import java.util.Map;

public class OrderMethodParser {

    private Map<String, Runnable> registrationMap = new HashMap<>();

    private void constructRegistrationMap(ActionManager<Order, OrderAnalyser> actionManager) {
        for (OrderAnalyticalMethods orderAnalMethod : OrderAnalyticalMethods.values()) {
            registrationMap.put(orderAnalMethod.str, () -> {
                actionManager.addAnalyticalMethod(orderAnalMethod.create()); });
        }

        for (OrderFilters orderFilter : OrderFilters.values()) {
            registrationMap.put(orderFilter.str, () -> { actionManager.addFilterMethod(orderFilter.filter); });
        }
    }

    public void parseMethods(Iterable<String> unparsedMethods) {
        for (String unparsedMethod : unparsedMethods) {
            if (!registrationMap.containsKey(unparsedMethod)) {
                throw new RuntimeException("Unrecognised data manipulation method.");
            }
            registrationMap.get(unparsedMethod).run();
        }
    }


    public OrderMethodParser(ActionManager<Order, OrderAnalyser> actionManager) {
        constructRegistrationMap(actionManager);
    }
}
