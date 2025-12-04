package com.groupthree.sims;

import java.time.LocalDateTime;

public class User {

    private long id;
    private String username;   // use this as EMP ID, e.g. "ADMIN001"
    private String password;   // plain text (simple version)
    private Role role;
    private String email;
    private boolean active;
    private LocalDateTime lastLoginAt;

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

    // --------- Getters & Setters ---------

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

    @Override
    public String toString() {
        return "ID: " + username +
                " | Role: " + role +
                " | Active: " + active;
    }
}

