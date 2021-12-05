package pl.wydzials.onlinemusicdatabase.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import pl.wydzials.onlinemusicdatabase.utils.Validation;

@Entity
public class Playlist extends BaseEntity {

  private String name;

  @ManyToOne(cascade = CascadeType.PERSIST)
  private User user;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "playlist")
  @OrderBy("position")
  private List<PlaylistEntry> entries = new ArrayList<>();

  @Deprecated
  protected Playlist() {
  }

  public Playlist(final String name, final User user) {
    Validation.notEmpty(name);
    Validation.notNull(user);

    this.name = name;
    this.user = user;
  }

  public void addRecording(final Recording recording) {
    Validation.notNull(recording);
    if (contains(recording))
      Validation.throwIllegalArgumentException();

    final PlaylistEntry playlistEntry = new PlaylistEntry(recording, this, entries.size(), PrivateToken.INSTANCE);

    entries.add(playlistEntry);
  }

  public void deleteRecording(final Recording recording) {
    Validation.notNull(recording);
    if (!contains(recording))
      Validation.throwIllegalArgumentException();

    final PlaylistEntry playlistEntry = entries.stream()
        .filter(entry -> entry.getRecording().equals(recording))
        .findFirst()
        .orElseThrow();

    entries.remove(playlistEntry);
  }

  public boolean isUserEqualTo(final User user) {
    Validation.notNull(user);
    return this.user.equals(user);
  }

  public boolean contains(final Recording recording) {
    return entries.stream()
        .anyMatch(playlistEntry -> playlistEntry.getRecording().equals(recording));
  }

  public String getName() {
    return name;
  }

  public int getSize() {
    return entries.size();
  }

  public List<Recording> getRecordings() {
    return entries.stream()
        .map(PlaylistEntry::getRecording)
        .collect(Collectors.toList());
  }

  static final class PrivateToken {

    private static final PrivateToken INSTANCE = new PrivateToken();

    private PrivateToken() {}
  }
}
