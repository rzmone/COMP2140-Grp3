package com.groupthree.sims;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for exporting text data to CSV files.
 *
 * This implementation:
 *  - Treats each String in {@code records} as a single-column row.
 *  - Optionally writes a header row.
 *  - Properly escapes values that contain commas, quotes, or newlines
 *    according to common CSV conventions.
 */
public class CsvExporter {

    /**
     * Writes the given records to a CSV file.
     *
     * Each entry in {@code records} becomes one row in the CSV, as a single column.
     * If {@code headers} is not null/empty, it is written as the first row.
     *
     * @param outputFile the path of the CSV file to write to
     * @param records    list of records, each record is a single CSV value (one column)
     * @param headers    optional header values (each becomes a column in the header row);
     *                   pass null or an empty list if you don't want headers
     * @throws IOException if an I/O error occurs
     */
    public static void writeCsv(Path outputFile,
                                List<String> records,
                                List<String> headers) throws IOException {

        try (BufferedWriter writer = Files.newBufferedWriter(outputFile, StandardCharsets.UTF_8)) {

            // Write header row (if any)
            if (headers != null && !headers.isEmpty()) {
                String headerLine = joinCsvRow(headers);
                writer.write(headerLine);
                writer.newLine();
            }

            // Write each record as a single-column row
            if (records != null) {
                for (String record : records) {
                    String escaped = escapeCsvField(record);
                    writer.write(escaped);
                    writer.newLine();
                }
            }
        }
    }

    /**
     * Joins multiple fields into a single CSV row string.
     */
    private static String joinCsvRow(List<String> fields) {
        return fields.stream()
                     .map(CsvExporter::escapeCsvField)
                     .collect(Collectors.joining(","));
    }

    /**
     * Escapes a single CSV field according to RFC 4180-style rules:
     *  - If the value contains a comma, double quote, CR, or LF, the whole
     *    field is wrapped in double quotes.
     *  - Any existing double quotes inside the value are doubled (" becomes "").
     *
     * Examples:
     *  John Doe      -> John Doe
     *  Smith, John   -> "Smith, John"
     *  He said "Hi"  -> "He said ""Hi"""
     *
     * @param value the raw field value
     * @return CSV-safe representation of the field
     */
    private static String escapeCsvField(String value) {
        if (value == null) {
            return "";
        }

        boolean mustQuote = value.contains(",")
                || value.contains("\"")
                || value.contains("\n")
                || value.contains("\r");

        // Escape double quotes by doubling them
        String escaped = value.replace("\"", "\"\"");

        if (mustQuote) {
            return "\"" + escaped + "\"";
        } else {
            return escaped;
        }
    }
}
