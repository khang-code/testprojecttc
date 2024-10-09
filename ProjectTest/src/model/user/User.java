package model.user;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;

public class User {
    protected String username;
    protected String password;
    protected String phoneNumber;
    protected String fullName;
    protected String email;
    protected Set<Role> roles = new HashSet<>();
    protected boolean isApproved;

    public User(String username, String password, String phoneNumber, String fullName, String email) {
        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.fullName = fullName;
        this.email = email;
        this.isApproved = false;
    }

    // Getters and setters

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Corrected getRoles method
    public Set<Role> getRoles() {
        return roles; // Returning Set<Role>
    }

    // Optional: If you need to return roles as a List<Role>
    public List<Role> getRolesAsList() {
        return new ArrayList<>(roles); // Convert Set to List and return
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public boolean hasRole(Role role) {
        return roles.contains(role);
    }

    public void addRole(Role role) {
        roles.add(role);
    }

    public void changePassword(String newPassword) {
        this.password = newPassword;
    }

    @Override
    public String toString() {
        return "username='" + getUsername() + '\'' +
                ", full name:'" + getFullName() + '\'' +
                ", phone:'" + getPhoneNumber() + '\'' +
                ", email:'" + getEmail() + '\'' +
                ", Role:'" + roles + '\''; // Use roles directly
    }
}
