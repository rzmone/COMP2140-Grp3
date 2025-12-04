package com.groupthree.sims;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SecuritySys {

    private final List<User> users = new ArrayList<>();
    private long nextUserId = 1;

    private final File userFile;

    // Use default file "users.txt" in the working directory
    public SecuritySys() {
        this(new File("users.txt"));
    }

    // Or you can pass a custom file from outside
    public SecuritySys(File userFile) {
        this.userFile = userFile;
        loadUsersFromFile();
    }

    // -------------- FILE LOAD / SAVE --------------

    private void loadUsersFromFile() {
        if (!userFile.exists()) {
            return; // no file yet => start empty
        }

        try (BufferedReader br = new BufferedReader(new FileReader(userFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                // id;username;password;role;email;active
                String[] parts = line.split(";", -1);
                if (parts.length < 6) continue; // skip bad lines

                long id         = Long.parseLong(parts[0]);
                String username = parts[1];
                String password = parts[2];
                Role role       = Role.valueOf(parts[3]);
                String email    = parts[4];
                boolean active  = Boolean.parseBoolean(parts[5]);

                User u = new User(id, username, password, role, email, active);
                users.add(u);

                if (id >= nextUserId) {
                    nextUserId = id + 1;
                }
            }
        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private void saveUsersToFile() {
        try (PrintWriter out = new PrintWriter(new FileWriter(userFile))) {
            for (User u : users) {
                // id;username;password;role;email;active
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

    // -------------- USER MANAGEMENT --------------

    /** Add/create a new user and immediately save to file. */
    public User createUser(String username,
                           String password,
                           Role role,
                           String email) {
        User user = new User(nextUserId++, username, password, role, email, true);
        users.add(user);
        saveUsersToFile();
        return user;
    }

    /** Delete a user by username (EMP ID). */
    public boolean deleteUserByUsername(String username) {
        boolean removed = users.removeIf(u ->
                u.getUsername().equalsIgnoreCase(username));
        if (removed) {
            saveUsersToFile();
        }
        return removed;
    }

    /** Delete a user by internal numeric ID. */
    public boolean deleteUserById(long userId) {
        boolean removed = users.removeIf(u -> u.getId() == userId);
        if (removed) {
            saveUsersToFile();
        }
        return removed;
    }

    public boolean deactivateUser(long userId) {
        User user = findUserById(userId);
        if (user == null) return false;
        user.setActive(false);
        saveUsersToFile();
        return true;
    }

    public boolean changePassword(long userId, String newPassword) {
        User user = findUserById(userId);
        if (user == null) return false;
        user.setPassword(newPassword);
        saveUsersToFile();
        return true;
    }

    public boolean updateUserRole(long userId, Role newRole) {
        User user = findUserById(userId);
        if (user == null) return false;
        user.setRole(newRole);
        saveUsersToFile();
        return true;
    }

    public List<User> listAllUsers() {
        return new ArrayList<>(users);
    }

    public List<User> listActiveUsers() {
        List<User> active = new ArrayList<>();
        for (User u : users) {
            if (u.isActive()) active.add(u);
        }
        return active;
    }

    public User findUserById(long id) {
        for (User u : users) {
            if (u.getId() == id) return u;
        }
        return null;
    }

    public User findUserByUsername(String username) {
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username)) return u;
        }
        return null;
    }

    // -------------- AUTH / AUTHZ --------------

    public User authenticate(String username, String password) {
        User user = findUserByUsername(username);
        if (user == null) return null;
        if (!user.isActive()) return null;
        if (!user.getPassword().equals(password)) return null;

        user.setLastLoginAt(LocalDateTime.now());
        saveUsersToFile(); // persist updated lastLoginAt
        return user;
    }

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

    // -------------- ROLE-BASED UI / ACTION ACCESS --------------

    // Only ADMIN + MANAGER can access AdminGUI
    public boolean canAccessAdminUI(User user) {
        if (user == null || !user.isActive()) return false;
        return user.getRole() == Role.ADMIN || user.getRole() == Role.MANAGER;
    }

    // ADMIN + MANAGER + SALES can access POSUI
    public boolean canAccessPOSUI(User user) {
        if (user == null || !user.isActive()) return false;
        Role r = user.getRole();
        return r == Role.ADMIN || r == Role.MANAGER || r == Role.SALES;
    }

    // ADMIN + MANAGER + PRODUCTION can access FactoryUI
    public boolean canAccessFactoryUI(User user) {
        if (user == null || !user.isActive()) return false;
        Role r = user.getRole();
        return r == Role.ADMIN || r == Role.MANAGER || r == Role.PRODUCTION;
    }

    // Only ADMIN + MANAGER can manage users
    public boolean canManageUsers(User user) {
        if (user == null || !user.isActive()) return false;
        return user.getRole() == Role.ADMIN || user.getRole() == Role.MANAGER;
    }

    // -------------- USED BY AdminGUI LOGIN --------------

    public boolean validateAdmin(String empId, String password) {
        User user = authenticate(empId, password);
        return canAccessAdminUI(user);
    }

    public List<User> getAllUsers() {
        return listAllUsers();
    }
}
