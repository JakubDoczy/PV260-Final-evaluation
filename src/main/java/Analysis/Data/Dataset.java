package Analysis.Data;

public interface Dataset<T> extends Iterable<T> {
    void add(T dataEntry);
}
