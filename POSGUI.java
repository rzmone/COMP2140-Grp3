
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * POSGUI
 *  - Used by Accounting / Sales employees.
 *  - UI only: basic POS layout; hooks to SaleSys & InventorySys later.
 */
public class POSGUI extends JFrame {

    private JTextField productIdField;
    private JTextField qtyField;
    private JLabel totalLabel;
    private DefaultTableModel tableModel;

    public POSGUI() {
        super("Sweetcraft - POS");
        buildUI();
    }

    public void displayScreen() {
        setVisible(true);
    }

    private void buildUI() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 450);
        setLocationRelativeTo(null);

        BackgroundPanel bg = new BackgroundPanel("sweetcraftlogo.jpeg");
        bg.setLayout(new BorderLayout());
        setContentPane(bg);

        JLabel title = new JLabel("Point of Sale", SwingConstants.CENTER);
        title.setFont(SweetcraftTheme.TITLE_FONT);
        title.setForeground(SweetcraftTheme.TITLE_BLUE);
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(title, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel(new GridLayout(1, 4, 5, 5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 5, 20));
        inputPanel.setOpaque(false);

        inputPanel.add(new JLabel("Product ID:"));
        productIdField = new JTextField();
        inputPanel.add(productIdField);

        inputPanel.add(new JLabel("Quantity:"));
        qtyField = new JTextField();
        inputPanel.add(qtyField);

        add(inputPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[]{"Product ID", "Qty", "Price", "Subtotal"}, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);

        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setOpaque(false);

        JButton addButton = new JButton("Add Item");
        JButton removeButton = new JButton("Remove Selected");
        JButton completeButton = new JButton("Complete Sale");
        JButton cancelButton = new JButton("Cancel");

        SweetcraftTheme.stylePrimaryButton(addButton);
        SweetcraftTheme.stylePrimaryButton(removeButton);
        SweetcraftTheme.stylePrimaryButton(completeButton);
        SweetcraftTheme.stylePrimaryButton(cancelButton);

        buttonsPanel.add(addButton);
        buttonsPanel.add(removeButton);
        buttonsPanel.add(completeButton);
        buttonsPanel.add(cancelButton);

        bottomPanel.add(buttonsPanel, BorderLayout.CENTER);

        JPanel totalPanel = new JPanel();
        totalPanel.setOpaque(false);
        totalLabel = new JLabel("Total: $0.00");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 14));
        totalPanel.add(totalLabel);

        bottomPanel.add(totalPanel, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);

        // UI-only actions
        addButton.addActionListener(e -> {
            String pid = productIdField.getText().trim();
            String qtyText = qtyField.getText().trim();
            if (pid.isEmpty() || qtyText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter Product ID and Quantity.");
                return;
            }
            int qty;
            try {
                qty = Integer.parseInt(qtyText);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Quantity must be a whole number.");
                return;
            }

            // dummy price for UI demo
            double price = 10.0;
            double subtotal = price * qty;
            tableModel.addRow(new Object[]{pid, qty, price, subtotal});
            updateTotal();
            productIdField.setText("");
            qtyField.setText("");
        });

        removeButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                tableModel.removeRow(row);
                updateTotal();
            }
        });

        completeButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                    this,
                    "Sale completed (UI only – connect to SaleSys/InventorySys later).",
                    "Info",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });

        cancelButton.addActionListener(e -> dispose());
    }

    private void updateTotal() {
        double total = 0.0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            total += (Double) tableModel.getValueAt(i, 3);
        }
        totalLabel.setText(String.format("Total: $%.2f", total));
    }
}
