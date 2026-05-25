package utec.reciscore.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import utec.reciscore.user.infrastructure.UserRepository;
import utec.reciscore.user.model.Role;
import utec.reciscore.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setEmail("ana@mail.com");
        user1.setPassword("pass123");
        user1.setName("Ana");
        user1.setUsername("ana99");
        user1.setPoints(100);
        user1.setMultiplier(1.0);
        user1.setReciclajes(0);
        user1.setNivel(1);
        user1.setRachaDias(0);
        user1.setRole(Role.USER);
        user1.setLocation("Lima");

        user2 = new User();
        user2.setEmail("bob@mail.com");
        user2.setPassword("pass456");
        user2.setName("Bob");
        user2.setUsername("bob88");
        user2.setPoints(200);
        user2.setMultiplier(1.0);
        user2.setReciclajes(0);
        user2.setNivel(1);
        user2.setRachaDias(0);
        user2.setRole(Role.USER);
        user2.setLocation("Lima");

        userRepository.save(user1);
        userRepository.save(user2);
    }

    @Test
    void shouldReturnTrueWhenUsernameExists() {
        boolean exists = userRepository.existsByUsername("ana99");
        assertTrue(exists);
    }

    @Test
    void shouldReturnFalseWhenUsernameDoesNotExist() {
        boolean exists = userRepository.existsByUsername("fantasma");
        assertFalse(exists);
    }

    @Test
    void shouldReturnTrueWhenEmailExists() {
        boolean exists = userRepository.existsByEmail("ana@mail.com");
        assertTrue(exists);
    }

    @Test
    void shouldReturnUserWhenEmailMatches() {
        Optional<User> result = userRepository.findByEmail("bob@mail.com");
        assertTrue(result.isPresent());
        assertEquals("Bob", result.get().getName());
    }

    @Test
    void shouldReturnEmptyWhenEmailDoesNotExist() {
        Optional<User> result = userRepository.findByEmail("noexiste@mail.com");
        assertFalse(result.isPresent());
    }

    @Test
    void shouldReturnUsersOrderedByPointsDescending() {
        List<User> result = userRepository.findAllByOrderByPointsDesc();
        assertEquals(2, result.size());
        assertTrue(result.get(0).getPoints() >= result.get(1).getPoints());
    }

    @Test
    void shouldReturnUsersFromSameLocationOrderedByPoints() {
        List<User> result = userRepository.findByLocationIgnoreCaseOrderByPointsDesc("lima");
        assertEquals(2, result.size());
        assertTrue(result.get(0).getPoints() >= result.get(1).getPoints());
    }

    @Test
    void shouldReturnEmptyWhenNoUsersInLocation() {
        List<User> result = userRepository.findByLocationIgnoreCaseOrderByPointsDesc("Arequipa");
        assertTrue(result.isEmpty());
    }
}