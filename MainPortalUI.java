import javax.swing.*;
import java.awt.*;

/**
 * MainPortalUI
 *  - First screen shown to ALL Sweetcraft employees.
 *  - Lets user choose which system they want to access:
 *      Engineer Employee  -> FactoryUI
 *      Accounting Employee -> POSGUI
 *      Manager Screen      -> AdminGUI
 *
 * UI ONLY — no business logic here.
 */
public class MainPortalUI extends JFrame {

    public MainPortalUI() {
        super("Sweetcraft - Employee Portal");
        buildUI();
    }

    private void buildUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 250);
        setLocationRelativeTo(null);

        // Background image
        BackgroundPanel bg = new BackgroundPanel("sweetcraftlogo.jpeg");
        bg.setLayout(new BorderLayout());
        setContentPane(bg);

        JLabel title = new JLabel("Select your role to continue", SwingConstants.CENTER);
        title.setFont(SweetcraftTheme.TITLE_FONT);
        title.setForeground(SweetcraftTheme.TITLE_BLUE);
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(title, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 60, 15, 60));
        buttonPanel.setOpaque(false);      // allow logo to show

        JButton engineerButton   = new JButton("Engineer Employee");
        JButton accountingButton = new JButton("Accounting Employee");
        JButton managerButton    = new JButton("Manager Screen");

        // light blue theme + hover
        SweetcraftTheme.stylePrimaryButton(engineerButton);
        SweetcraftTheme.stylePrimaryButton(accountingButton);
        SweetcraftTheme.stylePrimaryButton(managerButton);

        buttonPanel.add(engineerButton);
        buttonPanel.add(accountingButton);
        buttonPanel.add(managerButton);

        add(buttonPanel, BorderLayout.CENTER);

        JButton exitButton = new JButton("Exit");
        SweetcraftTheme.stylePrimaryButton(exitButton);

        JPanel bottom = new JPanel();
        bottom.setOpaque(false);           // allow logo to show
        bottom.add(exitButton);
        add(bottom, BorderLayout.SOUTH);

        exitButton.addActionListener(e -> System.exit(0));

        // ===== Button actions: open SECOND WINDOW (login) =====
        engineerButton.addActionListener(e -> {
            setVisible(false);
            new LoginUI(this, "Engineer Employee", "ENGINEER").setVisible(true);
        });

        accountingButton.addActionListener(e -> {
            setVisible(false);
            new LoginUI(this, "Accounting Employee", "ACCOUNTING").setVisible(true);
        });

        managerButton.addActionListener(e -> {
            setVisible(false);
            new LoginUI(this, "Manager Screen", "MANAGER").setVisible(true);
        });
    }

    /**
     * Entry point for the whole application.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainPortalUI ui = new MainPortalUI();
            ui.setVisible(true);
        });
    }
}
