package Analysis;

import Analysis.Data.Customer;
import Analysis.Data.Order;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class TopCustomers implements AnalyticalMethod<Order, OrderAnalyser>{

    private static final Comparator<Map.Entry<Customer, Long>> ENTRY_COMPARATOR = new Comparator<Map.Entry<Customer, Long>>() {
        @Override
        public int compare(Map.Entry<Customer, Long> l, Map.Entry<Customer, Long> r) {
            return Long.compare(l.getValue(), r.getValue());
        }
    };

    private Map<Customer, Long> customerOrders;
    private MaxList<Map.Entry<Customer, Long>> maxList;

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
    public void reportTXT(OutputStream os) throws IOException {
        if (customerOrders.isEmpty()) {
            throw new IllegalStateException("Calling reportTXT before analysis.");
        }

        if (maxList.getSortedData().isEmpty()) {
            calculate();
        }

        List<Map.Entry<Customer, Long>> topCustomers = maxList.getSortedData();
        String header = "Top customers" + System.lineSeparator();
        os.write(header.getBytes());

        for (int i = 0; i < topCustomers.size(); i++) {
            Map.Entry<Customer, Long> customerEntry = topCustomers.get(i);

            os.write(String.format("No. %d customer: %s, %d orders%n"
                    , i+1
                    , customerEntry.getKey().toString()
                    , customerEntry.getValue()).getBytes());
        }
    }

    @Override
    public void reportXML(XMLStreamWriter writer) throws XMLStreamException {
        if (customerOrders.isEmpty()) {
            throw new IllegalStateException("Calling reportXML before analysis.");
        }

        if (maxList.getSortedData().isEmpty()) {
            calculate();
        }

        List<Map.Entry<Customer, Long>> topCustomers = maxList.getSortedData();

        writer.writeStartElement("top_customers");

        for (Map.Entry<Customer, Long> entry : topCustomers) {
            writer.writeStartElement(entry.getKey().toString());
            writer.writeCharacters(entry.getValue().toString());
            writer.writeEndElement();
        }

        writer.writeEndElement();
    }

    @Override
    public String toString() {
        return "calculation of top customers";
    }
}
