package ReportFormatter;

import java.util.function.Function;

public class TableFormatter {

    enum Alignment {
        LEFT, RIGHT // centre
    }

    private final int[] columnSizes;
    private Alignment[] alignments;

    public static Function<Long, String> FORMATTER_LONG = new Function<Long, String>() {
        @Override
        public String apply(Long key) {
            return key.toString();
        }
    };

    public static Function<Double, String> FORMATTER_DOUBLE = new Function<Double, String>() {
        @Override
        public String apply(Double key) {
            return String.format("%.2f", (double) key);
        }
    };

    TableFormatter(int[] columnSizes, Alignment[] alignments) {
        if (columnSizes.length != alignments.length) {
            throw new IllegalArgumentException("Number of columnSizes (" + columnSizes.length +
                    ") doesn't match number of alignments (" + alignments.length + ")");
        }

        this.columnSizes = columnSizes;
        this.alignments = alignments;
    }

    public void setAlignments(Alignment[] alignments) {
        if (columnSizes.length != alignments.length) {
            throw new IllegalArgumentException("Number of columnSizes (" + columnSizes.length +
                    ") doesn't match number of alignments (" + alignments.length + ")");
        }

        this.alignments = alignments;
    }

    private static String repeat(char c, int n) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < n; i++) {
            builder.append(c);
        }
        return builder.toString();
    }

    private static String formatStr(String block, int columnSize, Alignment alignment) {
        assert block.length() <= columnSize;
        int paddingSize = columnSize - block.length();

        String outBlock;

        switch (alignment) {
            case LEFT:
                outBlock = block + repeat(' ', paddingSize);
                break;
            case RIGHT:
                outBlock = repeat(' ', paddingSize) + block;
                break;
            default:
                throw new AssertionError("Unsupported formatting.");
        }

        assert outBlock != null;
        return outBlock;
    }

    public String formatRow(String[] row) {
        // checking for every row might be expensive so I'll use assert
        assert row.length == columnSizes.length;

        StringBuilder strRow = new StringBuilder("| ");

        for (int i = 0; i < row.length; i++) {
            strRow.append(formatStr(row[i], columnSizes[i], alignments[i]));
            strRow.append((i != row.length - 1) ? " | " : " |" + System.lineSeparator());
        }

        return strRow.toString();
    }


}
