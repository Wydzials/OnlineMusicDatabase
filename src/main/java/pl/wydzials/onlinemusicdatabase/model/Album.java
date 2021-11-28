package pl.wydzials.onlinemusicdatabase.model;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.web.multipart.MultipartFile;
import pl.wydzials.onlinemusicdatabase.configuration.MvcConfiguration;
import pl.wydzials.onlinemusicdatabase.utils.ImageStorageService;
import pl.wydzials.onlinemusicdatabase.utils.Validation;

@Entity
public class Album extends RateableEntity {

  private String name;
  private int year;

  @ManyToOne(cascade = CascadeType.PERSIST)
  private Artist artist;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "album")
  @OrderBy("albumPosition")
  private List<Recording> recordings = new ArrayList<>();

  private String imageId = MvcConfiguration.PLACEHOLDER_IMAGE_ID;

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

  public Recording createAlbumRecording(final String title, final Duration duration, final int albumPosition) {
    Validation.notEmpty(title);
    Validation.notNull(duration);

    final boolean positionOccupied = recordings.stream()
        .anyMatch(recording -> recording.getAlbumPosition() == albumPosition);
    if (positionOccupied)
      Validation.throwIllegalArgumentException();

    final Recording recording = Recording.createAlbumRecording(title, duration, artist, this, albumPosition,
        PrivateToken.INSTANCE);
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

  public List<Recording> getRecordings() {
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

  public String getImageId() {
    return imageId;
  }

  public void addImage(final MultipartFile multipartFile, final ImageStorageService imageStorageService)
      throws IOException {
    Validation.notNull(imageId);

    this.imageId = imageStorageService.save(multipartFile);
  }

  // For tests only
  @Deprecated
  public void addImageId(final String imageId) {
    Validation.notNull(imageId);
    this.imageId = imageId;
  }

  static final class PrivateToken {

    private static final PrivateToken INSTANCE = new PrivateToken();

    private PrivateToken() {}
  }
}
