package com.tuniway.model;

import com.tuniway.model.enums.RoleType;
import jakarta.persistence.*;
import java.util.List;

@Entity
@DiscriminatorValue("Guide")
public class Guide extends User {
    private String bio;
    private List<String> languages;
    private String availability;

    public Guide() {
        this.setRole(RoleType.GUIDE);
    }

    public String getBio() {
        return bio;
    }
    public void setBio(String bio) {
        this.bio = bio;
    }
    public List<String> getLanguages() {
        return languages;
    }
    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }
    public String getAvailability() {
        return availability;
    }
    public void setAvailability(String availability) {
        this.availability = availability;
    }
}