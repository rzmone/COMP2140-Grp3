package com.groupthree.sims;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class SaleSys
{
    public static boolean processSale(String customerName, Date date, Sale sale)
    {
        System.out.println("Processing sale...");

        // check for stock availability
        if (!validateSale(sale))
        {
            // TODO: log error on GUI
            System.out.println("Sale validation failed. Cannot process sale.");
            return false;
        }

        // reduce stock
        for (Map.Entry<Item, Integer> item : sale.getItems().entrySet())
        {
            //Record in inventory system
        }

        // TODO: Record sale in history module
        // TODO: Add sale database module

        System.out.println("Sale processed");
        return true;
    }

    public static boolean validateSale(Sale sale)
    {
        System.out.println("Validating sale...");

        for (Map.Entry<Item, Integer> item : sale.getItems().entrySet())
        {
            // return false if stock for an item is insufficient
        }

        System.out.println("Sale validated");
        return true;
    }

    public static List<Sale> getSalesHistory(Date startDate, Date endDate)
    {
        System.out.println("Retrieving sales history...");
        System.out.println("Sales history retrieved");
        return List.of();
    }
}