package com.groupthree.sims;

public class ProductionSys
{
    public void recordProduction(String batchId,
                                 int itemId,
                                 int goodQty,
                                 int defectiveQty,
                                 int userId) {

        System.out.println("=== Production Recorded ===");
        System.out.println("Batch ID: " + batchId);
        System.out.println("Item ID: " + itemId);
        System.out.println("Good Quantity: " + goodQty);
        System.out.println("Defective Quantity: " + defectiveQty);
        System.out.println("Recorded by user: " + userId);

        // Update inventory for good items
        InventorySys.updateStock(itemId, goodQty);

        // Log to history
        String details = "Batch " + batchId +
                " | Item " + itemId +
                " | Good=" + goodQty +
                " | Defective=" + defectiveQty;

        HistorySys.logAny(SecuritySys.findUserById(userId), "Production Recorded", details);

        // check thresholds
        AlertSys.checkLowStockThreshold(itemId);

        System.out.println("Production saved.\n");
    }

    
    public void recordDefects(String batchId,
                              int itemId,
                              int defectiveQty,
                              String reason,
                              int userId) {

        System.out.println("=== Defect Recorded ===");
        System.out.println("Batch ID: " + batchId);
        System.out.println("Item ID: " + itemId);
        System.out.println("Defective Quantity: " + defectiveQty);
        System.out.println("Reason: " + reason);
        System.out.println("Recorded by user: " + userId);

        // Log defect in history
        String details = "Batch " + batchId +
                " | Item " + itemId +
                " | Defective=" + defectiveQty +
                " | Reason=" + reason;
                
        HistorySys.logAny(SecuritySys.findUserById(userId), "DEFECT", details);

        // Get threshold from AlertSys 
        //int threshold = AlertSys.getDefectThreshold();
        int threshold = 10; // Example threshold

        // Check if this defect count passes the threshold
        if (defectiveQty >= threshold) {
            String msg = "Defect threshold exceeded for item " + itemId +
                    " in batch " + batchId +
                    ". Defective quantity: " + defectiveQty +
                    " (threshold: " + threshold + ")";
            //AlertSys.sendAlert(msg);
        } else {
            System.out.println("Defects are below threshold (" + threshold + "). No alert sent.");
        }

        System.out.println("Defect saved.\n");
    }
}
