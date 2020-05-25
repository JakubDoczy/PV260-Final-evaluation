package ReportFormatter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.function.Function;

public class DefaultMapReportCreator<K, V> {
    private Map<K, V> data;
    private Function<K, String> keyFormatter;
    private Function<V, String> valueFormatter;
    private String[] columnNames;
    private int[] columnLengths;

    public void defaultMapReport(OutputStream os) throws IOException {
        assert columnNames.length == 2;
        assert columnLengths.length == 2;

        TableFormatter.Alignment[] colNameAlignment = {TableFormatter.Alignment.LEFT
                , TableFormatter.Alignment.LEFT};

        TableFormatter.Alignment[] numRowAlignment = {TableFormatter.Alignment.RIGHT
                , TableFormatter.Alignment.RIGHT};

        TableFormatter tableFormatter = new TableFormatter(columnLengths, colNameAlignment);

        try {
            os.write(tableFormatter.formatRow(columnNames).getBytes());

            tableFormatter.setAlignments(numRowAlignment);

            for (Map.Entry<K, V> entry : data.entrySet()) {
                String[] row = { keyFormatter.apply(entry.getKey()), valueFormatter.apply(entry.getValue()) };
                os.write(tableFormatter.formatRow(row).getBytes());
            }

        } catch (IOException e) {
            throw new IOException("Failed to write data to output stream while creating report.", e);
        }
    }

    public static class Builder<K, V> {
        private Map<K, V> data;
        private Function<K, String> keyFormatter;
        private Function<V, String> valueFormatter;
        private String[] columnNames;
        private int[] columnLengths;

        public static <K, V> Builder<K, V> newInstance()
        {
            return new Builder<K, V>();
        }

        private Builder() {}

        public Builder<K, V> setData(Map<K, V> data) {
            this.data = data;
            return this;
        }

        public Builder<K, V> setKeyFormatter(Function<K, String> keyFormatter) {
            assert keyFormatter != null;
            this.keyFormatter = keyFormatter;
            return this;
        }

        public Builder<K, V> setValueFormatter(Function<V, String> valueFormatter) {
            assert valueFormatter != null;

            this.valueFormatter = valueFormatter;
            return this;
        }

        public Builder<K, V> setColumnNames(String[] columnNames) {
            assert columnNames != null;
            assert columnNames.length == 2;

            this.columnNames = columnNames;
            return this;
        }

        public Builder<K, V> setColumnLengths(int[] columnLengths) {
            this.columnLengths = columnLengths;
            return this;
        }

        public DefaultMapReportCreator<K, V> build() {
            assert data != null;
            assert keyFormatter != null;
            assert valueFormatter != null;
            assert columnNames != null;
            assert columnLengths != null;
            assert columnNames.length == 2;
            assert columnLengths.length == 2;

            DefaultMapReportCreator<K, V> reportCreator = new DefaultMapReportCreator<K, V>();
            reportCreator.data = data;
            reportCreator.keyFormatter = keyFormatter;
            reportCreator.valueFormatter = valueFormatter;
            reportCreator.columnNames = columnNames;
            reportCreator.columnLengths = columnLengths;

            return reportCreator;
        }
    }

}