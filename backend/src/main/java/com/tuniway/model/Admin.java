package com.tuniway.model;

import com.tuniway.model.enums.RoleType;
import jakarta.persistence.*;

@Entity
@Table(name="admin")
public class Admin extends Utilisateur {

    public Admin() {
        this.setRole(RoleType.ADMIN);
    }
}