package com.tuniway.model;

import com.tuniway.model.enums.RoleType;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="user",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username"),
                @UniqueConstraint(columnNames = "email")
        })
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 20)
    private String username;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank
    @Size(max = 120)
    private String password;

    @Enumerated(EnumType.STRING)
    private RoleType role;

    // UPDATED: Add columnDefinition to handle large base64 images
    @Column(name = "profile_picture", columnDefinition = "LONGTEXT")
    @Lob  // Large Object annotation for JPA
    private String profilePicture;



    // Add this after the profilePicture field (around line 45)

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_favorite_places",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "place_id")
    )
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Set<Place> favoritePlaces = new HashSet<>();

    public Set<Place> getFavoritePlaces() {
        return favoritePlaces;
    }

    public void setFavoritePlaces(Set<Place> favoritePlaces) {
        this.favoritePlaces = favoritePlaces;
    }



}