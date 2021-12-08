package pl.wydzials.onlinemusicdatabase.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.wydzials.onlinemusicdatabase.model.FriendRequest;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
}
