package com.groupthree.sims;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

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
public class POSGUI extends JFrame
{
    private JTextField usernameField;
    private JTextField customerField;
    private JTextField bottleTypeField;
    private JTextField quantityField;

    private JTextArea outputArea;

    /**
     * Default constructor: builds the POS screen.
     */
    public POSGUI()
    {
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
        formPanel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        formPanel.add(usernameField);

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

    public void displayScreen()
    {
        setVisible(true);
    }

    /**
     * UML: +enterSale()
     * Handles the main sale entry: validation + (later) backend calls.
     */
    public void enterSale()
    {
        String username = usernameField.getText().trim();
        String customer = customerField.getText().trim();
        String bottleType = bottleTypeField.getText().trim();
        int quantity = Integer.parseInt(quantityField.getText().trim());

        Sale sale = new Sale();
        Item bottle = InventorySys.getItemByName(bottleType);
        sale.addItem(bottle, quantity);

        User user = SecuritySys.findUserByUsername(username);

        if (user == null)
        {
            display("User does not exist");
            return;
        }

        // Let SaleSys handle privilege + stock logic
        SaleResultStatus status = SaleSys.processSale(user, customer, new Date(), sale);

        switch (status)
        {
            case SUCCESS:
                display("Sale recorded successfully. Total cost: $" + sale.getTotalAmount());
                break;

            case NO_PRIVILEGE:
                display("User does not have required privileges");
                break;

            case OUT_OF_STOCK:
                display("Error: Item out of stock");
                break;

            case ERROR:
            default:
                display("Error: Sale processing failed");
        }
    }

    private void display(String text)
    {
        outputArea.append(text + "\n");
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() -> {
            POSGUI gui = new POSGUI();
            gui.displayScreen();
        });
    }
}
