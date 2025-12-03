package com.groupthree.sims;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a sale transaction consisting of one or more items and their
 * associated quantities. The class provides functionality for adding items,
 * calculating the total cost, and retrieving a read-only view of all items
 * included in the sale.
 *
 * <p>This class maintains its internal item list privately and ensures that
 * external code cannot modify the underlying data structure directly. Only
 * controlled updates through {@link #addItem(Item, int)} are permitted.</p>
 */
class Sale {

    /** 
     * Stores the items included in the sale along with their quantities.
     * The key represents the item, and the value represents the quantity.
     */
    private Map<Item, Integer> items;

    /**
     * Constructs a new, empty sale.
     */
    public Sale() {
        this.items = new HashMap<>();
    }

    /**
     * Adds an item and its quantity to the sale. If the item already exists
     * in the sale, its quantity will be replaced with the new value.
     *
     * @param item     the item being added
     * @param quantity the quantity of the item
     */
    public void addItem(Item item, int quantity) {
        items.put(item, quantity);
    }

    /**
     * Calculates the total monetary value of the sale.
     *
     * <p>The total is computed by summing the price of each item multiplied
     * by its quantity.</p>
     *
     * @return the total amount of the sale
     */
    public double getTotalAmount() {
        double sum = 0;

        for (Map.Entry<Item, Integer> entry : items.entrySet()) {
            sum += entry.getKey().getPrice() * entry.getValue();
        }

        return sum;
    }

    /**
     * Returns an unmodifiable view of the items included in this sale.
     *
     * <p>The returned map cannot be altered by the caller. Any attempt to
     * modify it will result in an {@code UnsupportedOperationException}. This
     * ensures that the integrity of the sale's internal data is preserved.</p>
     *
     * @return a read-only map of items to their quantities
     */
    public Map<Item, Integer> getItems() {
        return Collections.unmodifiableMap(items);
    }

    /**
     * Returns a human-readable string representation of the sale,
     * including each item, its quantity, its price, and the total amount.
     *
     * @return a string describing the sale details
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Sale Details:\n");

        for (Map.Entry<Item, Integer> entry : items.entrySet()) {
            sb.append("Item: ").append(entry.getKey().getName())
              .append(", Quantity: ").append(entry.getValue())
              .append(", Price: ").append(entry.getKey().getPrice())
              .append("\n");
        }

        sb.append("Total Amount: ").append(getTotalAmount()).append("\n");
        return sb.toString();
    }
}
