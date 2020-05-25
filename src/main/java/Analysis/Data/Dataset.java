package Analysis.Data;

/**
 * Dataset is a data structure that allows
 * adding data and iterating over it.
 *
 * The main idea is that it can be some class that
 * loads data dynamically. I would just use set or array
 * and not complicate it but in this exercise my
 * task is to think about future development.
 *
 * @param <T> Type of data this dataset stores
 */
public interface Dataset<T> extends Iterable<T> {
    void add(T dataEntry);
}
