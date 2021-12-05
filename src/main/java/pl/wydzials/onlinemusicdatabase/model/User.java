package pl.wydzials.onlinemusicdatabase.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;
import pl.wydzials.onlinemusicdatabase.configuration.MvcConfiguration;
import pl.wydzials.onlinemusicdatabase.repository.PlaylistRepository;
import pl.wydzials.onlinemusicdatabase.utils.ImageStorageService;
import pl.wydzials.onlinemusicdatabase.utils.Validation;

@Entity
@Table(indexes = @Index(columnList = "username"))
public class User extends BaseEntity implements UserDetails {

  @Column(nullable = false, unique = true)
  private String username;

  @Column(nullable = false)
  private String password;

  private String imageId;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user")
  private List<Playlist> playlists = new ArrayList<>();

  @Deprecated
  protected User() {
  }

  public User(final String username, final String password) {
    Validation.notEmpty(username);
    Validation.notEmpty(password);

    this.username = username;
    this.password = password;

    this.imageId = MvcConfiguration.PLACEHOLDER_IMAGE_ID;
  }

  public void updateDetails(final String username) {
    if (username != null && username.length() < 1)
      Validation.throwIllegalArgumentException();

    if (username != null) {
      this.username = username;
    }
  }

  public void updatePassword(final String rawPassword, final PasswordEncoder passwordEncoder) {
    Validation.notEmpty(rawPassword);
    Validation.notNull(passwordEncoder);

    this.password = passwordEncoder.encode(rawPassword);
  }

  public boolean isPasswordEqual(final String rawPassword, final PasswordEncoder passwordEncoder) {
    Validation.notEmpty(rawPassword);
    Validation.notNull(passwordEncoder);

    return passwordEncoder.matches(rawPassword, this.password);
  }

  public void addImage(final MultipartFile multipartFile, final ImageStorageService imageStorageService)
      throws IOException {
    Validation.notNull(imageId);

    this.imageId = imageStorageService.save(multipartFile);
  }

  public void createPlaylist(final String name) {
    Validation.notEmpty(name);

    final Playlist playlist = new Playlist(name, this);
    playlists.add(playlist);
  }

  public void deletePlaylist(final Playlist playlist, final PlaylistRepository playlistRepository) {
    Validation.notNull(playlist);
    Validation.notNull(playlistRepository);

    if (!playlists.contains(playlist))
      Validation.throwIllegalArgumentException();

    playlists.remove(playlist);
    playlistRepository.delete(playlist);
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.emptyList();
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return username;
  }

  public String getImageId() {
    return imageId;
  }

  public List<Playlist> getPlaylists() {
    return playlists;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
