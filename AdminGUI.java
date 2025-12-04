import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * AdminGUI
 *  - Used by managers/administrators.
 *  - UI only: manage users, view reports, view alerts.
 */
public class AdminGUI extends JFrame {

    private final String employeeId;
    private final String employeeName;

    public AdminGUI(String employeeId, String employeeName) {
        super("Sweetcraft - Admin / Manager");
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        buildUI();
    }

    private void buildUI() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);

        BackgroundPanel bg = new BackgroundPanel("sweetcraftlogo.jpeg");
        bg.setLayout(new BorderLayout());
        setContentPane(bg);

        JLabel title = new JLabel("Admin Dashboard - " + employeeName, SwingConstants.CENTER);
        title.setFont(SweetcraftTheme.TITLE_FONT);
        title.setForeground(SweetcraftTheme.TITLE_BLUE);
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(title, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setOpaque(false);

        tabs.addTab("Users", createUsersPanel());
        tabs.addTab("Reports", createReportsPanel());
        tabs.addTab("Alerts", createAlertsPanel());

        add(tabs, BorderLayout.CENTER);
    }

    private JPanel createUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        DefaultTableModel model = new DefaultTableModel(new Object[]{"User ID", "Name", "Role"}, 0);
        JTable table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);

        JPanel top = new JPanel();
        top.setOpaque(false);
        JButton addUserBtn = new JButton("Add User");
        JButton editUserBtn = new JButton("Edit User");
        JButton resetPwdBtn = new JButton("Reset Password");
        SweetcraftTheme.stylePrimaryButton(addUserBtn);
        SweetcraftTheme.stylePrimaryButton(editUserBtn);
        SweetcraftTheme.stylePrimaryButton(resetPwdBtn);

        top.add(addUserBtn);
        top.add(editUserBtn);
        top.add(resetPwdBtn);

        panel.add(top, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        // UI-only: add a dummy user when Add User clicked
        addUserBtn.addActionListener(e ->
                model.addRow(new Object[]{"EMP001", "Sample User", "Manager"}));

        return panel;
    }

    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JTextArea area = new JTextArea(
                "Reports area.\n\nGenerate inventory, production, and sales reports here."
        );
        area.setEditable(false);
        JScrollPane scroll = new JScrollPane(area);
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);

        JButton generateBtn = new JButton("Generate Dummy Report");
        SweetcraftTheme.stylePrimaryButton(generateBtn);

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.add(generateBtn);

        panel.add(top, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        generateBtn.addActionListener(e ->
                JOptionPane.showMessageDialog(this,
                        "Report generation is UI-only. Connect to HistorySys/InventorySys later."));

        return panel;
    }

    private JPanel createAlertsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"Time", "Type", "Message"}, 0);
        JTable table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);

        JButton refreshBtn = new JButton("Load Sample Alerts");
        SweetcraftTheme.stylePrimaryButton(refreshBtn);

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.add(refreshBtn);

        panel.add(top, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        refreshBtn.addActionListener(e -> {
            model.setRowCount(0);
            model.addRow(new Object[]{"10:15 AM", "Low Stock",
                    "Bottle X500 below threshold."});
            model.addRow(new Object[]{"2:40 PM", "Quality",
                    "Defect rate exceeded 10% on Line 3."});
        });

        return panel;
    }
}
