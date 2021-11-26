package pl.wydzials.onlinemusicdatabase.model;

import java.time.Duration;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import pl.wydzials.onlinemusicdatabase.utils.Validation;

@Entity
public class Recording extends RateableEntity {

  private String title;

  private Duration duration;

  @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
  private Artist artist;

  @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
  private Album album;

  @Deprecated
  protected Recording() {
  }

  private Recording(final String title, final Duration duration, final Artist artist, final Album album) {
    this.title = title;
    this.duration = duration;
    this.artist = artist;
    this.album = album;
  }

  public static Recording createSingleRecording(final String title, final Duration duration, final Artist artist,
      final Artist.PrivateToken token) {
    Validation.notNull(title);
    Validation.notNull(duration);
    Validation.notNull(artist);
    Validation.notNull(token);

    return new Recording(title, duration, artist, null);
  }

  public static Recording createAlbumRecording(final String title, final Duration duration, final Artist artist,
      final Album album, final Album.PrivateToken token) {
    Validation.notNull(title);
    Validation.notNull(duration);
    Validation.notNull(artist);

    Validation.notNull(album);
    Validation.isTrue(album.isArtistEqual(artist));

    Validation.notNull(token);

    return new Recording(title, duration, artist, album);
  }

  public boolean isSingle() {
    return album == null;
  }

  public String getTitle() {
    return title;
  }

  public Duration getDuration() {
    return duration;
  }

  public Artist getArtist() {
    return artist;
  }

  public Album getAlbum() {
    return album;
  }
}
