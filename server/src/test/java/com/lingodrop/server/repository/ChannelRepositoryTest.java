package com.lingodrop.server.repository;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import com.lingodrop.server.model.Channel;
import com.lingodrop.server.model.User;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest 
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ChannelRepositoryTest {

    @Autowired 
    private ChannelRepository channelRepository;

    @Autowired 
    private UserRepository userRepository;

    @Test 
    void shouldSaveChannelWithCreator() {
        User user = new User("bob", "password123", "en");
        User savedUser = userRepository.save(user);

        Channel channel = new Channel("General", savedUser, false);
        Channel savedChannel = channelRepository.saveAndFlush(channel);

        assertThat(savedChannel.getId()).isNotNull();
        assertThat(savedChannel.getCreatedAt()).isNotNull();
        assertThat(savedChannel.getCreatedBy().getId()).isEqualTo(savedUser.getId());
    }

    @Test 
    void shouldSaveChannelWithoutCreator() {
        Channel channel = new Channel("Default Channel", null, false);
        Channel savedChannel = channelRepository.saveAndFlush(channel);

        Optional<Channel> retrievedOptional = channelRepository.findById(savedChannel.getId());
        assertThat(retrievedOptional).isPresent();
        assertThat(retrievedOptional.get().getCreatedBy()).isNull();
    }

    @Test 
    void shouldFilterChannelsByPrivacyStatus() {
        Channel publicChannel = new Channel();
        publicChannel.setName("Public Channel");
        publicChannel.setPrivate(false);

        Channel privateChannel = new Channel();
        privateChannel.setName("Private Channel");
        privateChannel.setPrivate(true);

        channelRepository.save(publicChannel);
        channelRepository.save(privateChannel);
        channelRepository.flush();

        List<Channel> publicChannels = channelRepository.findByIsPrivate(false);
        assertThat(publicChannels).hasSize(1)
                .extracting(Channel::getName)
                .contains("Public Channel")
                .doesNotContain("Private Channel");
    }
}
