package model;

import enumtypes.Role;

public class User {
    private int id;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private Role role;
    private boolean approved;
    private boolean active;

    public User() {
        this.role = Role.STUDENT;
        this.approved = false;
        this.active = false;
    }

    public User(int id, String username, String password, String fullName, String email, Role role,
                boolean approved, boolean active) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.approved = approved;
        this.active = active;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public boolean isApproved() { return approved; }
    public void setApproved(boolean approved) { this.approved = approved; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
