package pl.wydzials.onlinemusicdatabase.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import pl.wydzials.onlinemusicdatabase.utils.Validation;

@Entity
public class UserLike extends BaseEntity {

  @ManyToOne(cascade = CascadeType.PERSIST)
  private Rating rating;

  @ManyToOne(cascade = CascadeType.PERSIST)
  private User user;

  @Deprecated
  protected UserLike() {
  }

  public UserLike(final Rating rating, final User user) {
    Validation.notNull(rating);
    Validation.notNull(user);

    this.rating = rating;
    this.user = user;
  }

  public boolean isUserEqualTo(final User user) {
    Validation.notNull(user);
    return this.user.equals(user);
  }
}
