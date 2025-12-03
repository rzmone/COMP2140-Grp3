package com.groupthree.sims;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Coordinates the processing of sales within the SIMS application.
 *
 * <p>This class provides high-level operations for validating sales, updating
 * inventory, and retrieving sales history. It acts as a central point for
 * orchestrating interactions between the {@link Sale} data, the inventory
 * system, and any related history or database modules.</p>
 */
public class SaleSys {

    /**
     * Processes a sale for the specified customer on the given date.
     *
     * <p>The method performs the following steps:</p>
     * <ol>
     *     <li>Validates the sale using {@link #validateSale(Sale)}.</li>
     *     <li>If validation succeeds, reduces stock for each item in the sale
     *         by updating the inventory system.</li>
     *     <li>Records the sale in the history and database modules (to be
     *         implemented).</li>
     * </ol>
     *
     * <p>If validation fails, the method returns {@code false} and no changes
     * are made to inventory.</p>
     *
     * @param customerName the name of the customer making the purchase
     * @param date         the date of the sale
     * @param sale         the sale to be processed
     * @return {@code true} if the sale is processed successfully;
     *         {@code false} if validation fails or the sale cannot be processed
     */
    public static boolean processSale(String customerName, Date date, Sale sale) {
        System.out.println("Processing sale...");

        // check for stock availability
        if (!validateSale(sale)) {
            // TODO: log error on GUI
            System.out.println("Sale validation failed. Cannot process sale.");
            return false;
        }

        // reduce stock
        for (Map.Entry<Item, Integer> item : sale.getItems().entrySet()) {
            // Record in inventory system
            // TODO: Call inventory module to reduce stock for item.getKey() by item.getValue()
        }

        // TODO: Record sale in history module
        // TODO: Add sale database module

        System.out.println("Sale processed");
        return true;
    }

    /**
     * Validates a sale by ensuring that all items have sufficient stock.
     *
     * <p>The method inspects each item-quantity pair in the sale and is
     * expected to consult the inventory system to verify that the requested
     * quantities can be fulfilled. If any item cannot be supplied in the
     * required quantity, the method should return {@code false}.</p>
     *
     * @param sale the sale to validate
     * @return {@code true} if the sale is valid and can be processed;
     *         {@code false} if stock is insufficient for one or more items
     */
    public static boolean validateSale(Sale sale) {
        System.out.println("Validating sale...");

        for (Map.Entry<Item, Integer> item : sale.getItems().entrySet()) {
            // TODO: Check available stock for item.getKey()
            // return false if stock for an item is insufficient
        }

        System.out.println("Sale validated");
        return true;
    }

    /**
     * Retrieves a list of sales that occurred within the specified date range.
     *
     * <p>The method is intended to query the underlying history or database
     * module to obtain all sales whose dates fall between {@code startDate}
     * and {@code endDate}, inclusive or exclusive depending on the final
     * implementation.</p>
     *
     * @param startDate the beginning of the date range for the query
     * @param endDate   the end of the date range for the query
     * @return a list of sales within the given period; currently an empty list
     */
    public static List<Sale> getSalesHistory(Date startDate, Date endDate) {
        System.out.println("Retrieving sales history...");
        System.out.println("Sales history retrieved");
        return List.of();
    }
}
