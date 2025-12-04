import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Shared colors / fonts / button styling for all Sweetcraft screens.
 */
public class SweetcraftTheme {

    // Colors
    public static final Color WINDOW_BG        = new Color(0xF7F9FC);
    public static final Color TITLE_BLUE       = new Color(0x003F87);
    public static final Color BUTTON_LIGHT_BLUE= new Color(0xD6E9FF);  // normal
    public static final Color BUTTON_HOVER     = new Color(0xBFD8FF);  // hover
    public static final Color BUTTON_BORDER    = new Color(0x2F6FB7);

    // Fonts
    public static final Font TITLE_FONT  = new Font("Arial", Font.BOLD, 20);
    public static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 14);

    /**
     * Apply Sweetcraft light-blue style + hover to a button.
     */
    public static void stylePrimaryButton(JButton btn) {
        btn.setBackground(BUTTON_LIGHT_BLUE);
        btn.setForeground(Color.BLACK);
        btn.setFont(BUTTON_FONT);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(BUTTON_BORDER, 1));
        btn.setOpaque(true);

        // simple hover effect
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(BUTTON_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(BUTTON_LIGHT_BLUE);
            }
        });
    }
}
