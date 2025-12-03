import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * AdminGUI
 *  - Admin console for Sweetcraft.
 *  - Admins can view security alerts and system history logs.
 *
 * Uses hard-coded demo data.
 * Hooks to SecuritySys / HistorySys / AlertSys are commented out.
 */
public class AdminGUI extends JFrame {

    private String employeeId;
    private String managerName;

    private JTextArea outputArea;

    /*
     * ================== BACKEND HOOKS (for later) ==================
     *
     * // Fields:
     * private SecuritySys securitySys;
     * private HistorySys historySys;
     * private AlertSys alertSys;
     *
     * // Constructor for full integration:
     * public AdminGUI(String empId, String name,
     *                 SecuritySys sec, HistorySys hist, AlertSys alert) {
     *     this(empId, name);
     *     this.securitySys = sec;
     *     this.historySys  = hist;
     *     this.alertSys    = alert;
     * }
     */

    public AdminGUI(String empId, String managerName) {
        super("Sweetcraft Admin Console");

        this.employeeId = empId;
        this.managerName = managerName;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(650, 450);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ===== Top: admin info =====
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        JLabel infoLabel = new JLabel(
                "Logged in as: " + managerName + "  (ID: " + employeeId + ")"
        );
        infoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        infoPanel.add(infoLabel);

        add(infoPanel, BorderLayout.NORTH);

        // ===== Center: output area =====
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(outputArea);
        add(scroll, BorderLayout.CENTER);

        // ===== Bottom: buttons =====
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton viewAlertsButton  = new JButton("View Security Alerts");
        JButton viewHistoryButton = new JButton("View History Logs");
        JButton manageUsersButton = new JButton("Manage Users");
        JButton clearButton       = new JButton("Clear");
        JButton exitButton        = new JButton("Exit");

        Color lightYellow = new Color(255, 255, 153);
        viewAlertsButton.setBackground(lightYellow);
        viewHistoryButton.setBackground(lightYellow);
        manageUsersButton.setBackground(lightYellow);
        clearButton.setBackground(lightYellow);
        exitButton.setBackground(lightYellow);

        viewAlertsButton.setOpaque(true);
        viewHistoryButton.setOpaque(true);
        manageUsersButton.setOpaque(true);
        clearButton.setOpaque(true);
        exitButton.setOpaque(true);

        buttonPanel.add(viewAlertsButton);
        buttonPanel.add(viewHistoryButton);
        buttonPanel.add(manageUsersButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(exitButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Button actions
        viewAlertsButton.addActionListener(e -> showAlerts());
        viewHistoryButton.addActionListener(e -> showHistory());
        manageUsersButton.addActionListener(e -> manageUsers());
        clearButton.addActionListener(e -> outputArea.setText(""));
        exitButton.addActionListener(e -> dispose());
    }

    // =============== DEMO ACTIONS (with hooks commented) ===============

    /**
     * Shows demo security alerts.
     * Later this will call SecuritySys / AlertSys.
     */
    public void showAlerts() {
        appendLine("=== Security Alerts ===");

        /*
         * LATER (real system):
         *
         * if (securitySys != null) {
         *     for (String alert : securitySys.getActiveAlerts()) {
         *         appendLine(alert);
         *     }
         * }
         */

        appendLine("[HIGH]  Multiple failed login attempts on admin account.");
        appendLine("[MEDIUM] POS terminal #3 accessed after hours.");
        appendLine("[LOW]   Password for 'factory01' expires in 3 days.");
        appendLine("--------------------------------------------------");
    }

    /**
     * Shows demo history logs.
     * Later this will call HistorySys.
     */
    public void showHistory() {
        appendLine("=== System History ===");

        /*
         * LATER (real system):
         *
         * if (historySys != null) {
         *     for (String line : historySys.getRecentEvents()) {
         *         appendLine(line);
         *     }
         * }
         */

        appendLine("2025-12-02 09:15  Batch #1245 recorded by EMP003.");
        appendLine("2025-12-02 09:45  Sale #657 total $12,500.00 by ACCT101.");
        appendLine("2025-12-02 10:05  Inventory adjustment: -50 damaged units (500ml).");
        appendLine("2025-12-02 10:30  New user 'factory07' added by ADMIN001.");
        appendLine("--------------------------------------------------");
    }

    /**
     * Demo user management view.
     * Later this could call SecuritySys / User repository.
     */
    public void manageUsers() {
        appendLine("=== User Management (DEMO) ===");

        /*
         * LATER (real system):
         *
         * if (securitySys != null) {
         *     for (User u : securitySys.getAllUsers()) {
         *         appendLine(u.toString());
         *     }
         * }
         */

        appendLine("ID: ADMIN001  | Name: Shequan McCalla  | Role: ADMIN");
        appendLine("ID: ACCT101   | Name: Jane Brown       | Role: ACCOUNTS");
        appendLine("ID: FACT001   | Name: John Smith       | Role: FACTORY");
        appendLine("--------------------------------------------------");
    }

    private void appendLine(String text) {
        outputArea.append(text + "\n");
    }

    // =============== LOGIN + MAIN ===============

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            // One dialog with ID, password, manager name
            JTextField idField = new JTextField();
            JPasswordField passwordField = new JPasswordField();
            JTextField nameField = new JTextField();

            JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
            panel.add(new JLabel("User ID:"));
            panel.add(idField);
            panel.add(new JLabel("User password:"));
            panel.add(passwordField);
            panel.add(new JLabel("Manager name:"));
            panel.add(nameField);

            int result = JOptionPane.showConfirmDialog(
                    null,
                    panel,
                    "Admin Login",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (result != JOptionPane.OK_OPTION) {
                JOptionPane.showMessageDialog(
                        null,
                        "Login cancelled. Access denied.",
                        "Access Denied",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            String empId       = idField.getText().trim();
            String password    = new String(passwordField.getPassword()).trim();
            String managerName = nameField.getText().trim();

            if (empId.isEmpty() || password.isEmpty() || managerName.isEmpty()) {
                JOptionPane.showMessageDialog(
                        null,
                        "All fields are required. Access denied.",
                        "Access Denied",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            // Temporary hard-coded credentials
            boolean allowed =
                    (empId.equals("ADMIN001") && password.equals("admin123")) ||
                    (empId.equals("MGR001")   && password.equals("mgr123"));

            /*
             * LATER (real system):
             *
             * boolean allowed = securitySys.validateAdmin(empId, password, managerName);
             */

            if (!allowed) {
                JOptionPane.showMessageDialog(
                        null,
                        "Invalid credentials. You are not authorized to access the Admin system.",
                        "Access Denied",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            AdminGUI gui = new AdminGUI(empId, managerName);
            gui.setVisible(true);
        });
    }
}
