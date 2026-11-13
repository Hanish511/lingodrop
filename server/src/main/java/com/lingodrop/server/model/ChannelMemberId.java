package com.lingodrop.server.model;

import java.io.Serializable;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable  
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@EqualsAndHashCode 
public class ChannelMemberId implements Serializable {

    @Column(name = "channel_id")
    private UUID channelId;

    @Column(name = "user_id")
    private UUID userId;
}
