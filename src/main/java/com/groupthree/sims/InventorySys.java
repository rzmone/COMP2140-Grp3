package com.groupthree.sims;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventorySys
{
    /**
     * Validates whether there is enough stock for the specified item name
     * to satisfy the requested quantity.
     *
     * <p>This method:
     * <ul>
     *   <li>Looks up the stock record by name</li>
     *   <li>Returns {@code true} if the item exists and its
     *       {@code stockLevel} is greater than or equal to the requested quantity</li>
     *   <li>Returns {@code false} otherwise (including invalid arguments)</li>
     * </ul>
     *
     * <p><b>Example Usage:</b></p>
     * <pre>{@code
     * boolean canSell = InventorySys.validateStock("500ml", 3);
     * if (canSell) {
     *     // proceed with sale
     * }
     * }</pre>
     *
     * @param name     the name of the stock item
     * @param quantity the quantity requested; must be greater than 0
     * @return {@code true} if there is enough stock; {@code false} otherwise
     */
    public static boolean validateStock(String name, int quantity)
    {
        // Basic argument validation
        if (name == null || name.isEmpty() || quantity <= 0)
        {
            return false;
        }

        // Look up the stock record for the given name
        Stock stock = findStockByName(name);
        if (stock == null)
        {
            return false;
        }

        // Check if current stock is enough to satisfy the request
        return stock.getStockLevel() >= quantity;
    }

    /**
     * Reduces the stock level of the specified item by the given quantity.
     *
     * <p>If the quantity to reduce exceeds the current stock level,
     * the stock level is clamped to {@code 0}. If the item cannot be
     * found or the arguments are invalid, no change is made.</p>
     *
     * <p><b>Example Usage:</b></p>
     * <pre>{@code
     * // Reduce stock of "500ml" by 2 units
     * InventorySys.reduceStock("500ml", 2);
     * }</pre>
     *
     * @param name     the name of the stock item to reduce
     * @param quantity the quantity to subtract from the current stock;
     *                 must be greater than 0
     */
    public static void reduceStock(String name, int quantity)
    {
        if (name == null || name.isEmpty() || quantity <= 0)
        {
            return;
        }

        // Fetch the current stock record
        Stock stock = findStockByName(name);
        if (stock == null)
        {
            System.out.println("reduceStock: No stock record found for item '" + name + "'.");
            return;
        }

        // Compute the new stock level (do not allow negative values)
        int newLevel = stock.getStockLevel() - quantity;
        if (newLevel < 0)
        {
            newLevel = 0;
        }

        // Prepare the update values
        Map<String, Object> updates = new HashMap<>();
        updates.put("stockLevel", newLevel);

        // Use the ID for the WHERE clause (more reliable than name)
        String where = "id = " + stock.getId();

        // Apply the update in the database
        Database.update("stock", updates, where);
    }

    /**
     * Increases the stock level of the specified item by the given quantity.
     *
     * <p>If the item cannot be found or the arguments are invalid,
     * no change is made.</p>
     *
     * <p><b>Example Usage:</b></p>
     * <pre>{@code
     * // Add 10 units to the stock of "1l"
     * InventorySys.increaseStock("1l", 10);
     * }</pre>
     *
     * @param name     the name of the stock item to increase
     * @param quantity the quantity to add to the current stock;
     *                 must be greater than 0
     */
    public static void increaseStock(String name, int quantity)
    {
        if (name == null || name.isEmpty() || quantity <= 0)
        {
            return;
        }

        // Fetch the current stock record
        Stock stock = findStockByName(name);
        if (stock == null)
        {
            System.out.println("increaseStock: No stock record found for item '" + name + "'.");
            return;
        }

        // Compute the new stock level
        int newLevel = stock.getStockLevel() + quantity;

        // Prepare the update values
        Map<String, Object> updates = new HashMap<>();
        updates.put("stockLevel", newLevel);

        String where = "id = " + stock.getId();

        // Apply the update in the database
        Database.update("stock", updates, where);
    }

    /**
     * Sets the stock level of the specified item to the given quantity.
     *
     * <p>If a stock record for the item already exists, its
     * {@code stockLevel} is updated. If no such record exists, nothing
     * happens.</p>
     *
     * <p><b>Example Usage:</b></p>
     * <pre>{@code
     * // Set the stock of "500ml" to exactly 25 units
     * InventorySys.updateStock("500ml", 25);
     * }</pre>
     *
     * @param name     the name of the stock item
     * @param quantity the desired stock level; must be 0 or greater
     */
    public static void updateStock(String name, int quantity)
    {
        if (name == null || name.isEmpty() || quantity < 0)
        {
            return;
        }

        // Try to find an existing stock record
        Stock stock = findStockByName(name);

        if (stock == null)
        {
            return;
        }
        else
        {
            // Existing record: update the stock level
            Map<String, Object> updates = new HashMap<>();
            updates.put("stockLevel", quantity);

            String where = "id = " + stock.getId();
            Database.update("stock", updates, where);
        }
    }

    /**
     * Retrieves an item from the database using its unique identifier.
     *
     * <p>This method performs a lookup query and returns a fully constructed
     * {@code Item} instance based on the result.</p>
     *
     * <p><b>Example Usage:</b></p>
     * <pre>{@code
     * Item item = Item.getItemById("I001");
     * System.out.println(item.getName());
     * }</pre>
     *
     * @param id the identifier of the item to retrieve
     * @return the matching item, or {@code null} if no item is found
     */
    public static Item getItemById(int id) {
        List<Map<String, Object>> results =
            Database.select("SELECT * FROM Items WHERE id = '" + id + "'");

        if (results.isEmpty()) {
            return null;
        }

        Map<String, Object> row = results.get(0);

        return new Item(
            (int) row.get("id"),
            (String) row.get("name"),
            (double) row.get("price")  // correct type
        );
    }

    /**
     * Retrieves an item from the database using its name.
     *
     * <p>This method performs a lookup query and returns a fully constructed
     * {@code Item} instance based on the result.</p>
     *
     * <p><b>Example Usage:</b></p>
     * <pre>{@code
     * Item item = Item.getItemByName("Widget");
     * System.out.println(item.getId());
     * }</pre>
     *
     * @param name the name of the item to retrieve
     * @return the matching item, or {@code null} if no item is found
     */
    public static Item getItemByName(String name) {
        List<Map<String, Object>> results =
            Database.select("SELECT * FROM Items WHERE name = '" + name + "'");

        if (results.isEmpty()) {
            return null;
        }

        Map<String, Object> row = results.get(0);

        return new Item(
            (int) row.get("id"),
            (String) row.get("name"),
            (double) row.get("price")  // correct type
        );
    }

    /**
     * Finds a {@link Stock} record by its name.
     *
     * <p>This method performs a lookup on the {@code stock} table using the
     * provided name and returns a {@link Stock} object if found.</p>
     *
     * @param name the name of the stock item
     * @return the corresponding {@code Stock} instance,
     *         or {@code null} if no matching record is found or the name is invalid
     */
    public static Stock findStockByName(String name)
    {
        if (name == null || name.isEmpty())
        {
            return null;
        }

        // Escape single quotes to avoid breaking the SQL string
        String safeName = escapeSql(name);

        List<Map<String, Object>> results =
            Database.select("SELECT * FROM stock WHERE name = '" + safeName + "'");

        if (results.isEmpty())
        {
            return null;
        }

        return mapRowToStock(results.get(0));
    }

    /**
     * Finds a {@link Stock} record by its unique identifier.
     *
     * <p>This method performs a lookup on the {@code stock} table using the
     * provided ID and returns a {@link Stock} object if found.</p>
     *
     * @param id the primary key of the stock record
     * @return the corresponding {@code Stock} instance,
     *         or {@code null} if no matching record is found
     */
    public static Stock findStockById(int id)
    {
        List<Map<String, Object>> results =
            Database.select("SELECT * FROM stock WHERE id = " + id);

        if (results.isEmpty())
        {
            return null;
        }

        return mapRowToStock(results.get(0));
    }

    /**
     * Converts a database row into a {@link Stock} object.
     *
     * <p>This helper expects the map to contain the following keys that match
     * the {@code stock} table columns:
     * <ul>
     *   <li>{@code "id"}</li>
     *   <li>{@code "name"}</li>
     *   <li>{@code "stockLevel"}</li>
     *   <li>{@code "minimumStockLevel"}</li>
     * </ul>
     *
     * @param row a map representing a single database row
     * @return a new {@code Stock} instance, or {@code null} if {@code row} is {@code null}
     */
    private static Stock mapRowToStock(Map<String, Object> row)
    {
        if (row == null)
        {
            return null;
        }

        int id = ((Number) row.get("id")).intValue();
        String name = (String) row.get("name");
        int stockLevel = ((Number) row.get("stockLevel")).intValue();
        int minimumStockLevel = ((Number) row.get("minimumStockLevel")).intValue();

        return new Stock(id, name, stockLevel, minimumStockLevel);
    }

    /**
     * Escapes single quotes in a string so that it can be safely injected
     * into a simple SQL literal.
     *
     * <p><b>Note:</b> This is a minimal helper for this project. In a real
     * application, you should strongly prefer prepared statements with
     * {@link java.sql.PreparedStatement} to avoid SQL injection.</p>
     *
     * @param value the original string value
     * @return the escaped value, or {@code null} if {@code value} is {@code null}
     */
    private static String escapeSql(String value)
    {
        return value == null ? null : value.replace("'", "''");
    }
}