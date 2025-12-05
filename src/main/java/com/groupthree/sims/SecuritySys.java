package com.groupthree.sims;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SecuritySys handles all user authentication, authorization,
 * and user-account management for the SIMS system.
 *
 * Now backed by the database via the {@link Database} utility class.
 *
 * Responsibilities:
 *  - Create, update, and delete user accounts in the database
 *  - Authenticate login attempts
 *  - Enforce role-based access control for UI components
 */
public class SecuritySys {

    /**
     * Creates a SecuritySys using the database as the backing store.
     */
    public SecuritySys() {
        // No file-based storage anymore; everything is in the DB
    }

    /* ===========================================================
       INTERNAL HELPERS (DB MAPPING)
       =========================================================== */

    private static String escapeSql(String value) {
        return value == null ? null : value.replace("'", "''");
    }

    /**
     * Maps a generic DB row (column name -> value) to a User object.
     */
    private static User mapRowToUser(Map<String, Object> row)
    {
        if (row == null || row.isEmpty()) return null;

        int id = ((Number) row.get("id")).intValue();
        String username = (String) row.get("username");
        String password = (String) row.get("password");
        String roleStr = (String) row.get("role");
        Role role = Role.valueOf(roleStr);

        String email = (String) row.get("email");

        Object activeObj = row.get("active");
        boolean active = false;
        if (activeObj instanceof Boolean) {
            active = (Boolean) activeObj;
        } else if (activeObj instanceof Number) {
            active = ((Number) activeObj).intValue() != 0;
        } else if (activeObj instanceof String) {
            active = Boolean.parseBoolean((String) activeObj);
        }

        User user = new User(id, username, password, role, email, active);

        Object lastLoginObj = row.get("last_login_at");
        if (lastLoginObj != null) {
            LocalDateTime lastLoginAt = null;

            if (lastLoginObj instanceof Timestamp) {
                lastLoginAt = ((Timestamp) lastLoginObj).toLocalDateTime();
            } else if (lastLoginObj instanceof LocalDateTime) {
                lastLoginAt = (LocalDateTime) lastLoginObj;
            } else if (lastLoginObj instanceof String) {
                // Fallback if driver returns a String
                lastLoginAt = LocalDateTime.parse((String) lastLoginObj);
            }

            if (lastLoginAt != null) {
                user.setLastLoginAt(lastLoginAt);
            }
        }

        return user;
    }

    /**
     * Loads a single user by username from the database.
     */
    public static User findUserByUsername(String username) {
        if (username == null || username.isEmpty()) return null;

        String escaped = escapeSql(username);
        String sql = "SELECT * FROM users WHERE username = '" + escaped + "' LIMIT 1";

        List<Map<String, Object>> rows = Database.select(sql);
        if (rows.isEmpty()) return null;

        return mapRowToUser(rows.get(0));
    }

    /**
     * Loads a single user by ID from the database.
     */
    public static User findUserById(int id) {
        String sql = "SELECT * FROM users WHERE id = " + id + " LIMIT 1";

        List<Map<String, Object>> rows = Database.select(sql);
        if (rows.isEmpty()) return null;

        return mapRowToUser(rows.get(0));
    }

    /**
     * Updates the last_login_at column in the database.
     */
    private static void updateLastLoginInDatabase(User user) {
        if (user == null || user.getId() <= 0) return;

        Map<String, Object> values = new HashMap<>();
        // Convert to Timestamp explicitly for compatibility
        values.put("last_login_at", Timestamp.valueOf(user.getLastLoginAt()));

        String where = "id = " + user.getId();
        Database.update("users", values, where);
    }


    /* ===========================================================
       USER MANAGEMENT
       =========================================================== */

    /**
     * Creates a new active user account and stores it in the database.
     *
     * @return the created User object (loaded back from DB), or null if insert failed
     */
    public static User createUser(String username, String password, Role role, String email) {
        Map<String, Object> values = new HashMap<>();
        values.put("username", username);
        values.put("password", password);
        values.put("role", role.name());
        values.put("email", email);
        values.put("active", true);

        int rows = Database.insert("users", values);
        if (rows <= 0) {
            System.err.println("Failed to insert user into database.");
            return null;
        }

        // Reload the user from the database (assumes username is unique)
        return findUserByUsername(username);
    }

    /**
     * Deletes a user based on their username (employee ID).
     *
     * @return true if a user was removed, false otherwise
     */
    public static boolean deleteUserByUsername(String username) {
        String escaped = escapeSql(username);
        int rows = Database.delete("users", "username = '" + escaped + "'");
        return rows > 0;
    }

    /**
     * Deletes a user based on their numeric system ID.
     */
    public static boolean deleteUserById(int userId) {
        int rows = Database.delete("users", "id = " + userId);
        return rows > 0;
    }

    /**
     * Marks a user account as inactive (soft delete).
     */
    public static boolean deactivateUser(int userId) {
        Map<String, Object> values = new HashMap<>();
        values.put("active", false);

        int rows = Database.update("users", values, "id = " + userId);
        return rows > 0;
    }

    /**
     * Updates a user's password.
     */
    public static boolean changePassword(int userId, String newPassword) {
        Map<String, Object> values = new HashMap<>();
        values.put("password", newPassword);

        int rows = Database.update("users", values, "id = " + userId);
        return rows > 0;
    }

    /**
     * Assigns a new role to the selected user.
     */
    public static boolean updateUserRole(int userId, Role newRole) {
        Map<String, Object> values = new HashMap<>();
        values.put("role", newRole.name());

        int rows = Database.update("users", values, "id = " + userId);
        return rows > 0;
    }

    /**
     * @return all users stored in the system (including inactive users)
     */
    public static List<User> listAllUsers() {
        List<Map<String, Object>> rows = Database.selectAll("users");
        List<User> users = new ArrayList<>();

        for (Map<String, Object> row : rows) {
            User u = mapRowToUser(row);
            if (u != null) users.add(u);
        }

        return users;
    }

    /**
     * @return only users marked as active
     */
    public static List<User> listActiveUsers() {
        String sql = "SELECT * FROM users WHERE active = 1";
        List<Map<String, Object>> rows = Database.select(sql);
        List<User> users = new ArrayList<>();

        for (Map<String, Object> row : rows) {
            User u = mapRowToUser(row);
            if (u != null) users.add(u);
        }

        return users;
    }


    /* ===========================================================
       AUTHENTICATION & AUTHORIZATION
       =========================================================== */

    /**
     * Verifies login credentials and updates last-login timestamp in DB.
     *
     * @return the authenticated user, or null if login fails
     */
    public static User authenticate(String username, String password) {
        User user = findUserByUsername(username);
        if (user == null) return null;
        if (!user.isActive()) return null;
        if (!user.getPassword().equals(password)) return null;

        user.setLastLoginAt(LocalDateTime.now());
        updateLastLoginInDatabase(user);
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
    public static boolean authorize(User user, Role requiredRole) {
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
    public static boolean canAccessAdminUI(User user) {
        return user != null &&
                user.isActive() &&
                (user.getRole() == Role.ADMIN || user.getRole() == Role.MANAGER);
    }

    /** POSUI can be accessed by Admin, Manager, and Sales. */
    public static boolean canAccessPOSUI(User user) {
        if (user == null || !user.isActive()) return false;
        Role r = user.getRole();
        return r == Role.ADMIN || r == Role.MANAGER || r == Role.SALES;
    }

    /** FactoryUI can be accessed by Admin, Manager, and Production staff. */
    public static boolean canAccessFactoryUI(User user) {
        if (user == null || !user.isActive()) return false;
        Role r = user.getRole();
        return r == Role.ADMIN || r == Role.MANAGER || r == Role.PRODUCTION;
    }

    /** Only Admin + Manager are allowed to manage users. */
    public static boolean canManageUsers(User user) {
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
    public static boolean validateAdmin(String empId, String password) {
        User user = authenticate(empId, password);
        return canAccessAdminUI(user);
    }

    /** Convenience method for UI list display. */
    public static List<User> getAllUsers() {
        return listAllUsers();
    }
}
