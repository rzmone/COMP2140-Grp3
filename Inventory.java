
/**
 * Write a description of class Inventory here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
import java.time.LocalDateTime;
import java.io.*;
import java.nio.file.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Main Inventory Management System for Sweetcraft Ltd.
 * Feature ID: INV-001 - Sale Inventory Update
 * This class handles real-time inventory updates when sales occur.
 */

public class Inventory {
private static final boolean DEBUG_MODE = true;
private static final String DATA_FILE = "inventory_data.ser";

    public void exportSalesReport(LocalDateTime start, LocalDateTime end, String filename) {
    List<SaleTransaction> sales = getSalesByDateRange(start, end);
    try (PrintWriter writer = new PrintWriter(filename)) {
        writer.println("Transaction ID,Customer ID,Date,Total,Status");
        for (SaleTransaction sale : sales) {
            writer.println(String.format("%s,%s,%s,%.2f,%s",
                sale.getTransactionId(),
                sale.getCustomerId(),
                sale.getSaleDateTime(),
                sale.getTotalAmount(),
                sale.getStatus()));
        }
    } catch (IOException e) {
        System.err.println("Export failed: " + e.getMessage());
    }
}

    public Inventory(boolean loadSavedData) {
        this.inventoryItems = new ConcurrentHashMap<>();
        this.saleTransactions = new ConcurrentHashMap<>();
        this.saleHistory = Collections.synchronizedList(new ArrayList<>());
        
        if (loadSavedData) {
            loadFromFile();
        } 
    }

    public Inventory() {
        this(false); // Don't load saved data by default
    }

     public void saveToFile() {
        try (ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream(DATA_FILE))) {
            
            // Save inventory items
            out.writeObject(new HashMap<>(inventoryItems));
            // Save sale transactions  
            out.writeObject(new HashMap<>(saleTransactions));
            // Save sale history
            out.writeObject(new ArrayList<>(saleHistory));
            
            System.out.println("Inventory data saved to " + DATA_FILE);
        } catch (IOException e) {
            System.err.println("Error saving data: " + e.getMessage());
        }
    }
    
    @SuppressWarnings("unchecked")
    public void loadFromFile() {
        if (!Files.exists(Paths.get(DATA_FILE))) {
            System.out.println("No saved data found. Starting with fresh inventory.");
            return;
        }
        
        try (ObjectInputStream in = new ObjectInputStream(
                new FileInputStream(DATA_FILE))) {
            
            // Load inventory items
            Map<String, InventoryItem> savedItems = (Map<String, InventoryItem>) in.readObject();
            inventoryItems.clear();
            inventoryItems.putAll(savedItems);
            
            // Load sale transactions
            Map<String, SaleTransaction> savedSales = (Map<String, SaleTransaction>) in.readObject();
            saleTransactions.clear();
            saleTransactions.putAll(savedSales);
            
            // Load sale history
            List<SaleHistoryEntry> savedHistory = (List<SaleHistoryEntry>) in.readObject();
            saleHistory.clear();
            saleHistory.addAll(savedHistory);
            
            System.out.println("Inventory data loaded from " + DATA_FILE);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading data: " + e.getMessage());
        }
    }
    // Inventory item representation
    public static class InventoryItem implements Serializable{
        private static final long serialVersionUID = 1L;
        private final String itemId;
        private String name;
        private String description;
        private int currentStock;
	private final int initialStock;
        private int minimumStockLevel;
        private double unitPrice;
        private final LocalDateTime createdAt;
        private LocalDateTime lastUpdated;
        
        public InventoryItem(String itemId, String name, String description, 
                           int initialStock, int minimumStockLevel, double unitPrice) {
            this.itemId = itemId;
            this.name = name;
            this.description = description;
            this.currentStock = initialStock;
	    this.initialStock = initialStock;
            this.minimumStockLevel = minimumStockLevel;
            this.unitPrice = unitPrice;
            this.createdAt = LocalDateTime.now();
            this.lastUpdated = LocalDateTime.now();
        }
        
          
        // Getters and setters
        public String getItemId() { return itemId; }
        public String getName() { return name; }
        public void setName(String name) { 
            this.name = name; 
            this.lastUpdated = LocalDateTime.now();
        }
        public String getDescription() { return description; }
        public void setDescription(String description) { 
            this.description = description; 
            this.lastUpdated = LocalDateTime.now();
        }
        public int getCurrentStock() { return currentStock; }
	public int getStockChange() {return currentStock - initialStock;}
        public int getMinimumStockLevel() { return minimumStockLevel; }
        public void setMinimumStockLevel(int minimumStockLevel) { 
            this.minimumStockLevel = minimumStockLevel; 
            this.lastUpdated = LocalDateTime.now();
        }
        public double getUnitPrice() { return unitPrice; }
        public void setUnitPrice(double unitPrice) { 
            this.unitPrice = unitPrice; 
            this.lastUpdated = LocalDateTime.now();
        }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public LocalDateTime getLastUpdated() { return lastUpdated; }
        
        // Deduct stock for a sale

        public synchronized boolean deductStock(int quantity) {
            if (quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be positive");
            }
            if (this.currentStock >= quantity) {
                this.currentStock -= quantity;
                this.lastUpdated = LocalDateTime.now();
                return true;
            }
            return false;
        }

         // Add stock (for restocking)

        public synchronized void addStock(int quantity) {
            if (quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be positive");
            }
            this.currentStock += quantity;
            this.lastUpdated = LocalDateTime.now();
        }
        
        /**
         * Check if item is below minimum stock level
         * return true if stock is below minimum level
         */
        public boolean isBelowMinimumStock() {
            return this.currentStock < this.minimumStockLevel;
        }
        
        @Override
        public String toString() {
            return String.format("Item[ID: %s, Name: %s, Stock: %d, Min: %d, Price: $%.2f]", 
                itemId, name, currentStock, minimumStockLevel, unitPrice);
        }
    }

 // Sale transaction representation
    public static class SaleTransaction implements Serializable{
        private static final long serialVersionUID = 1L;
        private final String transactionId;
        private final String customerId;
        private final LocalDateTime saleDateTime;
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
        private final Map<String, SaleItem> items;
        private double totalAmount;
        private String status; // "PENDING", "CONFIRMED", "REJECTED"
        private String rejectedReason;
        private final String confirmedBy; // Actor/source who confirmed the sale
        
        public SaleTransaction(String transactionId, String customerId, String confirmedBy) {
            this.transactionId = transactionId;
            this.customerId = customerId;
            this.saleDateTime = LocalDateTime.now();
            this.items = new HashMap<>();
            this.totalAmount = 0.0;
            this.status = "PENDING";
            this.rejectedReason = null;
            this.confirmedBy = confirmedBy;
        }
        
        public void addItem(String itemId, String itemName, int quantity, double unitPrice) {
            SaleItem saleItem = new SaleItem(itemId, itemName, quantity, unitPrice);
            items.put(itemId, saleItem);
            calculateTotal();
        }
        
        private void calculateTotal() {
            this.totalAmount = items.values().stream()
                .mapToDouble(item -> item.getQuantity() * item.getUnitPrice())
                .sum();
        }
        
        // Getters
        public String getTransactionId() { return transactionId; }
        public String getCustomerId() { return customerId; }
        public LocalDateTime getSaleDateTime() { return saleDateTime; }
        public Map<String, SaleItem> getItems() { return Collections.unmodifiableMap(items); }
        public double getTotalAmount() { return totalAmount; }
        public String getStatus() { return status; }
        public String getRejectedReason() { return rejectedReason; }
        public String getConfirmedBy() { return confirmedBy; }
        
        public void setStatus(String status, String reason) {
            this.status = status;
            if ("REJECTED".equals(status)) {
                this.rejectedReason = reason;
            }
        }
        
        @Override
    public String toString() {
        // Use the formatted date/time
        return String.format("Sale[ID: %s, Customer: %s, Date: %s, Total: $%.2f, Status: %s]", 
            transactionId, 
            customerId, 
            saleDateTime.format(FORMATTER),
            totalAmount, 
            status);
    }
    }
    
    // Individual item in a sale
    public static class SaleItem implements Serializable{
        private static final long serialVersionUID = 1L;
        private final String itemId;
        private final String itemName;
        private final int quantity;
        private final double unitPrice;
        
        public SaleItem(String itemId, String itemName, int quantity, double unitPrice) {
            this.itemId = itemId;
            this.itemName = itemName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }
        
        // Getters
        public String getItemId() { return itemId; }
        public String getItemName() { return itemName; }
        public int getQuantity() { return quantity; }
        public double getUnitPrice() { return unitPrice; }
        public double getTotalPrice() { return quantity * unitPrice; }
    }

public static class SaleHistoryEntry implements Serializable{
        private static final long serialVersionUID = 1L;
        private final String transactionId;
        private final LocalDateTime timestamp;
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
        private final String actor; // Who confirmed/rejected the sale
        private final String action; // "SALE_CONFIRMED" or "SALE_REJECTED"
        private final String details;
        private final String reason;
        

public SaleHistoryEntry(String transactionId, String actor, String action, 
                              String details, String reason) {
            this.transactionId = transactionId;
            this.timestamp = LocalDateTime.now();
            this.actor = actor;
            this.action = action;
            this.details = details;
            this.reason = reason;
        }
        
        // Getters
        public String getTransactionId() { return transactionId; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public String getActor() { return actor; }
        public String getAction() { return action; }
        public String getDetails() { return details; }
        public String getReason() { return reason; }
        
         @Override
	public String toString() {
        // Format the timestamp using the static formatter
        String formattedTime = timestamp.format(FORMATTER);
        
        return String.format("History[Transaction: %s, Time: %s, Actor: %s, Action: %s, Reason: %s]", 
            transactionId, 
            formattedTime,
            actor, 
            action, 
            reason != null ? reason : "N/A"); }
    }

    private final Map<String, InventoryItem> inventoryItems;
    private final Map<String, SaleTransaction> saleTransactions;
    private final List<SaleHistoryEntry> saleHistory;
    
    

public void addInventoryItem(InventoryItem item) {
    // 1. Validate input
    if (item == null) {
        throw new IllegalArgumentException("Item cannot be null");
    }
    
    String itemId = item.getItemId();
    if (itemId == null || itemId.trim().isEmpty()) {
        throw new IllegalArgumentException("Item ID cannot be null or empty");
    }
    
    // 2. Check for duplicates
    if (inventoryItems.containsKey(itemId)) {
        throw new IllegalStateException(
            "Item with ID '" + itemId + "' already exists");
    }
    
    // 3. Add to inventory
    inventoryItems.put(itemId, item);
    
    // 4. Log the action
    saleHistory.add(new SaleHistoryEntry(
        "SYSTEM", "admin", "ITEM_ADDED",
        "Added new inventory item: " + item.getName(),
        null
    ));
    }

public InventoryItem getInventoryItem(String itemId) {
    // 1. Validate input
    if (itemId == null) {
        throw new IllegalArgumentException("Item ID cannot be null");
    }
    
    if (itemId.trim().isEmpty()) {
        throw new IllegalArgumentException("Item ID cannot be empty");
    }
    
    // 2. Look up
    InventoryItem item = inventoryItems.get(itemId);

    // 3. Return result
    return item;
}

public List<InventoryItem> getAllInventoryItems() {
    return new ArrayList<>(inventoryItems.values());
}

public List<InventoryItem> getAllInventoryItems(String sortBy) {
    // First: Create defensive copy
    List<InventoryItem> items = new ArrayList<>(inventoryItems.values());
    
    // Check if empty
    if (items.isEmpty()) {
        System.out.println("Inventory is empty");
        return items;
    }
    
    // Sort based on parameter
    if (sortBy != null) {
        switch (sortBy.toLowerCase()) {
            case "name":
                items.sort(Comparator.comparing(InventoryItem::getName));
                break;
            case "stock":
                items.sort(Comparator.comparingInt(InventoryItem::getCurrentStock));
                break;
            case "price":
                items.sort(Comparator.comparingDouble(InventoryItem::getUnitPrice));
                break;
            case "id":
                items.sort(Comparator.comparing(InventoryItem::getItemId));
                break;
            // Default: no sorting
        }
    }
    
    return items;
}

public synchronized SaleTransaction processSale(SaleTransaction sale, String actor) {
    // Validate sale
    if (sale == null || sale.getItems().isEmpty()) {
        String errorMsg = "Sale transaction is null or has no items";
        logSaleHistory(sale, actor, "SALE_REJECTED", errorMsg, "Invalid sale data");
        throw new IllegalArgumentException(errorMsg);
    }
    
    // Check stock availability for all items first
    Map<String, String> stockCheckErrors = new HashMap<>();
    for (SaleItem item : sale.getItems().values()) {
        InventoryItem inventoryItem = inventoryItems.get(item.getItemId());
        if (inventoryItem == null) {
            stockCheckErrors.put(item.getItemId(), "Item not found in inventory");
        } else if (inventoryItem.getCurrentStock() < item.getQuantity()) {
            stockCheckErrors.put(item.getItemId(), 
                String.format("Insufficient stock. Available: %d, Requested: %d", 
                    inventoryItem.getCurrentStock(), item.getQuantity()));
        }
    }
    
    // If any stock issues, reject the entire sale
    if (!stockCheckErrors.isEmpty()) {
        String reason = "Insufficient stock for items: " + 
            stockCheckErrors.entrySet().stream()
                .map(e -> e.getKey() + " (" + e.getValue() + ")")
                .collect(Collectors.joining(", "));
        
        sale.setStatus("REJECTED", reason);
        saleTransactions.put(sale.getTransactionId(), sale);
        
        // Log to history
        logSaleHistory(sale, actor, "SALE_REJECTED", 
            String.format("Sale rejected: %s", reason), reason);
        
        return sale;
    }
    
    // Lists to track what we've deducted (for rollback if needed)
    List<RollbackEntry> rollbackEntries = new ArrayList<>();
    
    // Deduct stock for all items with rollback tracking
    for (SaleItem item : sale.getItems().values()) {
        InventoryItem inventoryItem = inventoryItems.get(item.getItemId());
        
        // Record current state BEFORE deduction (for potential rollback)
        int previousStock = inventoryItem.getCurrentStock();
        boolean success = inventoryItem.deductStock(item.getQuantity());
        
        if (success) {
            // Track successful deduction for potential rollback
            rollbackEntries.add(new RollbackEntry(inventoryItem, previousStock));
        } else {
            // Deduction failed - need to roll back previous deductions
            String reason = String.format("Failed to deduct stock for item %s", item.getItemId());
            
            // Roll back ALL previously successful deductions
            rollbackAllDeductions(rollbackEntries);
            
            // Update sale status
            sale.setStatus("REJECTED", reason);
            saleTransactions.put(sale.getTransactionId(), sale);
            
            // Log to history
            logSaleHistory(sale, actor, "SALE_REJECTED", 
                String.format("Stock deduction failed and rolled back: %s", reason), 
                reason);
            
            return sale;
        }
    }
    
    // Mark sale as confirmed
    sale.setStatus("CONFIRMED", null);
    saleTransactions.put(sale.getTransactionId(), sale);
    
    // Log to history
    String details = String.format("Sale confirmed. Items: %d, Total: $%.2f", 
        sale.getItems().size(), sale.getTotalAmount());
    logSaleHistory(sale, actor, "SALE_CONFIRMED", details, null);
    
    return sale;
}

//Class for rollback tracking
private static class RollbackEntry {
    final InventoryItem item;
    final int previousStock;
    
    RollbackEntry(InventoryItem item, int previousStock) {
        this.item = item;
        this.previousStock = previousStock;
    }
}

// Rollback method
private void rollbackAllDeductions(List<RollbackEntry> rollbackEntries) {
    for (RollbackEntry entry : rollbackEntries) {
        // Calculate how much was deducted
        int currentStock = entry.item.getCurrentStock();
        int deductedAmount = entry.previousStock - currentStock;
        
        if (deductedAmount > 0) {
            // Restore the deducted stock
            entry.item.addStock(deductedAmount);
        }
    }
}

 public SaleTransaction processSaleDirect(String transactionId, String customerId, 
                                           Map<String, Integer> itemQuantities, 
                                           String actor) {
        // Create sale transaction
        SaleTransaction sale = new SaleTransaction(transactionId, customerId, actor);
        
        // Add items to sale
        for (Map.Entry<String, Integer> entry : itemQuantities.entrySet()) {
            String itemId = entry.getKey();
            int quantity = entry.getValue();
            
            InventoryItem inventoryItem = inventoryItems.get(itemId);
            if (inventoryItem == null) {
                throw new IllegalArgumentException("Item not found: " + itemId);
            }
            
            if (quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be positive for item: " + itemId);
            }
            
            sale.addItem(itemId, inventoryItem.getName(), quantity, inventoryItem.getUnitPrice());
        }
        
        // Process the sale
        return processSale(sale, actor);
    }

private void logSaleHistory(SaleTransaction sale, String actor, String action, 
                          String details, String reason) {
    try {
        // Handle null sale
        String transactionId = sale != null ? sale.getTransactionId() : "UNKNOWN";
        
        // Create history entry
        SaleHistoryEntry historyEntry = new SaleHistoryEntry(
            transactionId,
            actor,
            action,
            details,
            reason
        );
        
        // Add to synchronized list (thread-safe)
        saleHistory.add(historyEntry);
        
        // Use proper logging instead of System.out
        // logger.debug("History logged: {}", historyEntry);
        
        // Keep console output for debugging
        if (DEBUG_MODE) {
            System.out.println("[HISTORY LOGGED] " + historyEntry);
        }
        
    } catch (Exception e) {
        // Log the logging failure (meta!)
        System.err.println("[ERROR] Failed to log history: " + e.getMessage());
        // Don't throw logging failure shouldn't break main functionality
    }
}

public SaleTransaction getSale(String transactionId) {
        return saleTransactions.get(transactionId);
    }
    
    /**
     * Get all sales within a date range
     */
    public List<SaleTransaction> getSalesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return saleTransactions.values().parallelStream()
            .filter(sale -> !sale.getSaleDateTime().isBefore(startDate) && 
                           !sale.getSaleDateTime().isAfter(endDate))
            .collect(Collectors.toList());
    }

public Map<LocalDateTime, Double> getDailySalesSummary(LocalDateTime date) {
        LocalDateTime startOfDay = date.withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = date.withHour(23).withMinute(59).withSecond(59);
        
        List<SaleTransaction> daysSales = getSalesByDateRange(startOfDay, endOfDay);
        
        Map<LocalDateTime, Double> summary = new HashMap<>();
        for (SaleTransaction sale : daysSales) {
            if ("CONFIRMED".equals(sale.getStatus())) {
                // Group by hour for more detailed summary
                LocalDateTime hour = sale.getSaleDateTime().withMinute(0).withSecond(0);
                summary.merge(hour, sale.getTotalAmount(), Double::sum);
            }
        }
        
        return summary;
    }

 public List<SaleHistoryEntry> getSaleHistory() {
        return new ArrayList<>(saleHistory);
    }
    
    /**
     * Get sale history for a specific transaction
     */
    public List<SaleHistoryEntry> getSaleHistoryForTransaction(String transactionId) {
        return saleHistory.stream()
            .filter(entry -> entry.getTransactionId().equals(transactionId))
            .collect(Collectors.toList());
    }

 public List<InventoryItem> getItemsBelowMinimumStock() {
        return inventoryItems.values().stream()
            .filter(InventoryItem::isBelowMinimumStock)
            .collect(Collectors.toList());
    }

public void restockItem(String itemId, int quantity, String actor) {
        InventoryItem item = inventoryItems.get(itemId);
        if (item == null) {
            throw new IllegalArgumentException("Item not found: " + itemId);
        }
        
        item.addStock(quantity);
        
        // Log restocking action
        SaleHistoryEntry historyEntry = new SaleHistoryEntry(
            "RESTOCK-" + System.currentTimeMillis(),
            actor,
            "RESTOCK",
            String.format("Restocked item %s with %d units. New stock: %d", 
                itemId, quantity, item.getCurrentStock()),
            null
        );
        saleHistory.add(historyEntry);
    }

/**
     * Get inventory summary report
     */
    public String generateInventorySummary() {
        StringBuilder report = new StringBuilder();
        report.append("=== INVENTORY SUMMARY ===\n");
        report.append(String.format("Total Items: %d\n", inventoryItems.size()));
        
        int totalStockValue = 0;
        double totalMonetaryValue = 0.0;
        
        for (InventoryItem item : inventoryItems.values()) {
            totalStockValue += item.getCurrentStock();
            totalMonetaryValue += item.getCurrentStock() * item.getUnitPrice();
            
            report.append(String.format("\n%s: %d units @ $%.2f each = $%.2f", 
                item.getName(), item.getCurrentStock(), item.getUnitPrice(),
                item.getCurrentStock() * item.getUnitPrice()));
            
            if (item.isBelowMinimumStock()) {
                report.append(" [LOW STOCK!]");
            }
        }
        
        report.append(String.format("\n\nTotal Stock Units: %d", totalStockValue));
        report.append(String.format("\nTotal Monetary Value: $%.2f", totalMonetaryValue));
        
        List<InventoryItem> lowStockItems = getItemsBelowMinimumStock();
        if (!lowStockItems.isEmpty()) {
            report.append("\n\n=== LOW STOCK ALERT ===\n");
            for (InventoryItem item : lowStockItems) {
                report.append(String.format("%s: %d units (Min: %d)\n", 
                    item.getName(), item.getCurrentStock(), item.getMinimumStockLevel()));
            }
        }
        
        return report.toString();
    }

public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);
    
    System.out.println("=== Sweetcraft Ltd. Inventory Management System ===\n");
    
    // Ask if user wants to load saved data
    System.out.print("Load saved inventory? (yes/no): ");
    String response = scanner.nextLine().toLowerCase();
    boolean loadSaved = response.equals("yes") || response.equals("y");
    
    Inventory inventory = new Inventory(loadSaved);
    
    // Main menu loop
    boolean running = true;
    while (running) {
        System.out.println("\n=== MAIN MENU ===");
        System.out.println("1. Add new inventory item");
        System.out.println("2. Process a sale");
        System.out.println("3. View inventory");
        System.out.println("4. Restock item");
        System.out.println("5. View sales history");
        System.out.println("6. Generate reports");
        System.out.println("7. Save and exit");
        System.out.print("Select option (1-7): ");
        
        String choice = scanner.nextLine();
        
        switch (choice) {
            case "1":
                addNewInventoryItem(scanner, inventory);
                break;
            case "2":
                processSaleMenu(scanner, inventory);
                break;
            case "3":
                viewInventoryMenu(scanner, inventory);
                break;
            case "4":
                restockItemMenu(scanner, inventory);
                break;
            case "5":
                viewSalesHistory(inventory);
                break;
            case "6":
                generateReportsMenu(scanner, inventory);
                break;
            case "7":
                inventory.saveToFile();
                System.out.println("Data saved. Goodbye!");
                running = false;
                break;
            default:
                System.out.println("Invalid option. Please try again.");
        }
    }
    scanner.close();
}

// Helper methods for the menu options:

private static void addNewInventoryItem(Scanner scanner, Inventory inventory) {
    System.out.println("\n=== ADD NEW INVENTORY ITEM ===");
    
    System.out.print("Item ID: ");
    String itemId = scanner.nextLine();
    
    System.out.print("Item Name: ");
    String name = scanner.nextLine();
    
    System.out.print("Description: ");
    String description = scanner.nextLine();
    
    System.out.print("Initial Stock: ");
    int initialStock = Integer.parseInt(scanner.nextLine());
    
    System.out.print("Minimum Stock Level: ");
    int minStock = Integer.parseInt(scanner.nextLine());
    
    System.out.print("Unit Price: ");
    double price = Double.parseDouble(scanner.nextLine());
    
    try {
        InventoryItem newItem = new InventoryItem(itemId, name, description, 
            initialStock, minStock, price);
        inventory.addInventoryItem(newItem);
        System.out.println("Item added successfully!");
    } catch (Exception e) {
        System.out.println("Error: " + e.getMessage());
    }
}

private static void processSaleMenu(Scanner scanner, Inventory inventory) {
    System.out.println("\n=== PROCESS SALE ===");
    
    System.out.print("Transaction ID: ");
    String transId = scanner.nextLine();
    
    System.out.print("Customer ID: ");
    String custId = scanner.nextLine();
    
    System.out.print("Salesperson name: ");
    String salesperson = scanner.nextLine();
    
    Map<String, Integer> items = new HashMap<>();
    boolean addingItems = true;
    
    while (addingItems) {
        System.out.print("Item ID to sell (or 'done' to finish): ");
        String itemId = scanner.nextLine();
        
        if (itemId.equalsIgnoreCase("done")) {
            addingItems = false;
            continue;
        }
        
        System.out.print("Quantity: ");
        int quantity = Integer.parseInt(scanner.nextLine());
        
        items.put(itemId, quantity);
    }
    
    if (items.isEmpty()) {
        System.out.println("No items added. Sale cancelled.");
        return;
    }
    
    try {
        SaleTransaction sale = inventory.processSaleDirect(transId, custId, items, salesperson);
        System.out.println("Sale Result: " + sale);
        System.out.println("Status: " + sale.getStatus());
        if (sale.getRejectedReason() != null) {
            System.out.println("Reason: " + sale.getRejectedReason());
        }
    } catch (Exception e) {
        System.out.println("Sale Error: " + e.getMessage());
    }
}

private static void viewInventoryMenu(Scanner scanner, Inventory inventory) {
    System.out.println("\n=== VIEW INVENTORY ===");
    System.out.println("1. View all items");
    System.out.println("2. View low stock items");
    System.out.println("3. Search item by ID");
    System.out.print("Select option: ");
    
    String choice = scanner.nextLine();
    
    switch (choice) {
        case "1":
            System.out.println("\n" + inventory.generateInventorySummary());
            break;
        case "2":
            List<InventoryItem> lowStock = inventory.getItemsBelowMinimumStock();
            if (lowStock.isEmpty()) {
                System.out.println("No items below minimum stock level.");
            } else {
                lowStock.forEach(item -> System.out.println("- " + item));
            }
            break;
        case "3":
            System.out.print("Enter Item ID: ");
            String itemId = scanner.nextLine();
            InventoryItem item = inventory.getInventoryItem(itemId);
            if (item != null) {
                System.out.println(item);
                System.out.println("Stock change from initial: " + item.getStockChange());
            } else {
                System.out.println("Item not found.");
            }
            break;
        default:
            System.out.println("Invalid option.");
    }
}

private static void restockItemMenu(Scanner scanner, Inventory inventory) {
    System.out.println("\n=== RESTOCK ITEM ===");
    
    System.out.print("Item ID: ");
    String itemId = scanner.nextLine();
    
    System.out.print("Quantity to add: ");
    int quantity = Integer.parseInt(scanner.nextLine());
    
    System.out.print("Restocked by: ");
    String actor = scanner.nextLine();
    
    try {
        inventory.restockItem(itemId, quantity, actor);
        System.out.println("Restock successful!");
    } catch (Exception e) {
        System.out.println("Error: " + e.getMessage());
    }
}

private static void viewSalesHistory(Inventory inventory) {
    System.out.println("\n=== SALES HISTORY ===");
    List<SaleHistoryEntry> history = inventory.getSaleHistory();
    if (history.isEmpty()) {
        System.out.println("No sales history available.");
    } else {
        history.forEach(entry -> 
            System.out.println("- " + entry.getTimestamp() + " - " + 
                entry.getAction() + " by " + entry.getActor() + 
                " - Transaction: " + entry.getTransactionId()));
    }
}

private static void generateReportsMenu(Scanner scanner, Inventory inventory) {
    System.out.println("\n=== GENERATE REPORTS ===");
    System.out.println("1. Daily sales summary");
    System.out.println("2. Export sales report to CSV");
    System.out.print("Select option: ");
    
    String choice = scanner.nextLine();
    
    switch (choice) {
        case "1":
            Map<LocalDateTime, Double> summary = inventory.getDailySalesSummary(LocalDateTime.now());
            if (summary.isEmpty()) {
                System.out.println("No sales for today.");
            } else {
                summary.forEach((hour, total) -> 
                    System.out.println(String.format("Hour %s: $%.2f", 
                        hour.toLocalTime().toString().substring(0, 5), total)));
            }
            break;
        case "2":
            System.out.print("Start date (YYYY-MM-DD): ");
            String startStr = scanner.nextLine() + "T00:00:00";
            LocalDateTime start = LocalDateTime.parse(startStr.replace(" ", "T"));
            
            System.out.print("End date (YYYY-MM-DD): ");
            String endStr = scanner.nextLine() + "T23:59:59";
            LocalDateTime end = LocalDateTime.parse(endStr.replace(" ", "T"));
            
            System.out.print("Filename (e.g., report.csv): ");
            String filename = scanner.nextLine();
            
            inventory.exportSalesReport(start, end, filename);
            break;
        default:
            System.out.println("Invalid option.");
    }
}
}