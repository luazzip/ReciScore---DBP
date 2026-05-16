package utec.reciscore.user.infraestructure;
import org.springframework.data.jpa.repository.JpaRepository;
import utec.reciscore.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

}