package com.lingodrop.server.repository;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import com.lingodrop.server.model.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {
    
    @Autowired 
    private UserRepository userRepository;

    @Test 
    void shouldSaveandFindUserByUsername() {
        
        User user = new User("Alice", "dummy_password", "es");

        userRepository.save(user);
        Optional<User> result = userRepository.findByUsername("Alice");

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("Alice");
        assertThat(result.get().getNativeLanguage()).isEqualTo("es");
        assertThat(result.get().getId()).isNotNull();
        assertThat(result.get().getCreatedAt()).isNotNull();

    }

    @Test
    void shouldEnforceUniqueUsernameConstraint() {

        User user1 = new User("duplicate_user", "hash1", "en");
        User user2 = new User("duplicate_user", "hash2", "fr");

        userRepository.save(user1);
        userRepository.save(user2);

        assertThatThrownBy(() -> userRepository.flush())
                .isInstanceOf(DataIntegrityViolationException.class);
    }

}
