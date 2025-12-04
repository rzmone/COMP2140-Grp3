package com.groupthree.sims;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * SecuritySys handles all user authentication, authorization,
 * and user-account management for the SIMS system.
 *
 * Responsibilities:
 *  - Load and save user records from a persistent file
 *  - Create, update, and delete user accounts
 *  - Authenticate login attempts
 *  - Enforce role-based access control for UI components
 */
public class SecuritySys {

    /** In-memory user store populated from file at startup */
    private final List<User> users = new ArrayList<>();

    /** Auto-incrementing ID used for newly added users */
    private long nextUserId = 1;

    /** Data file used to persist all user accounts */
    private final File userFile;


    /**
     * Creates a SecuritySys using the default "users.txt" file.
     */
    public SecuritySys() {
        this(new File("users.txt"));
    }

    /**
     * Creates a SecuritySys using a custom file for loading/saving users.
     *
     * @param userFile file where user records are stored
     */
    public SecuritySys(File userFile) {
        this.userFile = userFile;
        loadUsersFromFile();
    }


    /* ===========================================================
       USER FILE HANDLING
       =========================================================== */

    /**
     * Loads all user entries from the persistent user file, if it exists.
     * Each line represents one user in the format:
     *
     *   id;username;password;role;email;active
     *
     * Malformed lines are skipped safely.
     */
    private void loadUsersFromFile() {
        if (!userFile.exists()) {
            return; // Start with an empty list on first run
        }

        try (BufferedReader br = new BufferedReader(new FileReader(userFile))) {
            String line;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(";", -1);
                if (parts.length < 6) continue;

                long id             = Long.parseLong(parts[0]);
                String username     = parts[1];
                String password     = parts[2];
                Role role           = Role.valueOf(parts[3]);
                String email        = parts[4];
                boolean active      = Boolean.parseBoolean(parts[5]);

                User u = new User(id, username, password, role, email, active);
                users.add(u);

                // Ensure nextUserId is always ahead of the highest ID
                if (id >= nextUserId) {
                    nextUserId = id + 1;
                }
            }

        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes all users currently in memory back to the persistent file.
     * This is called after every change to ensure local data remains synchronized.
     */
    private void saveUsersToFile() {
        try (PrintWriter out = new PrintWriter(new FileWriter(userFile))) {

            for (User u : users) {
                String email = (u.getEmail() == null) ? "" : u.getEmail();

                out.printf("%d;%s;%s;%s;%s;%b%n",
                        u.getId(),
                        u.getUsername(),
                        u.getPassword(),
                        u.getRole().name(),
                        email,
                        u.isActive());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /* ===========================================================
       USER MANAGEMENT
       =========================================================== */

    /**
     * Creates a new active user account and stores it permanently.
     *
     * @return the created User object
     */
    public User createUser(String username, String password, Role role, String email) {
        User user = new User(nextUserId++, username, password, role, email, true);
        users.add(user);
        saveUsersToFile();
        return user;
    }

    /**
     * Deletes a user based on their username (employee ID).
     *
     * @return true if a user was removed, false otherwise
     */
    public boolean deleteUserByUsername(String username) {
        boolean removed = users.removeIf(u -> u.getUsername().equalsIgnoreCase(username));
        if (removed) saveUsersToFile();
        return removed;
    }

    /**
     * Deletes a user based on their numeric system ID.
     */
    public boolean deleteUserById(long userId) {
        boolean removed = users.removeIf(u -> u.getId() == userId);
        if (removed) saveUsersToFile();
        return removed;
    }

    /**
     * Marks a user account as inactive (soft delete).
     */
    public boolean deactivateUser(long userId) {
        User user = findUserById(userId);
        if (user == null) return false;

        user.setActive(false);
        saveUsersToFile();
        return true;
    }

    /**
     * Updates a user's password.
     */
    public boolean changePassword(long userId, String newPassword) {
        User user = findUserById(userId);
        if (user == null) return false;

        user.setPassword(newPassword);
        saveUsersToFile();
        return true;
    }

    /**
     * Assigns a new role to the selected user.
     */
    public boolean updateUserRole(long userId, Role newRole) {
        User user = findUserById(userId);
        if (user == null) return false;

        user.setRole(newRole);
        saveUsersToFile();
        return true;
    }

    /**
     * @return all users stored in the system (including inactive users)
     */
    public List<User> listAllUsers() {
        return new ArrayList<>(users);
    }

    /**
     * @return only users marked as active
     */
    public List<User> listActiveUsers() {
        List<User> active = new ArrayList<>();
        for (User u : users) {
            if (u.isActive()) active.add(u);
        }
        return active;
    }


    /* ===========================================================
       USER LOOKUP HELPERS
       =========================================================== */

    /**
     * @return user with the given ID or null if not found
     */
    public User findUserById(long id) {
        for (User u : users) {
            if (u.getId() == id) return u;
        }
        return null;
    }

    /**
     * @return user with matching username (case-insensitive) or null if none exists
     */
    public User findUserByUsername(String username) {
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username)) return u;
        }
        return null;
    }


    /* ===========================================================
       AUTHENTICATION & AUTHORIZATION
       =========================================================== */

    /**
     * Verifies login credentials and updates last-login timestamp.
     *
     * @return the authenticated user, or null if login fails
     */
    public User authenticate(String username, String password) {
        User user = findUserByUsername(username);
        if (user == null) return null;
        if (!user.isActive()) return null;
        if (!user.getPassword().equals(password)) return null;

        user.setLastLoginAt(LocalDateTime.now());
        saveUsersToFile();
        return user;
    }

    /**
     * Checks whether a user's role satisfies a required access level.
     *
     * Rules:
     *  - ADMIN always has full access
     *  - MANAGER can access SALES + PRODUCTION actions
     *  - Other roles must match exactly
     */
    public boolean authorize(User user, Role requiredRole) {
        if (user == null || !user.isActive()) return false;

        Role userRole = user.getRole();

        if (userRole == Role.ADMIN) return true;
        if (userRole == requiredRole) return true;

        if (userRole == Role.MANAGER &&
                (requiredRole == Role.SALES || requiredRole == Role.PRODUCTION)) {
            return true;
        }

        return false;
    }


    /* ===========================================================
       ROLE-BASED UI ACCESS CONTROL
       =========================================================== */

    /** Only Admin + Manager can open the administrator interface. */
    public boolean canAccessAdminUI(User user) {
        return user != null &&
                user.isActive() &&
                (user.getRole() == Role.ADMIN || user.getRole() == Role.MANAGER);
    }

    /** POSUI can be accessed by Admin, Manager, and Sales. */
    public boolean canAccessPOSUI(User user) {
        if (user == null || !user.isActive()) return false;
        Role r = user.getRole();
        return r == Role.ADMIN || r == Role.MANAGER || r == Role.SALES;
    }

    /** FactoryUI can be accessed by Admin, Manager, and Production staff. */
    public boolean canAccessFactoryUI(User user) {
        if (user == null || !user.isActive()) return false;
        Role r = user.getRole();
        return r == Role.ADMIN || r == Role.MANAGER || r == Role.PRODUCTION;
    }

    /** Only Admin + Manager are allowed to manage users. */
    public boolean canManageUsers(User user) {
        if (user == null || !user.isActive()) return false;
        return user.getRole() == Role.ADMIN || user.getRole() == Role.MANAGER;
    }


    /* ===========================================================
       ADMIN LOGIN HELPER
       =========================================================== */

    /**
     * Used during AdminGUI login.
     *
     * @return true only if the credentials are valid AND the user
     *         has admin-level access rights.
     */
    public boolean validateAdmin(String empId, String password) {
        User user = authenticate(empId, password);
        return canAccessAdminUI(user);
    }

    /** Convenience method for UI list display. */
    public List<User> getAllUsers() {
        return listAllUsers();
    }
}
