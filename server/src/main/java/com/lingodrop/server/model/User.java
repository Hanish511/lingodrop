package com.lingodrop.server.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Getter 
    private UUID id;

    @Column(name = "username" ,nullable = false, unique = true)
    @Getter
    @Setter 
    private String username;

    @Column(name = "password_hash" ,nullable = false)
    @Getter
    @Setter 
    private String passwordHash;

    @Column(name = "native_language" ,nullable = false, length = 10)
    @Getter
    @Setter 
    private String nativeLanguage;

    @CreationTimestamp 
    @Column(name = "created_at" ,nullable = false, updatable = false) 
    @Getter
    private OffsetDateTime createdAt;


    public User() {
    }
    
    public User(String username, String passwordHash, String nativeLanguage) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.nativeLanguage = nativeLanguage;
    }

}
