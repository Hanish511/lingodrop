package com.lingodrop.server.model;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity 
@Table(name = "messages")
public class Message {

    @Id 
    @GeneratedValue(strategy = GenerationType.UUID)
    @Getter 
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = false)
    @Getter 
    @Setter 
    private Channel channel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    @Getter 
    @Setter 
    private User sender;

    @Column(name = "original_text", nullable = false)
    @Getter 
    @Setter 
    private String originalText;

    @Column(name = "source_language", nullable = false, length = 10)
    @Getter 
    @Setter 
    private String sourceLanguage;

    @CreationTimestamp
    @Getter 
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter 
    private List<MessageTranslation> translations = new ArrayList<>();

    public void addTranslation(MessageTranslation translation){
        translations.add(translation);
        translation.setMessage(this);
    }

    public Message(){}

    public Message(Channel channel, User sender, String originalText, String sourceLanguage){
        this.channel = channel;
        this.sender = sender;
        this.originalText = originalText;
        this.sourceLanguage = sourceLanguage;
    }

}
