package com.groupthree.sims;

/**
 * Represents an item that can be sold or stored in inventory.
 */
public class Item {

    private int id;
    private String name;
    private double price;

    /**
     * Constructs a new item with the specified id, name, and price.
     *
     * @param id    the unique identifier of the item
     * @param name  the name of the item
     * @param price the price of the item
     */
    public Item(int id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    /** @return the item identifier */
    public int getId() {
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
}
