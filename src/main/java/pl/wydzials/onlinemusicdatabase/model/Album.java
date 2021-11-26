package pl.wydzials.onlinemusicdatabase.model;

import java.time.Duration;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.ToStringBuilder;
import pl.wydzials.onlinemusicdatabase.utils.Validation;

@Entity
public class Album extends RateableEntity {

  private String name;
  private int year;

  @ManyToOne(cascade = CascadeType.PERSIST)
  private Artist artist;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "album")
  private Set<Recording> recordings = new HashSet<>();

  public Album(final String name, final Artist artist, final int year, final Artist.PrivateToken token) {
    Validation.notNull(name);
    Validation.notNull(artist);
    Validation.inclusiveBetween(1, Long.MAX_VALUE, year);
    Validation.notNull(token);

    this.name = name;
    this.artist = artist;
    this.year = year;
  }

  @Deprecated
  protected Album() {
  }

  public Recording createAlbumRecording(final String title, final Duration duration) {
    Validation.notEmpty(title);
    Validation.notNull(duration);

    final Recording recording = Recording.createAlbumRecording(title, duration, artist, this, PrivateToken.INSTANCE);
    recordings.add(recording);

    return recording;
  }

  public boolean isArtistEqual(final Artist artist) {
    Validate.notNull(artist);
    return this.artist.equals(artist);
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, BaseEntity.TO_STRING_STYLE)
        .appendSuper(super.toString())
        .append("name", name)
        .append("artist", artist)
        .build();
  }

  public String getName() {
    return name;
  }

  public Artist getArtist() {
    return artist;
  }

  public Set<Recording> getRecordings() {
    return recordings;
  }

  public Set<RateableEntity> getAllRateableEntities() {
    final Set<RateableEntity> recordingsSet = new HashSet<>(recordings);
    recordingsSet.add(this);
    return Collections.unmodifiableSet(recordingsSet);
  }

  public Duration getDuration() {
    final long totalSeconds = recordings.stream()
        .map(Recording::getDuration)
        .mapToLong(Duration::getSeconds)
        .sum();

    return Duration.ofSeconds(totalSeconds);
  }

  public int getYear() {
    return year;
  }

  static final class PrivateToken {

    private static final PrivateToken INSTANCE = new PrivateToken();

    private PrivateToken() {}
  }
}
