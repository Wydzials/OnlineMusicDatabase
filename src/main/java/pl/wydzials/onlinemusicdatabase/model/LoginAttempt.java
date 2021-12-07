package pl.wydzials.onlinemusicdatabase.model;

import java.time.LocalDateTime;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import pl.wydzials.onlinemusicdatabase.utils.GlobalConfiguration;
import pl.wydzials.onlinemusicdatabase.utils.Validation;

@Entity
public class LoginAttempt extends BaseEntity {

  @ManyToOne(cascade = CascadeType.PERSIST)
  private User user;

  private LocalDateTime dateTime;
  private String ip;
  private boolean successful;

  protected LoginAttempt() {
  }

  public LoginAttempt(final User user, final String ip, final boolean successful) {
    Validation.notNull(user);
    Validation.notEmpty(ip);

    this.user = user;
    this.dateTime = GlobalConfiguration.getCurrentDateTime();
    this.ip = ip.equals("0:0:0:0:0:0:0:1") ? "127.0.0.1" : ip;
    this.successful = successful;
  }

  public LocalDateTime getDateTime() {
    return dateTime;
  }

  public String getIp() {
    return ip;
  }

  public boolean isSuccessful() {
    return successful;
  }
}
