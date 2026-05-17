package utec.reciscore.user.infrastructure;
import org.springframework.data.jpa.repository.JpaRepository;
import utec.reciscore.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);


}