package com.tuniway.dto;

public class UpdateUserRequest {
    private String username;
    private String email;
    private String profilePicture;
    
    // Default constructor
    public UpdateUserRequest() {
    }
    
    // Constructor with all fields
    public UpdateUserRequest(String username, String email, String profilePicture) {
        this.username = username;
        this.email = email;
        this.profilePicture = profilePicture;
    }
    
    // Getters
    public String getUsername() {
        return username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public String getProfilePicture() {
        return profilePicture;
    }
    
    // Setters
    public void setUsername(String username) {
        this.username = username;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
}
