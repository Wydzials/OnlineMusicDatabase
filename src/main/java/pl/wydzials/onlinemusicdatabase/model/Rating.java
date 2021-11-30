package pl.wydzials.onlinemusicdatabase.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Stream;
import javax.persistence.AttributeConverter;
import javax.persistence.CascadeType;
import javax.persistence.Converter;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import org.apache.commons.lang3.builder.ToStringBuilder;
import pl.wydzials.onlinemusicdatabase.utils.GlobalConfiguration;
import pl.wydzials.onlinemusicdatabase.utils.Validation;

@Entity
public class Rating extends BaseEntity {

  @ManyToOne(cascade = CascadeType.PERSIST)
  private RateableEntity entity;

  @ManyToOne(cascade = CascadeType.PERSIST)
  private User user;

  private Stars stars;

  private LocalDateTime created;

  @Deprecated
  protected Rating() {
  }

  public Rating(final RateableEntity entity, final User user, final Stars stars, final LocalDate date) {
    Validation.notNull(entity);
    Validation.notNull(user);
    Validation.notNull(stars);

    this.entity = entity;
    this.user = user;
    this.stars = stars;
    this.created = date.atTime(GlobalConfiguration.getCurrentTime());
  }

  public int getStarsValue() {
    return stars.getValue();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, BaseEntity.TO_STRING_STYLE)
        .appendSuper(super.toString())
        .append("entity", entity)
        .append("user", user)
        .append("stars", stars)
        .build();
  }

  public enum Stars {
    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5);

    private final int value;

    Stars(final int value) {
      this.value = value;
    }

    public int getValue() {
      return value;
    }

    public static Stars of(int value) {
      return Stream.of(Stars.values())
          .filter(s -> s.getValue() == value)
          .findFirst()
          .orElseThrow(IllegalArgumentException::new);
    }
  }

  public boolean isEntityIdEqual(final long id) {
    return entity.getId().equals(id);
  }

  @Converter(autoApply = true)
  public static class StarsConverter implements AttributeConverter<Stars, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Stars stars) {
      if (stars == null) {
        return null;
      }
      return stars.getValue();
    }

    @Override
    public Stars convertToEntityAttribute(Integer value) {
      if (value == null) {
        return null;
      }

      return Stream.of(Stars.values())
          .filter(c -> c.getValue() == value)
          .findFirst()
          .orElseThrow(IllegalArgumentException::new);
    }
  }
}
