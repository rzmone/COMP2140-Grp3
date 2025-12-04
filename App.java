import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;

public class App
{
    // Store valid usernames and passwords
    private static final Map<String, String> users = new HashMap<>();
    
    static {
        // Initialize with some default users
        users.put("admin", "password123");
        users.put("user1", "user123");
        users.put("user2", "pass456");
    }
    
    public static void main( String[] args )
    {
        Scanner scanner = new Scanner(System.in);
        boolean loggedIn = false;
        
        System.out.println("========== Inventory System Login ==========");
        
        // Login attempts loop
        while (!loggedIn) {
            System.out.print("Enter username: ");
            String username = scanner.nextLine().trim();
            
            System.out.print("Enter password: ");
            String password = scanner.nextLine().trim();
            
            // Check if username and password match
            if (users.containsKey(username) && users.get(username).equals(password)) {
                System.out.println("\n✓ Login successful! Welcome, " + username + "!\n");
                loggedIn = true;
            } else {
                System.out.println("✗ Invalid username or password. Please try again.\n");
            }
        }
        
        // Once logged in, run the inventory application menu
        System.out.println("========== Launching Inventory Application ==========\n");
        
        // Call the Inventory main menu by running Inventory.main()
        Inventory.main(new String[]{});
        
        scanner.close();
    }
}
