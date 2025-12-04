package com.groupthree.sims;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
public class SaleSys
{
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
    public static SaleResultStatus processSale(User user, String customerName, Date date, Sale sale)
    {
        System.out.println("Processing sale...");

        // Check for user role / privileges
        if (user == null || !user.isActive())
        {
            System.out.println("User is null or inactive.");
            return SaleResultStatus.NO_PRIVILEGE;
        }

        if (!SecuritySys.canAccessPOSUI(user))
        {
            System.out.println("User does not have POS privileges.");
            return SaleResultStatus.NO_PRIVILEGE;
        }

        // check for stock availability
        if (!validateSale(sale))
        {
            System.out.println("Sale validation failed. Cannot process sale.");
            return SaleResultStatus.OUT_OF_STOCK;
        }

        try
        {
            // reduce stock
            for (Map.Entry<Item, Integer> entry : sale.getItems().entrySet())
            {
                Item item = entry.getKey();
                int quantity = entry.getValue();

                InventorySys.reduceStock(item.getName(), quantity);
            }

            // record sale in history module
            HistorySys.logSales(user, sale);

            //Add sale to the database
            saveSaleToDatabase(sale);

            System.out.println("Sale processed");
            sale.completeSale();

            return SaleResultStatus.SUCCESS;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return SaleResultStatus.ERROR;
        }
    }

    private static void saveSaleToDatabase(Sale sale)
    {
        Map<String, Object> values = new HashMap<>();
        values.put("customer_name", sale.getCustomerName());
        values.put("sale_date", java.sql.Timestamp.valueOf(sale.getSaleTime()));
        values.put("item_id", sale.getSingleItem().getId());
        values.put("quantity", sale.getSingleItemQuantity());

        Database.insert("sales", null);
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
    public static boolean validateSale(Sale sale)
    {
        System.out.println("Validating sale...");

        for (Map.Entry<Item, Integer> item : sale.getItems().entrySet())
        {
            if (!InventorySys.validateStock(item.getKey().getName(), item.getValue()))
            {
                return false;
            }
        }

        System.out.println("Sale validated");
        return true;
    }

    /**
     * Exports a summary report of sales within the specified date range.
     *
     * <p>The method retrieves all sales between {@code startDate} and
     * {@code endDate} using {@link #getSalesHistory(Date, Date)} and then
     * delegates the actual report creation to the Exporter module
     * (to be implemented).</p>
     *
     * <p>The summary report may include information such as total sales
     * amount, number of transactions, and breakdowns by item or category,
     * depending on the final reporting requirements.</p>
     *
     * <p><b>Example Usage:</b></p>
     * <pre>{@code
     * Date from = ...; // start date chosen by the user
     * Date to   = ...; // end date chosen by the user
     *
     * SaleSys.exportSummaryReport(from, to);
     * }</pre>
     *
     * @param startDate the beginning of the period for which the report is generated
     * @param endDate   the end of the period for which the report is generated
     */
    public static boolean exportSummaryReport(Date startDate, Date endDate)
    {
        System.out.println("Exporting summary report...");
        List<Sale> sales = getSalesHistory(startDate, endDate);
        List<String> saleStrings = new ArrayList<>();

        for (Sale sale : sales)
        {
            saleStrings.add(sale.toSummaryString());
        }

        // 3. Path to Desktop
        Path outputFile = Path.of(
                System.getProperty("user.home"),
                "Desktop",
                "sales_report.csv"
        );

        //Use the Exporter module to create the report
        List<String> headers = List.of("Sale Summary");

        try
        {
            CsvExporter.writeCsv(outputFile, saleStrings, headers);
            System.out.println("Report exported to: " + outputFile.toAbsolutePath());
            return true;
        }
        catch (IOException e)
        {
            System.err.println("Failed to export CSV");
            e.printStackTrace();
            return false;
        }
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
    public static List<Sale> getSalesHistory(Date startDate, Date endDate)
    {
        System.out.println("Retrieving sales history...");

        String sql = "SELECT id, customer_name, sale_date, item_id, quantity " +
                     "FROM sales " +
                     "WHERE sale_date BETWEEN ? AND ? " +
                     "ORDER BY sale_date ASC";

        List<Object> params = List.of(
                new Timestamp(startDate.getTime()),
                new Timestamp(endDate.getTime())
        );

        List<Map<String, Object>> sales = Database.select(sql, params);
        List<Sale> formattedSales = new ArrayList<>();

        for(Map<String, Object> row : sales)
        {
            String customerName = (String) row.get("customer_name");
            Timestamp ts = (Timestamp) row.get("sale_date");
            LocalDateTime saleTime = ts != null ? ts.toLocalDateTime() : null;

            int itemId = ((Number) row.get("item_id")).intValue();
            int quantity = ((Number) row.get("quantity")).intValue();

            Sale tempSale = new Sale();
            tempSale.setCustomerName(customerName);
            tempSale.setSaleTime(saleTime);

            Item saleItem = InventorySys.getItemById(itemId);

            if (saleItem != null)
                tempSale.addItem(saleItem, quantity);
            else
                System.out.println("Warning: no item found for id " + itemId);

            formattedSales.add(tempSale);
        }

        System.out.println("Sales history retrieved");
        return formattedSales;
    }
}