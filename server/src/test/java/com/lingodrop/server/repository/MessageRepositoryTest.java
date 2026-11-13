package com.lingodrop.server.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import com.lingodrop.server.model.Channel;
import com.lingodrop.server.model.Message;
import com.lingodrop.server.model.MessageTranslation;
import com.lingodrop.server.model.User;

@DataJpaTest 
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class MessageRepositoryTest {

    @Autowired 
    private MessageRepository messageRepository;

    @Autowired 
    private ChannelRepository channelRepository;

    @Autowired 
    private UserRepository userRepository;

    private User testSender;
    private Channel testChannel;

    @BeforeEach 
    void setUp() {
        testSender = userRepository.saveAndFlush(new User("Sam", "password987", "en"));
        testChannel = channelRepository.saveAndFlush(new Channel("Code Block", testSender, false));
    }

    @Test 
    void shouldCascadeSaveMessageAndTranslations() {
        Message message = new Message(testChannel, testSender, "Hello", "en");
        message.addTranslation(new MessageTranslation("es", "Hola"));

        Message savedMessage = messageRepository.saveAndFlush(message);

        assertThat(savedMessage.getId()).isNotNull();
        assertThat(savedMessage.getCreatedAt()).isNotNull();
        assertThat(savedMessage.getTranslations()).hasSize(1);

        MessageTranslation savedTranslation = savedMessage.getTranslations().get(0);
        assertThat(savedTranslation.getId()).isNotNull();
        assertThat(savedTranslation.getLanguageCode()).isEqualTo("es");
        assertThat(savedTranslation.getTranslatedText()).isEqualTo("Hola");
    }

    @Test 
    void shouldPaginateMessagesWithSlice() {
        messageRepository.save(new Message(testChannel, testSender, "First message", "en"));
        messageRepository.save(new Message(testChannel, testSender, "Second message", "en"));
        messageRepository.save(new Message(testChannel, testSender, "Third message", "en"));
        messageRepository.flush();

        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "createdAt"));
        Slice<Message> slice = messageRepository.findByChannelId(testChannel.getId(), pageRequest);

        assertThat(slice.getContent()).hasSize(2);
        assertThat(slice.hasNext()).isTrue();
    }

    @Test
    void shouldEnforceUniqueTranslationPerLanguageConstraint() {
        Message message = new Message(testChannel, testSender, "Hello", "en");
        message.addTranslation(new MessageTranslation("es", "Hola"));
        message.addTranslation(new MessageTranslation("es", "Buenos dias"));

        assertThatThrownBy(() -> messageRepository.saveAndFlush(message))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

}
