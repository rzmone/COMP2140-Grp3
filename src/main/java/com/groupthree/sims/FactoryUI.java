package com.groupthree.sims;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * FactoryUI
 *  - Graphical Factory UI for Sweetcraft.
 *  - Lets production staff enter batch data and see confirmations.
 *
 * Matches UML:
 *   +displayScreen()
 *   +enterData()
 *   +showResults()
 *
 * NOTE:
 *   All calls to ProductionSys and InventorySys are COMMENTED OUT.
 *   UNCOMMENT LINES LATER
 */
public class FactoryUI extends JFrame {

    private JTextField employeeIdField;
    private JTextField bottleTypeField;
    private JTextField quantityField;
    private JTextField machineIdField;

    private JTextArea outputArea;

    /*
     * ================== BACKEND HOOKS (for later) ==================
     *
     * When your group members implement the system classes, you can:
     *
     * // Fields:
     * private ProductionSys productionSys;
     * private InventorySys inventorySys;
     *
     * // Constructor for full integration:
     * public FactoryUI(ProductionSys ps, InventorySys inv) {
     *     this();
     *     this.productionSys = ps;
     *     this.inventorySys = inv;
     * }
     *
     * // Inside enterData():
     * if (productionSys != null) {
     *     productionSys.recordProduction(bottleType, quantity, machineId, empId);
     * }
     * if (inventorySys != null) {
     *     inventorySys.updateStock(bottleType, quantity);
     * }
     */

    /**
     * Default constructor – builds the GUI.
     * (No backend objects wired in yet.)
     */
    public FactoryUI() {
        super("Sweetcraft - Factory Records");
        buildUI();
    }

    private void buildUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 420);
        setLocationRelativeTo(null);  // centre
        setLayout(new BorderLayout());

        // ===== Top: input form (4 rows x 2 columns) =====
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        formPanel.add(new JLabel("Employee ID:"));
        employeeIdField = new JTextField();
        formPanel.add(employeeIdField);

        formPanel.add(new JLabel("Bottle type (e.g. 500ml, 1L):"));
        bottleTypeField = new JTextField();
        formPanel.add(bottleTypeField);

        formPanel.add(new JLabel("Quantity produced:"));
        quantityField = new JTextField();
        formPanel.add(quantityField);

        formPanel.add(new JLabel("Machine ID / name:"));
        machineIdField = new JTextField();
        formPanel.add(machineIdField);

        add(formPanel, BorderLayout.NORTH);

        // ===== Centre: output area =====
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(outputArea);
        add(scroll, BorderLayout.CENTER);

        // ===== Bottom: buttons =====
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton recordBatchButton = new JButton("Record Production");
        JButton clearButton = new JButton("Clear");
        JButton exitButton = new JButton("Exit");

        Color lightYellow = new Color(255, 255, 153);
        recordBatchButton.setBackground(lightYellow);
        clearButton.setBackground(lightYellow);
        exitButton.setBackground(lightYellow);

        recordBatchButton.setOpaque(true);
        clearButton.setOpaque(true);
        exitButton.setOpaque(true);

        buttonPanel.add(recordBatchButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(exitButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Use UML method names
        recordBatchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enterData();
            }
        });

        clearButton.addActionListener(e -> outputArea.setText(""));
        exitButton.addActionListener(e -> dispose());
    }

    // ================= UML METHODS =================

    /**
     * UML: +displayScreen()
     * Shows the Factory UI window.
     */
    public void displayScreen() {
        setVisible(true);
    }

    /**
     * UML: +enterData()
     * Reads the form, validates it, and (in the real system) would
     * send data to ProductionSys and InventorySys.
     * For now, it uses hard-coded demo behaviour.
     */
    public void enterData() {
        String empId      = employeeIdField.getText().trim();
        String bottleType = bottleTypeField.getText().trim();
        String qtyText    = quantityField.getText().trim();
        String machineId  = machineIdField.getText().trim();

        if (empId.isEmpty() || bottleType.isEmpty()
                || qtyText.isEmpty() || machineId.isEmpty()) {
            showResults("Please fill in ALL fields before recording production.");
            showResults("---------------------------------------------");
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(qtyText);
            if (quantity <= 0) {
                showResults("Quantity must be a positive integer.");
                showResults("---------------------------------------------");
                return;
            }
        } catch (NumberFormatException ex) {
            showResults("Quantity must be a valid integer.");
            showResults("---------------------------------------------");
            return;
        }

        // ========== HARD-CODED DEMO LOGIC ==========
        // This is just to show something sensible on screen.
        String sizeNote;
        if (quantity > 10000) {
            sizeNote = "Large batch – high volume production.";
        } else if (quantity > 1000) {
            sizeNote = "Medium batch.";
        } else {
            sizeNote = "Small batch.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("The following was recorded:" + "\n");
        sb.append("  Employee: ").append(empId).append("\n");
        sb.append("  Bottle:   ").append(bottleType).append("\n");
        sb.append("  Quantity: ").append(quantity).append("\n");
        sb.append("  Machine:  ").append(machineId).append("\n");
        //sb.append("  Note:     ").append(sizeNote).append("\n");
        //sb.append("  (In the real system, this would also update inventory and history.)\n");
        sb.append("---------------------------------------------");

        showResults(sb.toString());

        /*
         * ========== REAL SYSTEM CALLS GO HERE (LATER) ==========
         *
         * // 1. Record production in ProductionSys
         * if (productionSys != null) {
         *     productionSys.recordProduction(bottleType, quantity, machineId, empId);
         * }
         *
         * // 2. Update inventory in InventorySys
         * if (inventorySys != null) {
         *     inventorySys.updateStock(bottleType, quantity);
         * }
         */
    }

    /**
     * UML: +showResults()
     * Generic summary – currently just a placeholder.
     * (Usually you'd pull summary stats from the system layer.)
     */
    public void showResults() {
        showResults("Summary not implemented yet (real stats will come from system layer).");
    }

    /**
     * Overloaded helper: appends a message to the output area.
     */
    public void showResults(String message) {
        outputArea.append(message + "\n");
    }

    /**
     * Simple main so you can run FactoryUI directly.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FactoryUI ui = new FactoryUI();
            ui.displayScreen();
        });
    }
}
