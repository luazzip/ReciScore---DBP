package utec.reciscore.user.infrastructure;
import org.springframework.data.jpa.repository.JpaRepository;
import utec.reciscore.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    List<User> findAllByOrderByPointsDesc();
    List<User> findByLocationIgnoreCaseOrderByPointsDesc(String location);
}