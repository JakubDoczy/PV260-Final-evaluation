package Analysis.Data;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

public interface Parser<T> {
    void parseCSV(InputStream is, Consumer<T> dataConsumer) throws IOException;
}
