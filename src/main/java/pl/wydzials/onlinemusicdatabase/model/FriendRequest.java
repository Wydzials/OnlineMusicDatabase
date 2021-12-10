package pl.wydzials.onlinemusicdatabase.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import pl.wydzials.onlinemusicdatabase.utils.Validation;

@Entity
public class FriendRequest extends BaseEntity {

  @ManyToOne(cascade = CascadeType.PERSIST)
  private User sender;

  @ManyToOne(cascade = CascadeType.PERSIST)
  private User recipient;

  @Deprecated
  protected FriendRequest() {
  }

  public FriendRequest(final User sender, final User recipient) {
    Validation.notNull(sender);
    Validation.notNull(recipient);

    if (sender.equals(recipient))
      Validation.throwIllegalArgumentException();

    this.sender = sender;
    this.recipient = recipient;
  }

  public boolean isRecipientEqualTo(final User user) {
    Validation.notNull(user);

    return recipient.equals(user);
  }

  public boolean isSenderEqualTo(final User user) {
    Validation.notNull(user);

    return sender.equals(user);
  }

  public User getSender() {
    return sender;
  }

  public User getRecipient() {
    return recipient;
  }
}
