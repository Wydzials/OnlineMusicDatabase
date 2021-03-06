package pl.wydzials.onlinemusicdatabase.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;
import pl.wydzials.onlinemusicdatabase.configuration.MvcConfiguration;
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

  private String authorities;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user")
  private List<Playlist> playlists = new ArrayList<>();

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user")
  @OrderBy("dateTime desc")
  private List<LoginAttempt> loginAttempts = new LinkedList<>();

  @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
  @JoinTable(name = "friend",
      joinColumns = {@JoinColumn(name = "user1_id")},
      inverseJoinColumns = {@JoinColumn(name = "user2_id")})
  private Set<User> friends = new HashSet<>();

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "sender")
  private Set<FriendRequest> friendRequestsSent = new HashSet<>();

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "recipient")
  private Set<FriendRequest> friendRequestsReceived = new HashSet<>();

  @Deprecated
  protected User() {
  }

  public User(final String username, final String password, final boolean isAdmin) {
    Validation.notEmpty(username);
    Validation.notEmpty(password);

    this.username = username;
    this.password = password;

    this.imageId = MvcConfiguration.PLACEHOLDER_IMAGE_ID;
    this.authorities = "USER" + (isAdmin ? ",ADMIN" : "");
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

  public void deletePlaylist(final Playlist playlist) {
    Validation.notNull(playlist);

    if (!playlists.contains(playlist))
      Validation.throwIllegalArgumentException();

    playlists.remove(playlist);
  }

  public void addLoginAttempt(final String ip, final boolean successful) {
    Validation.notEmpty(ip);

    final LoginAttempt loginAttempt = new LoginAttempt(this, ip, successful);
    loginAttempts.add(0, loginAttempt);

    if (loginAttempts.size() > 10)
      loginAttempts.remove(10);
  }

  public void addFriend(final User user) {
    Validation.notNull(user);

    if (!friends.contains(user)) {
      friends.add(user);
      user.addFriend(this);
    }
  }

  public boolean isFriendOf(final User user) {
    Validation.notNull(user);

    return friends.contains(user);
  }

  public boolean hasFriendRequestSentTo(final User user) {
    Validation.notNull(user);

    return friendRequestsSent.stream()
        .anyMatch(request -> request.isRecipientEqualTo(user));
  }

  public boolean hasFriendRequestSentFrom(final User user) {
    Validation.notNull(user);

    return friendRequestsReceived.stream()
        .anyMatch(request -> request.isSenderEqualTo(user));
  }

  public void createFriendRequest(final User recipient) {
    Validation.notNull(recipient);
    if (recipient.equals(this))
      Validation.throwIllegalArgumentException();
    if (friends.contains(recipient))
      Validation.throwIllegalArgumentException();
    if (friendRequestsSent.stream().anyMatch(request -> request.isRecipientEqualTo(recipient)))
      Validation.throwIllegalArgumentException();
    if (friendRequestsReceived.stream().anyMatch(request -> request.isSenderEqualTo(recipient)))
      Validation.throwIllegalArgumentException();

    final FriendRequest friendRequest = new FriendRequest(this, recipient);

    friendRequestsSent.add(friendRequest);
    recipient.addFriendRequestToRecipient(friendRequest);
  }

  public void acceptFriendRequest(final FriendRequest friendRequest) {
    Validation.notNull(friendRequest);
    if (!friendRequest.isRecipientEqualTo(this))
      Validation.throwIllegalArgumentException();

    addFriend(friendRequest.getSender());
    removeFriendRequest(friendRequest);
  }

  public void declineFriendRequest(final FriendRequest friendRequest) {
    Validation.notNull(friendRequest);
    if (!friendRequest.isRecipientEqualTo(this))
      Validation.throwIllegalArgumentException();

    removeFriendRequest(friendRequest);
  }

  public void cancelFriendRequest(final FriendRequest friendRequest) {
    Validation.notNull(friendRequest);
    if (!friendRequest.isSenderEqualTo(this))
      Validation.throwIllegalArgumentException();

    removeFriendRequest(friendRequest);
  }

  private void addFriendRequestToRecipient(final FriendRequest friendRequest) {
    friendRequestsReceived.add(friendRequest);
  }

  private void removeFriendRequest(final FriendRequest friendRequest) {
    if (friendRequestsSent.contains(friendRequest) || friendRequestsReceived.contains(friendRequest)) {
      friendRequestsSent.remove(friendRequest);
      friendRequestsReceived.remove(friendRequest);

      final User sender = friendRequest.getSender();
      final User recipient = friendRequest.getRecipient();

      if (this.equals(sender))
        recipient.removeFriendRequest(friendRequest);
      else
        sender.removeFriendRequest(friendRequest);
    }
  }

  public boolean isAdmin() {
    return getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"));
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Arrays.stream(authorities.split(","))
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toSet());
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

  public List<LoginAttempt> getLoginAttempts() {
    return loginAttempts;
  }

  public Set<User> getFriends() {
    return friends;
  }

  public Set<FriendRequest> getFriendRequestsSent() {
    return friendRequestsSent;
  }

  public Set<FriendRequest> getFriendRequestsReceived() {
    return friendRequestsReceived;
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
