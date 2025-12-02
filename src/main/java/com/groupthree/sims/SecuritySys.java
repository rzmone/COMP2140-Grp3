package com.groupthree.sims;

public class SecuritySys {

    public static boolean authenticate(User user, String inputPassword) {
        System.out.println("Authenticating user " + user.getUsername() + "...");

        boolean success = user.getPassword().equals(inputPassword);

        if (success) {
            System.out.println("Authentication successful");
        } else {
            System.out.println("Authentication failed");
        }

        return success;
    }

    public static boolean authorize(User user, String requiredRole) {
        System.out.println("Authorizing user " + user.getUsername()
                           + " for role " + requiredRole + "...");

        boolean allowed = user.getRole().equalsIgnoreCase(requiredRole);

        if (allowed) {
            System.out.println("Authorization granted");
        } else {
            System.out.println("Authorization denied");
        }

        return allowed;
    }
}

class User {
    private String username;
    private String password;
    private String role;   // e.g. "ADMIN", "CASHIER", "MANAGER"

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }
}
