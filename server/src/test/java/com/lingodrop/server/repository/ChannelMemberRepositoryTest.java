package com.lingodrop.server.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import com.lingodrop.server.model.Channel;
import com.lingodrop.server.model.ChannelMember;
import com.lingodrop.server.model.ChannelMemberId;
import com.lingodrop.server.model.User;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

@DataJpaTest 
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ChannelMemberRepositoryTest {

    @Autowired 
    private ChannelMemberRepository channelMemberRepository;

    @Autowired 
    private ChannelRepository channelRepository;

    @Autowired 
    private UserRepository userRepository;

    @Test
    void shouldPersistMembership() {
        User user = userRepository.save(new User("charlie", "hashed123", "en"));
        Channel channel = channelRepository.save(new Channel("Code Club", user, false));

        ChannelMember membership = new ChannelMember(channel, user);
        ChannelMember savedMembership = channelMemberRepository.saveAndFlush(membership);

        assertThat(savedMembership.getJoinedAt()).isNotNull();
        assertThat(savedMembership.getId()).isNotNull();
        assertThat(savedMembership.getId().getChannelId()).isEqualTo(channel.getId());
        assertThat(savedMembership.getId().getUserId()).isEqualTo(user.getId());
    }

    @Test 
    void shouldFindMembershipByCompositeId() {
        User user = userRepository.save(new User("charlie", "hashed123", "en"));
        Channel channel = channelRepository.save(new Channel("Code Club", user, false));
        channelMemberRepository.saveAndFlush(new ChannelMember(channel, user));

        ChannelMemberId compositeId = new ChannelMemberId(channel.getId(), user.getId());
        Optional<ChannelMember> found = channelMemberRepository.findById(compositeId);

        assertThat(found).isPresent();
        assertThat(found.get().getUser().getUsername()).isEqualTo("charlie");
        assertThat(found.get().getChannel().getName()).isEqualTo("Code Club");
    }

    @Test
    void shouldFindAllMembersInChannel() {
        User user1 = userRepository.save(new User("charlie", "hashed123", "en"));
        User user2 = userRepository.save(new User("dana", "hashedpw12", "kr"));
        Channel channel = channelRepository.save(new Channel("Code Club", user1, false));

        channelMemberRepository.save(new ChannelMember(channel, user1));
        channelMemberRepository.save(new ChannelMember(channel, user2));
        channelMemberRepository.flush();

        List<ChannelMember> members = channelMemberRepository.findByIdChannelId(channel.getId());

        assertThat(members).hasSize(2);
        assertThat(members)
                .extracting(member -> member.getUser().getUsername())
                .containsExactlyInAnyOrder("charlie", "dana");
    }
}
