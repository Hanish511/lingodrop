package com.lingodrop.server.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lingodrop.server.model.ChannelMember;
import com.lingodrop.server.model.ChannelMemberId;

public interface ChannelMemberRepository extends JpaRepository<ChannelMember, ChannelMemberId>{

    List<ChannelMember> findByIdChannelId(UUID channelId);

}
