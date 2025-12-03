import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * POSGUI
 *  - Graphical Point-of-Sale UI for Sweetcraft.
 *  - Lets staff enter a sale and see a simple receipt.
 *
 * Matches UML (POSUI):
 *   +displayScreen()
 *   +enterSale()
 *   +showReceipt()
 *
 * NOTE:
 *   All calls to SaleSys and InventorySys are COMMENTED OUT.
 *   Your group members can plug them in later.
 */
public class POSGUI extends JFrame {

    private JTextField employeeIdField;
    private JTextField customerField;
    private JTextField bottleTypeField;
    private JTextField quantityField;
    private JTextField unitPriceField;

    private JTextArea outputArea;

    /*
     * ================== BACKEND HOOKS (for later) ==================
     *
     * // Fields:
     * private SaleSys saleSys;
     * private InventorySys inventorySys;
     *
     * // Constructor for full integration:
     * public POSGUI(SaleSys saleSys, InventorySys inv) {
     *     this();
     *     this.saleSys = saleSys;
     *     this.inventorySys = inv;
     * }
     */

    /**
     * Default constructor: builds the POS screen.
     */
    public POSGUI() {
        super("Sweetcraft - POS Terminal");

        // Basic window settings
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 420);
        setLocationRelativeTo(null);  // center on screen
        setLayout(new BorderLayout());

        // ===== Top: input form =====
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        // Row 1: Employee ID
        formPanel.add(new JLabel("Employee ID:"));
        employeeIdField = new JTextField();
        formPanel.add(employeeIdField);

        // Row 2: Customer name
        formPanel.add(new JLabel("Customer name:"));
        customerField = new JTextField();
        formPanel.add(customerField);

        // Row 3: Bottle type
        formPanel.add(new JLabel("Bottle type (e.g. 500ml, 1L):"));
        bottleTypeField = new JTextField();
        formPanel.add(bottleTypeField);

        // Row 4: Quantity
        formPanel.add(new JLabel("Quantity:"));
        quantityField = new JTextField();
        formPanel.add(quantityField);

        // Row 5: Unit price
        formPanel.add(new JLabel("Unit price:"));
        unitPriceField = new JTextField();
        formPanel.add(unitPriceField);

        add(formPanel, BorderLayout.NORTH);

        // ===== Center: receipt / output area =====
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(outputArea);
        add(scroll, BorderLayout.CENTER);

        // ===== Bottom: buttons =====
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton recordSaleButton = new JButton("Record Sale");
        JButton clearButton = new JButton("Clear");
        JButton exitButton = new JButton("Exit");

        // Make buttons light yellow
        Color lightYellow = new Color(255, 255, 153);
        recordSaleButton.setBackground(lightYellow);
        clearButton.setBackground(lightYellow);
        exitButton.setBackground(lightYellow);

        recordSaleButton.setOpaque(true);
        clearButton.setOpaque(true);
        exitButton.setOpaque(true);

        buttonPanel.add(recordSaleButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(exitButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // ===== Button actions =====
        // Use UML method name enterSale()
        recordSaleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enterSale();
            }
        });

        clearButton.addActionListener(e -> outputArea.setText(""));
        exitButton.addActionListener(e -> dispose());
    }

    // ================= UML METHODS =================

    /**
     * UML: +displayScreen()
     * Shows the POS window.
     */
    public void displayScreen() {
        setVisible(true);
    }

    /**
     * UML: +enterSale()
     * Handles the main sale entry: validation + (later) backend calls.
     */
    public void enterSale() {
        String empId         = employeeIdField.getText().trim();
        String customer      = customerField.getText().trim();
        String bottleType    = bottleTypeField.getText().trim();
        String quantityText  = quantityField.getText().trim();
        String unitPriceText = unitPriceField.getText().trim();

        // ==== Basic "required field" checks ====
        if (empId.isEmpty() || customer.isEmpty() || bottleType.isEmpty()
                || quantityText.isEmpty() || unitPriceText.isEmpty()) {
            showReceipt("Please fill in ALL fields before recording a sale.");
            showReceipt("--------------------------------------------------");
            return;
        }

        int quantity;
        double unitPrice;

        // ==== Quantity must be a positive integer ====
        try {
            quantity = Integer.parseInt(quantityText);
            if (quantity <= 0) {
                showReceipt("Quantity must be a positive integer.");
                showReceipt("--------------------------------------------------");
                return;
            }
        } catch (NumberFormatException ex) {
            showReceipt("Quantity must be a valid integer.");
            showReceipt("--------------------------------------------------");
            return;
        }

        // ==== Unit price must be a non-negative number ====
        try {
            unitPrice = Double.parseDouble(unitPriceText);
            if (unitPrice < 0) {
                showReceipt("Unit price cannot be negative.");
                showReceipt("--------------------------------------------------");
                return;
            }
        } catch (NumberFormatException ex) {
            showReceipt("Unit price must be a valid number.");
            showReceipt("--------------------------------------------------");
            return;
        }

        double total = quantity * unitPrice;

        // ========== HARD-CODED DEMO RECEIPT ==========
        StringBuilder sb = new StringBuilder();
        sb.append("The followng was recorded: \n");
        sb.append("  Employee: ").append(empId).append("\n");
        sb.append("  Customer: ").append(customer).append("\n");
        sb.append("  Product:  ").append(bottleType).append("\n");
        sb.append("  Quantity: ").append(quantity).append("\n");
        sb.append("  Unit:     $").append(String.format("%.2f", unitPrice)).append("\n");
        sb.append("  Total:    $").append(String.format("%.2f", total)).append("\n");
        //sb.append("  (In the real system, this would also update inventory & accounts.)\n");
        sb.append("--------------------------------------------------------------------------------------------------------------------------");

        showReceipt(sb.toString());

        /*
         * ========== REAL SYSTEM CALLS GO HERE (LATER) ==========
         *
         * // 1. Check stock
         * if (!inventorySys.validateStock(bottleType, quantity)) {
         *     showReceipt("Sale failed: not enough stock for " + bottleType);
         *     showReceipt("--------------------------------------------------");
         *     return;
         * }
         *
         * // 2. Process and post the sale in SaleSys
         * if (saleSys != null) {
         *     saleSys.processSale(empId, customer, bottleType, quantity, unitPrice);
         *     saleSys.postSale(empId, customer, bottleType, quantity, unitPrice);
         * }
         *
         * // 3. Reduce inventory
         * if (inventorySys != null) {
         *     inventorySys.updateStock(bottleType, -quantity);
         * }
         */
    }

    /**
     * UML: +showReceipt()
     * Simple generic receipt message (not used much in demo).
     */
    public void showReceipt() {
        showReceipt("Receipt details not available.");
    }

    /**
     * Overloaded helper that appends text to the receipt/output area.
     */
    public void showReceipt(String text) {
        appendLine(text);
    }

    // ================= Helpers =================

    private void appendLine(String text) {
        outputArea.append(text + "\n");
    }

    /**
     * Main method so you can run this class directly.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            POSGUI gui = new POSGUI();
            gui.displayScreen();   // UML-style
        });
    }
}
