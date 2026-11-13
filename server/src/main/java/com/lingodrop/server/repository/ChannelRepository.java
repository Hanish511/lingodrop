package com.lingodrop.server.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lingodrop.server.model.Channel;

public interface ChannelRepository extends JpaRepository<Channel, UUID> {

    List<Channel> findByIsPrivate(boolean isPrivate);
    
}
