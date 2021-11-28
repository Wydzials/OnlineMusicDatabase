package pl.wydzials.onlinemusicdatabase;

import com.github.javafaker.Faker;
import java.time.Duration;
import javax.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.wydzials.onlinemusicdatabase.model.Album;
import pl.wydzials.onlinemusicdatabase.model.Artist;
import pl.wydzials.onlinemusicdatabase.model.Artist.ArtistType;
import pl.wydzials.onlinemusicdatabase.model.Rating.Stars;
import pl.wydzials.onlinemusicdatabase.model.Recording;
import pl.wydzials.onlinemusicdatabase.model.User;
import pl.wydzials.onlinemusicdatabase.repository.ArtistRepository;
import pl.wydzials.onlinemusicdatabase.repository.RatingRepository;
import pl.wydzials.onlinemusicdatabase.repository.UserRepository;

@Component
public class CommandLineAppStartupRunner implements CommandLineRunner {

  private final ArtistRepository artistRepository;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final RatingRepository ratingRepository;

  private final Faker faker;

  public CommandLineAppStartupRunner(final ArtistRepository artistRepository,
      final UserRepository userRepository,
      final PasswordEncoder passwordEncoder,
      final RatingRepository ratingRepository) {
    this.artistRepository = artistRepository;
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.ratingRepository = ratingRepository;

    faker = new Faker();
  }

  @Override
  @Transactional
  public void run(String... args) {
    Artist artist = new Artist("AURORA", "Norweska piosenkarka i autorka tekstów", ArtistType.PERSON);
    artistRepository.save(artist);

    artist.createSingleRecording("Runaway", Duration.ofMinutes(4).plusSeconds(13));

    Album album = artist.createAlbum("All My Demons Greeting Me As A Friend", 2016);
    artist.createAlbum("Infections Of A Different Kind (Step 1)", 2018);
    artist.createAlbum("A Different Kind Of Human (Step 2)", 2019);

    final Recording underTheWater = album.createAlbumRecording("Under The Water",
        Duration.ofMinutes(4).plusSeconds(24), 1);

    User user1 = new User("szymon", passwordEncoder.encode("1"));
    underTheWater.createRating(user1, Stars.FIVE, ratingRepository);

    User user2 = new User("test", passwordEncoder.encode("1"));
    underTheWater.createRating(user2, Stars.FIVE, ratingRepository);

    User user3 = new User("john", passwordEncoder.encode("1"));
    underTheWater.createRating(user3, Stars.FOUR, ratingRepository);

    userRepository.save(user1);
    userRepository.save(user2);
    userRepository.save(user3);

    createFakeData();
  }

  private void createFakeData() {
    final int ARTISTS = 5;
    final int SINGLES_FOR_ARTIST = 5;
    final int ALBUMS_FOR_ARTIST = 5;
    final int RECORDINGS_FOR_ALBUM = 10;

    for (int artistNumber = 0; artistNumber < ARTISTS; artistNumber++) {
      final Artist artist = new Artist(faker.rockBand().name(), "Opis zespołu", ArtistType.BAND);
      artistRepository.save(artist);

      for (int singleNumber = 0; singleNumber < SINGLES_FOR_ARTIST; singleNumber++) {
        artist.createSingleRecording(faker.book().title(), getRandomRecordingDuration());
      }

      for (int albumNumber = 0; albumNumber < ALBUMS_FOR_ARTIST; albumNumber++) {
        final Album album = artist.createAlbum(faker.book().title(), 2020);

        for (int albumRecordingNumber = 0; albumRecordingNumber < RECORDINGS_FOR_ALBUM; albumRecordingNumber++) {
          album.createAlbumRecording(faker.book().title(), getRandomRecordingDuration(), albumRecordingNumber + 1);
        }
      }
    }
  }

  private Duration getRandomRecordingDuration() {
    return Duration.ofMinutes(faker.random().nextInt(4) + 2)
        .plusSeconds(faker.random().nextInt(60));
  }
}
