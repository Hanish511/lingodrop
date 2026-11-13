package com.lingodrop.server.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "message_translations", 
    uniqueConstraints = @UniqueConstraint(name = "uq_message_language", columnNames = {"message_id", "language_code"})
)
public class MessageTranslation {

    @Id 
    @GeneratedValue(strategy = GenerationType.UUID)
    @Getter 
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false)
    @Getter 
    @Setter 
    private Message message;

    @Column(name = "language_code", nullable = false, length = 10)
    @Getter 
    @Setter 
    private String languageCode;

    @Column(name = "translated_text", nullable = false)
    @Getter 
    @Setter 
    private String translatedText;

    public MessageTranslation(){
    }
    public MessageTranslation(String languageCode, String translatedText){
        this.languageCode = languageCode;
        this.translatedText = translatedText;
    }
}
