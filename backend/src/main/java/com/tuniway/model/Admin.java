package com.tuniway.model;

import com.tuniway.model.enums.RoleType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("Admin")
public class Admin extends User {
    public Admin() {
        this.setRole(RoleType.ADMIN);
    }
}