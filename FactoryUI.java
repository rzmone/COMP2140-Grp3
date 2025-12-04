import javax.swing.*;
import java.awt.*;

/**
 * FactoryUI
 *  - Used by production staff (Engineer Employee).
 *  - UI only: records production & defects; hooks to ProductionSys later.
 */
public class FactoryUI extends JFrame {

    private JTextField batchIdField;
    private JTextField totalProducedField;
    private JTextField goodUnitsField;
    private JTextField defectiveUnitsField;
    private JTextArea  notesArea;

    public FactoryUI() {
        super("Sweetcraft - Factory Production");
        buildUI();
    }

    private void buildUI() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        BackgroundPanel bg = new BackgroundPanel("sweetcraftlogo.jpeg");
        bg.setLayout(new BorderLayout());
        setContentPane(bg);

        JLabel title = new JLabel("Record Production Batch", SwingConstants.CENTER);
        title.setFont(SweetcraftTheme.TITLE_FONT);
        title.setForeground(SweetcraftTheme.TITLE_BLUE);
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 8, 8));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));
        formPanel.setOpaque(false);

        formPanel.add(new JLabel("Batch ID:"));
        batchIdField = new JTextField();
        formPanel.add(batchIdField);

        formPanel.add(new JLabel("Total Bottles Produced:"));
        totalProducedField = new JTextField();
        formPanel.add(totalProducedField);

        formPanel.add(new JLabel("Good Bottles:"));
        goodUnitsField = new JTextField();
        formPanel.add(goodUnitsField);

        formPanel.add(new JLabel("Defective Bottles:"));
        defectiveUnitsField = new JTextField();
        formPanel.add(defectiveUnitsField);

        formPanel.add(new JLabel("Notes:"));
        notesArea = new JTextArea(3, 20);
        JScrollPane notesScroll = new JScrollPane(notesArea);
        formPanel.add(notesScroll);

        add(formPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);

        JButton saveButton = new JButton("Save Production");
        JButton clearButton = new JButton("Clear");
        JButton closeButton = new JButton("Close");

        SweetcraftTheme.stylePrimaryButton(saveButton);
        SweetcraftTheme.stylePrimaryButton(clearButton);
        SweetcraftTheme.stylePrimaryButton(closeButton);

        bottomPanel.add(saveButton);
        bottomPanel.add(clearButton);
        bottomPanel.add(closeButton);

        add(bottomPanel, BorderLayout.SOUTH);

        // UI-only behaviour
        clearButton.addActionListener(e -> {
            batchIdField.setText("");
            totalProducedField.setText("");
            goodUnitsField.setText("");
            defectiveUnitsField.setText("");
            notesArea.setText("");
        });

        closeButton.addActionListener(e -> dispose());

        saveButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                    this,
                    "Production entry saved (UI only – hook to ProductionSys later).",
                    "Info",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });
    }
}
