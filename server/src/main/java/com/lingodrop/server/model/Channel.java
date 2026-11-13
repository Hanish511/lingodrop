package com.lingodrop.server.model;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "channels")
public class Channel {

    @Id 
    @GeneratedValue(strategy = GenerationType.UUID)
    @Getter 
    private UUID id;

    @Column(name = "name", nullable = false)
    @Getter 
    @Setter 
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    @Getter 
    @Setter 
    private User createdBy;

    @Column(name = "is_private", nullable = false)
    @Getter 
    @Setter 
    private boolean isPrivate = false;

    @CreationTimestamp 
    @Column(name = "created_at", nullable =false, updatable =false)
    @Getter 
    private OffsetDateTime createdAt;

    public Channel() {
    }

    public Channel(String name, User createdBy, boolean isPrivate) {
        this.name = name;
        this.createdBy = createdBy;
        this.isPrivate = isPrivate;
    }
}
