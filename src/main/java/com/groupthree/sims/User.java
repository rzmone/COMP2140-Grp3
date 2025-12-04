package com.groupthree.sims;

import java.time.LocalDateTime;

/**
 * Represents a system user account within SIMS.
 *
 * A User contains:
 *  - A unique internal ID (numeric)
 *  - A username used as an Employee ID (e.g., "ADMIN001")
 *  - A password (plain-text in this simplified version)
 *  - A role defining system permissions (ADMIN, MANAGER, SALES, etc.)
 *  - Contact information
 *  - Account status (active/inactive)
 *  - Timestamp of the last successful login
 *
 * This class acts as a data model and is primarily managed through SecuritySys.
 */
public class User {

    /** Unique system-generated numeric identifier */
    private long id;

    /** Employee ID / login name used for authentication */
    private String username;

    /** User password (plain text — recommended to hash in real systems) */
    private String password;

    /** User role determining access rights */
    private Role role;

    /** Optional email address for contact or password reset */
    private String email;

    /** Indicates whether the account is active and permitted to log in */
    private boolean active;

    /** Timestamp of the user's last successful login */
    private LocalDateTime lastLoginAt;


    /**
     * Constructs a new user account with all required information.
     *
     * @param id        unique internal identifier
     * @param username  employee ID used for login
     * @param password  account password
     * @param role      assigned user role
     * @param email     contact email
     * @param active    whether the account is enabled
     */
    public User(long id,
                String username,
                String password,
                Role role,
                String email,
                boolean active) {

        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.email = email;
        this.active = active;
    }


    /* ===========================================================
       Getters and Setters
       =========================================================== */

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }


    /**
     * Provides a simplified string representation suitable for logs or UI lists.
     *
     * @return formatted summary of the user
     */
    @Override
    public String toString() {
        return "ID: " + username +
                " | Role: " + role +
                " | Active: " + active;
    }
}
