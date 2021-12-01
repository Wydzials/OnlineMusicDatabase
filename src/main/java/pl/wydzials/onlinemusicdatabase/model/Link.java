package pl.wydzials.onlinemusicdatabase.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import pl.wydzials.onlinemusicdatabase.utils.Validation;

@Entity
public class Link extends BaseEntity {

  private String text;

  private String link;

  @ManyToOne(cascade = CascadeType.PERSIST)
  private Artist artist;

  @Deprecated
  protected Link() {
  }

  public Link(final String text, final String link, final Artist artist) {
    Validation.notEmpty(text);
    Validation.notEmpty(link);
    Validation.notNull(artist);

    this.text = text;
    this.link = link;
    this.artist = artist;
  }

  public String getText() {
    return text;
  }

  public String getLink() {
    return link;
  }
}
