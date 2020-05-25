package Analysis.Data;

import java.util.Date;

public class Order {
    private final int id;
    private Date orderDate;
    private Customer customer;
    private int totalPrice;
    private OrderStatus orderStatus;

    public Order(int id) {
        this.id = id;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public Customer getCustomer() {
        return customer;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }


    public static class Builder {
        private int id = -1;
        private Date orderDate;
        private String customerEmail;
        private String customerAddress;
        private int totalPrice;
        private OrderStatus orderStatus;

        private Builder() {}

        public static Builder newInstance()
        {
            return new Builder();
        }

        public Builder setId(int id) {
            assert id != -1;
            this.id = id;
            return this;
        }

        public Builder setOrderDate(Date orderDate) {
            this.orderDate = orderDate;
            return this;
        }

        public Builder setCustomerEmail(String customerEmail) {
            this.customerEmail = customerEmail;
            return this;
        }

        public Builder setCustomerAddress(String customerAddress) {
            this.customerAddress = customerAddress;
            return this;
        }

        public Builder setTotalPrice(int totalPrice) {
            this.totalPrice = totalPrice;
            return this;
        }

        public Builder setOrderStatus(OrderStatus orderStatus) {
            this.orderStatus = orderStatus;
            return this;
        }

        public Order build() {
            assert id != -1;

            Customer customer = new Customer(customerEmail, customerAddress);
            Order order = new Order(id);
            order.orderDate = orderDate;
            order.customer = customer;
            order.totalPrice = totalPrice;
            order.orderStatus = orderStatus;

            return order;
        }
    }

}
