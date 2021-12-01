package pl.wydzials.onlinemusicdatabase.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import pl.wydzials.onlinemusicdatabase.utils.Validation;

@Entity
public class PlaylistEntry extends BaseEntity {

  @ManyToOne(cascade = CascadeType.PERSIST)
  private Recording recording;

  @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
  private Playlist playlist;

  private int position;

  @Deprecated
  public PlaylistEntry() {
  }

  public PlaylistEntry(final Recording recording, final Playlist playlist, final int position,
      final Playlist.PrivateToken token) {
    Validation.notNull(recording);
    Validation.notNull(playlist);
    Validation.inclusiveBetween(0, Integer.MAX_VALUE, position);
    Validation.notNull(token);

    this.recording = recording;
    this.playlist = playlist;
    this.position = position;
  }

  public Recording getRecording() {
    return recording;
  }

  public int getPosition() {
    return position;
  }
}
