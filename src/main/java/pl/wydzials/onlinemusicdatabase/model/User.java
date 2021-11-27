package pl.wydzials.onlinemusicdatabase.model;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import javax.persistence.Column;
import javax.persistence.Entity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;
import pl.wydzials.onlinemusicdatabase.configuration.MvcConfiguration;
import pl.wydzials.onlinemusicdatabase.utils.ImageStorageService;
import pl.wydzials.onlinemusicdatabase.utils.Validation;

@Entity
public class User extends BaseEntity implements UserDetails {

  @Column(nullable = false, unique = true)
  private String username;

  @Column(nullable = false)
  private String password;

  private String imageId;

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
