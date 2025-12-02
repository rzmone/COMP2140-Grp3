package com.groupthree.sims;

import java.util.HashMap;
import java.util.Map;

public class SaleSys {
    public static boolean processSale(Sale sale) {
        // Implement sale processing logic here
        System.out.println("Processing sale...");
        System.out.println("Sale processed");
        return true; // Placeholder return value
    }

    public static boolean postSale(Sale sale) {
        // Implement post-sale processing logic here
        System.out.println("Posting sale...");
        System.out.println("Sale posted");
        return true; // Placeholder return value
    }
}

class Sale {
    private Map<Item, Integer> items;

    public Sale() {
        this.items = new HashMap<Item, Integer>();
    }

    public void addItem(Item item, int quantity) {
        items.put(item, quantity);
    }

    public double getTotalAmount() {
        int sum = 0;

        for (Map.Entry<Item, Integer> item : items.entrySet())
        {
            sum += item.getKey().getPrice() * item.getValue();
        }

        return sum;
    }
}

class Item {
    private String name;
    private double price;

    public Item(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }
}