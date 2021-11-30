package pl.wydzials.onlinemusicdatabase.model;

import javax.persistence.Entity;
import pl.wydzials.onlinemusicdatabase.utils.Validation;

@Entity
public class Link extends BaseEntity {

  private String text;

  private String link;

  @Deprecated
  protected Link() {
  }

  public Link(final String text, final String link) {
    Validation.notEmpty(text);
    Validation.notEmpty(link);

    this.text = text;
    this.link = link;
  }

  public String getText() {
    return text;
  }

  public String getLink() {
    return link;
  }
}
