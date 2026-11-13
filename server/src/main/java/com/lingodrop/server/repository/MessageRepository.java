package com.lingodrop.server.repository;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import com.lingodrop.server.model.Message;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    Slice<Message> findByChannelId(UUID channelId, Pageable pageable);

}
