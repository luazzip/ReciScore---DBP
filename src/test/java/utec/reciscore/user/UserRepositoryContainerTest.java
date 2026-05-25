package utec.reciscore.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import utec.reciscore.user.infrastructure.UserRepository;
import utec.reciscore.user.model.Role;
import utec.reciscore.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryContainerTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("reciscore_test")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        User u1 = new User();
        u1.setEmail("ana@test.com");
        u1.setPassword("pass");
        u1.setName("Ana");
        u1.setUsername("ana");
        u1.setPoints(150);
        u1.setMultiplier(1.0);
        u1.setReciclajes(0);
        u1.setNivel(1);
        u1.setRachaDias(0);
        u1.setRole(Role.USER);
        u1.setLocation("Lima");

        User u2 = new User();
        u2.setEmail("bob@test.com");
        u2.setPassword("pass");
        u2.setName("Bob");
        u2.setUsername("bob");
        u2.setPoints(50);
        u2.setMultiplier(1.0);
        u2.setReciclajes(0);
        u2.setNivel(1);
        u2.setRachaDias(0);
        u2.setRole(Role.USER);
        u2.setLocation("Arequipa");

        userRepository.save(u1);
        userRepository.save(u2);
    }

    @Test
    void shouldFindUserByEmailWhenExists() {
        Optional<User> result = userRepository.findByEmail("ana@test.com");
        assertTrue(result.isPresent());
        assertEquals("Ana", result.get().getName());
    }

    @Test
    void shouldReturnTrueWhenUsernameExistsInPostgres() {
        assertTrue(userRepository.existsByUsername("bob"));
    }

    @Test
    void shouldReturnUsersOrderedByPointsDescWhenMultipleExist() {
        List<User> result = userRepository.findAllByOrderByPointsDesc();
        assertEquals(2, result.size());
        assertTrue(result.get(0).getPoints() >= result.get(1).getPoints());
    }

    @Test
    void shouldFilterUsersByLocationIgnoringCaseWhenLocationMatches() {
        List<User> result = userRepository.findByLocationIgnoreCaseOrderByPointsDesc("lima");
        assertEquals(1, result.size());
        assertEquals("Ana", result.get(0).getName());
    }

    @Test
    void shouldReturnEmptyWhenEmailDoesNotExistInPostgres() {
        Optional<User> result = userRepository.findByEmail("noexiste@test.com");
        assertFalse(result.isPresent());
    }
}