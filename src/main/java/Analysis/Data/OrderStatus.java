package Analysis.Data;

public enum OrderStatus {
    PAID("PAID"), UNPAID("UNPAID");

    OrderStatus(String str) {
        this.str = str;
    }

    public String str;
}
