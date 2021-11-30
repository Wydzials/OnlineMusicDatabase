package pl.wydzials.onlinemusicdatabase;

import com.github.javafaker.Faker;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.wydzials.onlinemusicdatabase.model.Album;
import pl.wydzials.onlinemusicdatabase.model.Artist;
import pl.wydzials.onlinemusicdatabase.model.Artist.ArtistType;
import pl.wydzials.onlinemusicdatabase.model.RateableEntity;
import pl.wydzials.onlinemusicdatabase.model.Rating.Stars;
import pl.wydzials.onlinemusicdatabase.model.User;
import pl.wydzials.onlinemusicdatabase.repository.ArtistRepository;
import pl.wydzials.onlinemusicdatabase.repository.RatingRepository;
import pl.wydzials.onlinemusicdatabase.repository.UserRepository;
import pl.wydzials.onlinemusicdatabase.utils.GlobalConfiguration;
import pl.wydzials.onlinemusicdatabase.utils.ImageStorageService;

@Component
public class CommandLineAppStartupRunner implements CommandLineRunner {

  private final ArtistRepository artistRepository;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final RatingRepository ratingRepository;
  private final ImageStorageService imageStorageService;

  private final Faker faker;

  private final List<User> users = new ArrayList<>();

  public CommandLineAppStartupRunner(final ArtistRepository artistRepository,
      final UserRepository userRepository,
      final PasswordEncoder passwordEncoder,
      final RatingRepository ratingRepository,
      final ImageStorageService imageStorageService) {
    this.artistRepository = artistRepository;
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.ratingRepository = ratingRepository;
    this.imageStorageService = imageStorageService;

    faker = new Faker();
  }

  @Override
  @Transactional
  public void run(String... args) throws IOException {
    GlobalConfiguration.setIsGeneratingData(true);

    createUsers();
    createAuroraData();
    createPinkFloydData();
    createFakeData();

    System.out.println("Data generated");
    GlobalConfiguration.setIsGeneratingData(false);
  }

  private void createUsers() {
    users.add(new User("szymon", passwordEncoder.encode("1")));

    for (int i = 0; i < 10; i++) {
      final String username = "User" + (i + 1);
      final String password = String.valueOf(i + 1);

      final User user = new User(username, password);
      users.add(user);
    }
    userRepository.saveAll(users);
  }

  private void createRatings(final Set<? extends RateableEntity> entities, int min, int max) {
    for (final RateableEntity entity : entities) {
      int numberOfRatings = faker.random().nextInt(0, users.size() - 1);

      for (int i = 0; i < numberOfRatings; i++) {
        final User user = users.get(i);
        final Stars stars = Stars.of(faker.random().nextInt(min, max));

        entity.createRating(user, stars, ratingRepository, GlobalConfiguration.getCurrentDate());
      }
    }
  }

  private void createAuroraData() throws IOException {
    Artist aurora = new Artist("AURORA", "Norweska piosenkarka i autorka tekstów", ArtistType.PERSON);
    artistRepository.save(aurora);

    aurora.addLink("Oficjalna strona", "https://www.aurora-music.com/");
    aurora.addLink("Instagram", "https://www.instagram.com/auroramusic/");
    aurora.addLink("YouTube", "https://www.youtube.com/channel/UCNjHgaLpdy1IMNK57pYiKiQ");

    //    aurora.addImage(new MockMultipartFile("aurora.jpg", new FileInputStream("images/aurora.jpg")),
    //        imageStorageService);
    aurora.addImageId("aurora2");

    Album album1 = aurora.createAlbum("All My Demons Greeting Me As A Friend", 2016);
    album1.addImageId("aurora-album1");
    album1.createAlbumRecording("Runaway", duration(4, 8), 1);
    album1.createAlbumRecording("Conqueror", duration(3, 25), 2);
    album1.createAlbumRecording("Running With The Wolves", duration(3, 13), 3);
    album1.createAlbumRecording("Lucky", duration(4, 13), 4);
    album1.createAlbumRecording("Winter Bird", duration(4, 3), 5);
    album1.createAlbumRecording("I Went Too Far", duration(3, 26), 6);
    album1.createAlbumRecording("Through The Eyes Of A Child", duration(4, 34), 7);
    album1.createAlbumRecording("Warrior", duration(3, 42), 8);
    album1.createAlbumRecording("Murder Song (5, 4, 3, 2, 1)", duration(3, 19), 9);
    album1.createAlbumRecording("Home", duration(3, 32), 10);
    album1.createAlbumRecording("Under The Water", duration(4, 24), 11);
    album1.createAlbumRecording("Black Water Lilies", duration(4, 41), 12);
    album1.createAlbumRecording("Half The World Away", duration(3, 18), 13);
    album1.createAlbumRecording("Murder Song (5, 4, 3, 2, 1) (Acoustic)", duration(3, 37), 14);
    album1.createAlbumRecording("Nature Boy (Acoustic)", duration(2, 59), 15);
    album1.createAlbumRecording("Wisdom Cries", duration(4, 5), 16);
    album1.createAlbumRecording("Running With The Wolves (Pablo Nouvelle Remix)", duration(3, 49), 17);

    Album album2 = aurora.createAlbum("Infections Of A Different Kind (Step 1)", 2018);
    album2.addImageId("aurora-album2");
    album2.createAlbumRecording("Queendom", duration(3, 27), 1);
    album2.createAlbumRecording("Forgotten Love", duration(3, 29), 2);
    album2.createAlbumRecording("Gentle Earthquakes", duration(3, 47), 3);
    album2.createAlbumRecording("All Is Soft Inside", duration(5, 9), 4);
    album2.createAlbumRecording("It Happened Quiet", duration(4, 9), 5);
    album2.createAlbumRecording("Churchyard", duration(3, 46), 6);
    album2.createAlbumRecording("Soft Universe", duration(4, 0), 7);
    album2.createAlbumRecording("Infections of a Different Kind", duration(5, 27), 8);

    Album album3 = aurora.createAlbum("A Different Kind Of Human (Step 2)", 2019);
    album3.addImageId("aurora-album3");
    album3.createAlbumRecording("The River", duration(3, 37), 1);
    album3.createAlbumRecording("Animal", duration(3, 35), 2);
    album3.createAlbumRecording("Dance On The Moon", duration(3, 36), 3);
    album3.createAlbumRecording("Daydreamer", duration(3, 39), 4);
    album3.createAlbumRecording("Hunger", duration(2, 46), 5);
    album3.createAlbumRecording("Soulless Creatures", duration(5, 2), 6);
    album3.createAlbumRecording("In Bottles", duration(3, 58), 7);
    album3.createAlbumRecording("A Different Kind Of Human", duration(4, 1), 8);
    album3.createAlbumRecording("Apple Tree", duration(3, 8), 9);
    album3.createAlbumRecording("The Seed", duration(4, 26), 10);
    album3.createAlbumRecording("Mothership", duration(2, 16), 11);

    final Set<RateableEntity> recordings = new HashSet<>();
    for (Album album : aurora.getAlbums()) {
      recordings.addAll(album.getAllRateableEntities());
    }

    createRatings(recordings, 3, 5);
    createRatings(Collections.singleton(aurora), 3, 5);
  }

  private void createPinkFloydData() throws IOException {
    Artist pinkFloyd = new Artist("Pink Floyd", "Angielski zespół rockowy", ArtistType.BAND);
    artistRepository.save(pinkFloyd);

    pinkFloyd.addLink("Oficjalna strona", "https://www.pinkfloyd.com/");
    pinkFloyd.addLink("Instagram", "https://www.instagram.com/pinkfloyd/");
    pinkFloyd.addLink("Twitter", "https://twitter.com/PinkFloyd");

    //    pinkFloyd.addImage(new MockMultipartFile("pinkFloyd.jpg", new FileInputStream("images/pink-floyd.jpg")),
    //        imageStorageService);
    pinkFloyd.addImageId("pink-floyd2");

    Album album1 = pinkFloyd.createAlbum("The Dark Side of the Moon", 1973);
    album1.addImageId("pink-floyd-album1");

    album1.createAlbumRecording("Speak to Me", duration(1, 8), 1);
    album1.createAlbumRecording("Breathe", duration(2, 49), 2);
    album1.createAlbumRecording("On the Run", duration(3, 51), 3);
    album1.createAlbumRecording("Time", duration(6, 50), 4);
    album1.createAlbumRecording("The Great Gig in the Sky", duration(4, 44), 5);
    album1.createAlbumRecording("Money", duration(6, 23), 6);
    album1.createAlbumRecording("Us and Them", duration(7, 50), 7);
    album1.createAlbumRecording("Any Colour You Like", duration(3, 26), 8);
    album1.createAlbumRecording("Brain Damage", duration(3, 47), 9);
    album1.createAlbumRecording("Eclipse", duration(2, 12), 10);

    Album album2 = pinkFloyd.createAlbum("The wall", 1979);
    album2.addImageId("pink-floyd-album2");

    album2.createAlbumRecording("In the Flesh?", duration(3, 20), 1);
    album2.createAlbumRecording("The Thin Ice", duration(2, 30), 2);
    album2.createAlbumRecording("Another Brick in the Wall, Part 1", duration(3, 11), 3);
    album2.createAlbumRecording("The Happiest Days of Our Lives", duration(1, 51), 4);
    album2.createAlbumRecording("Another Brick in the Wall, Part 2", duration(4, 1), 5);
    album2.createAlbumRecording("Mother", duration(5, 34), 6);
    album2.createAlbumRecording("Goodbye Blue Sky", duration(2, 50), 7);
    album2.createAlbumRecording("Empty Spaces", duration(2, 7), 8);
    album2.createAlbumRecording("Young Lust", duration(3, 33), 9);
    album2.createAlbumRecording("One of My Turns", duration(3, 35), 10);
    album2.createAlbumRecording("Don’t Leave Me Now", duration(4, 17), 11);
    album2.createAlbumRecording("Another Brick in the Wall, Part 3", duration(1, 18), 12);
    album2.createAlbumRecording("Goodbye Cruel World", duration(1, 15), 13);

    final Set<RateableEntity> recordings = new HashSet<>();
    for (Album album : pinkFloyd.getAlbums()) {
      recordings.addAll(album.getAllRateableEntities());
    }

    createRatings(recordings, 2, 5);
    createRatings(Collections.singleton(pinkFloyd), 3, 5);
  }

  private void createFakeData() {
    final int ARTISTS = 15;
    final int SINGLES_FOR_ARTIST = 5;
    final int ALBUMS_FOR_ARTIST = 5;
    final int RECORDINGS_FOR_ALBUM = 10;

    for (int artistNumber = 0; artistNumber < ARTISTS; artistNumber++) {
      final ArtistType artistType = faker.random().nextBoolean()
          ? ArtistType.BAND
          : ArtistType.PERSON;

      final Artist artist = new Artist(faker.rockBand().name(), "Opis...", artistType);
      artistRepository.save(artist);

      for (User user : users) {
        if (faker.random().nextBoolean())
          artist.createRating(user, Stars.of(faker.random().nextInt(1, 5)), ratingRepository,
              GlobalConfiguration.getCurrentDate());
      }

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

  private Duration duration(int minutes, int seconds) {
    return Duration.ofMinutes(minutes).plusSeconds(seconds);
  }

  private Duration getRandomRecordingDuration() {
    return Duration.ofMinutes(faker.random().nextInt(4) + 2)
        .plusSeconds(faker.random().nextInt(60));
  }
}
