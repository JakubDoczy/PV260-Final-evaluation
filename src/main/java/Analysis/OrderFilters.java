package Analysis;

import Analysis.Data.Order;

import java.util.function.Function;

public enum OrderFilters {

    MISSING_EMAIL("missing_email", Tools.missingStrAttributeFilter(Tools.EMAIL_GETTER, "email")),
    MISSING_ADDRESS("missing_address", Tools.missingStrAttributeFilter(Tools.ADDRESS_GETTER, "address"));

    public final String str;
    public final BasicFilter<Order> filter;

    OrderFilters(String str, BasicFilter<Order> filter) {
        this.str = str;
        this.filter = filter;
    }

    private static class Tools {

        static final Function<Order, String> EMAIL_GETTER = new Function<Order, String>() {
            @Override
            public String apply(Order order) {
                return order.getCustomer().getEmail();
            }
        };

        static final Function<Order, String> ADDRESS_GETTER = new Function<Order, String>() {
            @Override
            public String apply(Order order) {
                return order.getCustomer().getAddress();
            }
        };

        /**
         * Creates filter that removes orders that are missing attribute obtained by getter
         *
         * @param getter returns relevant attribute from order
         * @return filter that removes all orders that are missing attribute
         */
        static BasicFilter<Order> missingStrAttributeFilter(Function<Order, String> getter, String attributeName) {
            Function<Order, Boolean> predicate = new Function<Order, Boolean>() {
                @Override
                public Boolean apply(Order order) {
                    String attribute = getter.apply(order);
                    return (attribute != null && !attribute.isEmpty());
                }

                @Override
                public String toString() {
                    return "remove missing " + attributeName;
                }
            };

            return new BasicFilter<Order>(predicate);
        }
    }

}
