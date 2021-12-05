package pl.wydzials.onlinemusicdatabase.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.wydzials.onlinemusicdatabase.model.Recording;

@Repository
public interface RecordingRepository extends JpaRepository<Recording, Long> {
}
