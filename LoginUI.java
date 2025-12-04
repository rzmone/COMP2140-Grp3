import javax.swing.*;
import java.awt.*;

/**
 * LoginUI
 *  - Second screen after selecting role in MainPortalUI.
 *  - Has TWO text boxes: Employee ID and Password.
 *
 * After basic UI validation, it opens:
 *   ENGINEER   -> FactoryUI
 *   ACCOUNTING -> POSGUI
 *   MANAGER    -> AdminGUI
 *
 * UI ONLY — real authentication/authorization must be done
 * in SecuritySys (Business Logic Layer) when that class is ready.
 */
public class LoginUI extends JFrame {

    private final MainPortalUI mainPortal;
    private final String roleCode;  // "ENGINEER", "ACCOUNTING", "MANAGER"

    private JTextField employeeIdField;
    private JPasswordField passwordField;

    public LoginUI(MainPortalUI portal, String roleLabel, String roleCode) {
        super("Login - " + roleLabel);
        this.mainPortal = portal;
        this.roleCode = roleCode;
        buildUI(roleLabel);
    }

    private void buildUI(String roleLabel) {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 220);
        setLocationRelativeTo(null);

        BackgroundPanel bg = new BackgroundPanel("sweetcraftlogo.jpeg");
        bg.setLayout(new BorderLayout());
        setContentPane(bg);

        JLabel title = new JLabel(roleLabel + " Login", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setForeground(SweetcraftTheme.TITLE_BLUE);
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        formPanel.setOpaque(false);   // let background show

        formPanel.add(new JLabel("Employee ID:"));
        employeeIdField = new JTextField();
        formPanel.add(employeeIdField);

        formPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        formPanel.add(passwordField);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false); // let background show

        JButton loginButton = new JButton("Login");
        JButton cancelButton = new JButton("Cancel");

        SweetcraftTheme.stylePrimaryButton(loginButton);
        SweetcraftTheme.stylePrimaryButton(cancelButton);

        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Actions
        loginButton.addActionListener(e -> attemptLogin());
        cancelButton.addActionListener(e -> dispose());
    }

    private void attemptLogin() {
        String empId    = employeeIdField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (empId.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Employee ID and Password are required.",
                    "Missing Information",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        switch (roleCode) {
            case "ENGINEER":
                FactoryUI factoryUI = new FactoryUI();
                factoryUI.setVisible(true);
                break;
            case "ACCOUNTING":
                POSGUI posgui = new POSGUI();
                posgui.displayScreen();
                break;
            case "MANAGER":
                AdminGUI adminGUI = new AdminGUI(empId, empId);
                adminGUI.setVisible(true);
                break;
            default:
                JOptionPane.showMessageDialog(
                        this,
                        "Unknown role: " + roleCode,
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
        }

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                if (mainPortal != null) {
                    mainPortal.setVisible(true);
                }
            }
        });
    }
}
