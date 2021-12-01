package pl.wydzials.onlinemusicdatabase.model;

import java.util.ArrayList;
import java.util.List;
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

  public void createEntry(final Recording recording) {
    Validation.notNull(recording);

    final PlaylistEntry playlistEntry = new PlaylistEntry(recording, this, entries.size(),
        PrivateToken.INSTANCE);

    entries.add(playlistEntry);
  }

  public String getName() {
    return name;
  }

  public List<PlaylistEntry> getEntries() {
    return entries;
  }

  static final class PrivateToken {

    private static final PrivateToken INSTANCE = new PrivateToken();

    private PrivateToken() {}
  }
}
