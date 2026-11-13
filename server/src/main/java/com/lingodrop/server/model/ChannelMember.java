package com.lingodrop.server.model;

import java.time.OffsetDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity 
@Table(name = "channel_members")
public class ChannelMember {

    @EmbeddedId 
    @Getter 
    private ChannelMemberId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("channelId")
    @JoinColumn(name = "channel_id")
    @Getter 
    @Setter 
    private Channel channel;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    @Getter 
    @Setter 
    private User user;

    @CreationTimestamp 
    @Column(name = "joined_at", nullable = false, updatable = false)
    @Getter 
    private OffsetDateTime joinedAt;

    public ChannelMember() {
    }
    
    public ChannelMember(Channel channel, User user) {
        this.channel = channel;
        this.user = user;
        this.id = new ChannelMemberId(channel.getId(), user.getId());
    }
}
