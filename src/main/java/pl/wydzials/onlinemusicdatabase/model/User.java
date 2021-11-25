package pl.wydzials.onlinemusicdatabase.model;

import java.util.Collection;
import java.util.Collections;
import javax.persistence.Column;
import javax.persistence.Entity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.wydzials.onlinemusicdatabase.utils.Validation;

@Entity
public class User extends BaseEntity implements UserDetails {

  @Column(nullable = false, unique = true)
  private String username;

  @Column(nullable = false)
  private String password;

  @Deprecated
  protected User() {
  }

  public User(final String username, final String password) {
    Validation.notEmpty(username);
    Validation.notEmpty(password);

    this.username = username;
    this.password = password;
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
