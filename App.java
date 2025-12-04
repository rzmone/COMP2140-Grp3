
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("========== Inventory System Login ==========");

        // Use SecuritySys for authentication
        SecuritySys security = new SecuritySys();
        boolean loggedIn = false;
        while (!loggedIn) {
            System.out.print("Enter username: ");
            String username = scanner.nextLine().trim();
            System.out.print("Enter password: ");
            String password = scanner.nextLine().trim();

            // Authenticate using SecuritySys
            var user = security.authenticate(username, password);
            if (user != null) {
                System.out.println("\n✓ Login successful! Welcome, " + username + "!\n");
                loggedIn = true;
            } else {
                System.out.println("✗ Invalid username or password. Please try again.\n");
            }
        }

        // Once logged in, run the inventory application menu
        System.out.println("========== Launching Inventory Application ==========\n");
        Inventory.main(new String[]{});
        scanner.close();
    }
}
