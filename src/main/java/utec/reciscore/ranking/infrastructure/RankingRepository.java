package utec.reciscore.ranking.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import utec.reciscore.user.model.User;

import java.util.List;

public interface RankingRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u ORDER BY u.points DESC")
    List<User> findAllOrderByPointsDesc();

    @Query("SELECT u FROM User u WHERE u.location = :location ORDER BY u.points DESC")
    List<User> findByLocationOrderByPointsDesc(String location);
}