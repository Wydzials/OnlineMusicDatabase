package pl.wydzials.onlinemusicdatabase.model;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.web.multipart.MultipartFile;
import pl.wydzials.onlinemusicdatabase.configuration.MvcConfiguration;
import pl.wydzials.onlinemusicdatabase.utils.ImageStorageService;
import pl.wydzials.onlinemusicdatabase.utils.Validation;

@Entity
public class Artist extends RateableEntity {

  private String name;

  private String description;

  @Enumerated(EnumType.STRING)
  private ArtistType artistType;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "artist")
  private Set<Recording> recordings = new HashSet<>();

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "artist")
  @OrderBy("year")
  private List<Album> albums = new ArrayList<>();

  private String imageId = MvcConfiguration.PLACEHOLDER_IMAGE_ID;

  @Deprecated
  protected Artist() {
  }

  public Artist(final String name, final String description, final ArtistType artistType) {
    Validation.notEmpty(name);
    Validation.notEmpty(description);
    Validation.notNull(artistType);

    this.name = name;
    this.description = description;
    this.artistType = artistType;
  }

  public Album createAlbum(final String name, final int year) {
    Validation.notNull(name);
    Validation.inclusiveBetween(1, Long.MAX_VALUE, year);

    final Album album = new Album(name, this, year, PrivateToken.INSTANCE);
    albums.add(album);

    return album;
  }

  public Recording createSingleRecording(final String title, final Duration duration) {
    Validation.notEmpty(title);
    Validation.notNull(duration);

    final Recording recording = Recording.createSingleRecording(title, duration, this, PrivateToken.INSTANCE);
    recordings.add(recording);

    return recording;
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

  public Set<Recording> getSingleRecordings() {
    return recordings.stream()
        .filter(Recording::isSingle)
        .collect(Collectors.toSet());
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, BaseEntity.TO_STRING_STYLE)
        .appendSuper(super.toString())
        .append("name", name)
        .build();
  }


  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public ArtistType getArtistType() {
    return artistType;
  }

  public List<Album> getAlbums() {
    return albums;
  }

  public String getImageId() {
    return imageId;
  }

  static final class PrivateToken {

    private static final PrivateToken INSTANCE = new PrivateToken();

    private PrivateToken() {}
  }

  public enum ArtistType {
    BAND("Zespół"), PERSON("Osoba");

    public final String viewName;

    ArtistType(final String viewName) {
      this.viewName = viewName;
    }

    public static Optional<ArtistType> of(String name) {
      return Arrays.stream(values())
          .filter(type -> Objects.equals(type.name(), name))
          .findFirst();
    }
  }
}
