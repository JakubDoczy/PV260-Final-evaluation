package Analysis.Data;

import Analysis.Data.Dataset;
import Analysis.Data.Order;

import java.util.*;
import java.util.function.Consumer;

public class OrdersDatasetImpl implements Dataset<Order> {

    private Set<Order> orders = new HashSet<>();

    public void add(Order dataEntry) {
        orders.add(dataEntry);
    }

    public Iterator<Order> iterator() {
        return orders.iterator();
    }

    public void forEach(Consumer<? super Order> action) {
        orders.forEach(action);
    }

    public Spliterator<Order> spliterator() {
        return orders.spliterator();
    }
}
