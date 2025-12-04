package com.groupthree.sims;

public class Stock
{
    private int id;
    private String name;
    private int stockLevel;
    private int minimumStockLevel;

    public Stock(int id, String name, int stockLevel, int minimumStockLevel)
    {
        this.id = id;
        this.name = name;
        this.stockLevel = stockLevel;
        this.minimumStockLevel = minimumStockLevel;
    }

    public int getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public int getStockLevel()
    {
        return stockLevel;
    }

    public int getMinimumStockLevel()
    {
        return minimumStockLevel;
    }
}
