package com.tuniway.model;

import com.tuniway.model.enums.RoleType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("Client")
public class Client extends User {
    public Client() {
        this.setRole(RoleType.CLIENT);
    }
}