package com.groupthree.sims;

import java.util.List;
import java.util.Map;

/**
 * Represents an item that can be sold or stored in inventory.
 */
public class Item {

    private String id;
    private String name;
    private double price;

    /**
     * Constructs a new item with the specified id, name, and price.
     *
     * @param id    the unique identifier of the item
     * @param name  the name of the item
     * @param price the price of the item
     */
    public Item(String id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    /** @return the item identifier */
    public String getId() {
        return id;
    }

    /** @return the item name */
    public String getName() {
        return name;
    }

    /** @return the item price */
    public double getPrice() {
        return price;
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
    public static Item getItemById(String id) {
        List<Map<String, Object>> results =
            Database.select("SELECT * FROM Item WHERE id = '" + id + "'");

        if (results.isEmpty()) {
            return null;
        }

        Map<String, Object> row = results.get(0);

        return new Item(
            (String) row.get("id"),
            (String) row.get("name"),
            (double) row.get("price")  // correct type
        );
    }
}
