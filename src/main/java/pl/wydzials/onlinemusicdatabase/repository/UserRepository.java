package pl.wydzials.onlinemusicdatabase.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.wydzials.onlinemusicdatabase.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findById(Long id);

  Optional<User> findByUsername(String username);

  @Query("select u from User u "
      + "where lower(u.username) like %:username%")
  List<User> searchByUsername(String username, Pageable pageable);
}
