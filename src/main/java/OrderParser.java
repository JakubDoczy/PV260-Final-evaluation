import Analysis.Data.Order;
import Analysis.Data.OrderStatus;
import Analysis.Data.Parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Consumer;

public class OrderParser implements Parser<Order> {


    private static final SimpleDateFormat dateParser=new SimpleDateFormat("DD.MM.yyyy");

    private Date parseDate(String str) {
        if (str == null) {
            throw new RuntimeException("Date of order is missing.");
        }

        Date date;

        try {
            date = dateParser.parse(str);
        } catch (ParseException e) {
            throw new RuntimeException("Unable to parse order date.", e);
        }

        return date;
    }

    private OrderStatus parseOrderStatus(String str) {
        for (OrderStatus status : OrderStatus.values()) {
            if (status.str.equals(str)) {
                return status;
            }
        }

        throw new RuntimeException("Failed to parse order status.");
    }

    @Override
    public void parseCSV(InputStream is, Consumer<Order> dataConsumer) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line;

        while ((line = reader.readLine()) != null) {
            String[] attributes = line.split(",");

            if (attributes.length != 6) {
                throw new RuntimeException("Encountered invalid number of columns while parsing orders.");
            }

            Order order = Order.Builder.newInstance().setId(Integer.parseInt(attributes[0]))
                    .setOrderDate(parseDate(attributes[1]))
                    .setCustomerEmail(attributes[2])
                    .setCustomerAddress(attributes[3])
                    .setTotalPrice(Integer.parseInt(attributes[4]))
                    .setOrderStatus(parseOrderStatus(attributes[5]))
                    .build();

            dataConsumer.accept(order);
        }
    }



}
