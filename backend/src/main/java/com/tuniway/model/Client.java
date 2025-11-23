package com.tuniway.model;

import com.tuniway.model.enums.RoleType;
import jakarta.persistence.*;

@Entity
@Table(name="client")
public class Client extends Utilisateur {

    public Client() {
        this.setRole(RoleType.CLIENT);
    }
}