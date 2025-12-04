package com.groupthree.sims;

import java.util.*;
import java.sql.*;

public class Database {

    private static final String URL = "jdbc:mysql://localhost:3306/sims?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "Fifa201!";

    static
    {
        try
        {
            // Make sure the MySQL driver is loaded (for older JDBC setups).
            Class.forName("com.mysql.cj.jdbc.Driver");
        } 
        catch (ClassNotFoundException e) 
        {
            System.err.println("MySQL JDBC driver not found.");
            e.printStackTrace();
        }
    }

    private static Connection getConnection() throws SQLException
    {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Executes a read operation and retrieves data from the database.
     *
     * <p>The method returns a list of rows, where each row is represented as a
     * {@code Map<String, Object>}. Keys correspond to column names, and values
     * store the associated column data.</p>
     *
     * <p><b>Example Usage:</b></p>
     * <pre>{@code
     * List<Map<String, Object>> results = Database.select();
     *
     * for (Map<String, Object> row : results) {
     *     System.out.println("ID: " + row.get("id"));
     *     System.out.println("Name: " + row.get("name"));
     *     System.out.println("Age: " + row.get("age"));
     * }
     * }</pre>
     *
     * @return a list of result rows, each represented as a map of column names
     *         to values; never {@code null}
     */
    public static List<Map<String, Object>> select(String query)
    {
        List<Map<String, Object>> results = new ArrayList<>();

        System.out.println("Querying the database...");

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();

            while (rs.next())
            {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++)
                {
                    String columnName = meta.getColumnLabel(i);
                    Object value = rs.getObject(i);
                    row.put(columnName, value);
                }
                results.add(row);
            }

            System.out.println("Query executed successfully.");
        }
        catch (SQLException e)
        {
            System.err.println("Error executing select query:");
            e.printStackTrace();
        }

        return results;
    }

    public static List<Map<String, Object>> select(String query, List<Object> params)
    {
        System.out.println("Querying the database (with params)...");
        List<Map<String, Object>> results = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            // bind parameters
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                ResultSetMetaData meta = rs.getMetaData();
                int columnCount = meta.getColumnCount();

                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        String col = meta.getColumnLabel(i);
                        Object val = rs.getObject(i);
                        row.put(col, val);
                    }
                    results.add(row);
                }
            }

            System.out.println("Query with params executed successfully.");
        } catch (SQLException e) {
            System.err.println("Error executing parametrized select:");
            e.printStackTrace();
        }

        return results;
    }

    /**
     * Retrieves all records from the specified table.
     *
     * <p>This method performs a simple selection of all rows and columns from
     * the given table name.</p>
     *
     * <p><b>Example Usage:</b></p>
     * <pre>{@code
     * List<Map<String, Object>> results = Database.selectAll("students");
     *
     * for (Map<String, Object> row : results) {
     *     System.out.println("ID: " + row.get("id"));
     *     System.out.println("Name: " + row.get("name"));
     *     System.out.println("Age: " + row.get("age"));
     * }
     * }</pre>
     *
     * @param tableName the name of the table from which to retrieve records
     * @return a list of result rows, each represented as a map of column names
     *         to values; never {@code null}
     */
    public static List<Map<String, Object>> selectAll(String tableName)
    {
        System.out.println("Selecting all from " + tableName + "...");
        String sql = "SELECT * FROM " + tableName;
        return select(sql);
    }

    /**
     * Inserts a new record into the specified table.
     *
     * <p>The {@code values} map represents column names and their corresponding
     * values for the new record. The method returns the number of rows affected
     * by the operation. A return value of {@code 1} typically indicates that a 
     * single record was added successfully.</p>
     *
     * <p><b>Example Usage:</b></p>
     * <pre>{@code
     * Map<String, Object> values = new HashMap<>();
     * values.put("name", "John Doe");
     * values.put("age", 20);
     * values.put("major", "Computer Science");
     *
     * int rowsInserted = Database.insert("students", values);
     *
     * if (rowsInserted > 0) {
     *     System.out.println("Student inserted successfully.");
     * }
     * }</pre>
     *
     * @param tableName the name of the table into which the record will be inserted
     * @param values a map of column names to values representing the new record
     * @return the number of records inserted
     */
    public static int insert(String tableName, Map<String, Object> values)
    {
        if (values == null || values.isEmpty())
        {
            System.out.println("No values provided for insert.");
            return 0;
        }

        System.out.println("Inserting into the database...");

        StringBuilder sql = new StringBuilder("INSERT INTO ");
        sql.append(tableName).append(" (");

        StringBuilder placeholders = new StringBuilder();
        List<Object> params = new ArrayList<>();

        // Build column list and placeholders
        for (String column : values.keySet())
        {
            if (!params.isEmpty())
            {
                sql.append(", ");
                placeholders.append(", ");
            }

            sql.append(column);
            placeholders.append("?");
            params.add(values.get(column));
        }

        sql.append(") VALUES (").append(placeholders).append(")");

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++)
            {
                ps.setObject(i + 1, params.get(i));
            }

            int affected = ps.executeUpdate();
            System.out.println("Inserted successfully. Rows affected: " + affected);
            return affected;

        } catch (SQLException e) {
            System.err.println("Error executing insert:");
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Updates existing records in the specified table.
     *
     * <p>The {@code values} map represents the columns to be updated and their
     * new values. The {@code whereClause} defines which records should be
     * modified (for example, {@code "id = 5"}).</p>
     *
     * <p>The method returns the number of rows modified during the update
     * operation. A return value of {@code 1} generally indicates that one
     * record was updated.</p>
     *
     * <p><b>Example Usage:</b></p>
     * <pre>{@code
     * Map<String, Object> updates = new HashMap<>();
     * updates.put("major", "Information Technology");
     *
     * String where = "id = 5";
     *
     * int rowsUpdated = Database.update("students", updates, where);
     *
     * if (rowsUpdated > 0) {
     *     System.out.println("Student record updated.");
     * }
     * }</pre>
     *
     * @param tableName the name of the table containing the records to update
     * @param values a map of column names to their new values
     * @param whereClause the condition used to select which records to update
     * @return the number of records updated
     */
    public static int update(String tableName, Map<String, Object> values, String whereClause)
    {
        if (values == null || values.isEmpty()) {
            System.out.println("No values provided for update.");
            return 0;
        }

        System.out.println("Updating the database...");

        StringBuilder sql = new StringBuilder("UPDATE ");
        sql.append(tableName).append(" SET ");

        List<Object> params = new ArrayList<>();
        boolean first = true;

        for (String column : values.keySet()) {
            if (!first) {
                sql.append(", ");
            }
            sql.append(column).append(" = ?");
            params.add(values.get(column));
            first = false;
        }

        if (whereClause != null && !whereClause.trim().isEmpty()) {
            sql.append(" WHERE ").append(whereClause);
        }

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            int affected = ps.executeUpdate();
            System.out.println("Updated successfully. Rows affected: " + affected);
            return affected;
        } catch (SQLException e) {
            System.err.println("Error executing update:");
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Deletes records from the specified table using the provided condition.
     *
     * <p>The {@code whereClause} defines which records should be removed
     * (for example, {@code "id = 5"}). If the {@code whereClause} is omitted
     * or too broad, multiple records may be deleted depending on the
     * underlying implementation and database state.</p>
     *
     * <p>The method returns the number of rows removed from the database. A
     * return value of {@code 1} generally indicates that one record was
     * deleted.</p>
     *
     * <p><b>Example Usage:</b></p>
     * <pre>{@code
     * String table = "students";
     * String where = "id = 10";
     *
     * int rowsDeleted = Database.delete(table, where);
     *
     * if (rowsDeleted > 0) {
     *     System.out.println("Record deleted successfully.");
     * } else {
     *     System.out.println("No records matched the delete criteria.");
     * }
     * }</pre>
     *
     * @param tableName   the name of the table from which records will be deleted
     * @param whereClause the condition used to select which records to delete
     * @return the number of records deleted
     */
    public static int delete(String tableName, String whereClause)
    {
        System.out.println("Deleting from the database...");

        StringBuilder sql = new StringBuilder("DELETE FROM ");
        sql.append(tableName);

        if (whereClause != null && !whereClause.trim().isEmpty()) {
            sql.append(" WHERE ").append(whereClause);
        }

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int affected = ps.executeUpdate();
            System.out.println("Deleted successfully. Rows affected: " + affected);
            return affected;
        } catch (SQLException e) {
            System.err.println("Error executing delete:");
            e.printStackTrace();
            return 0;
        }
    }
}