package pl.wydzials.onlinemusicdatabase.model;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import pl.wydzials.onlinemusicdatabase.model.Rating.Stars;
import pl.wydzials.onlinemusicdatabase.repository.RatingRepository;
import pl.wydzials.onlinemusicdatabase.utils.Validation;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class RateableEntity extends BaseEntity {

  private Double averageRating;
  private int numberOfRatings;

  public void createRating(final User user, final Stars stars, final RatingRepository ratingRepository) {
    Validation.notNull(user);
    Validation.notNull(stars);
    Validation.notNull(ratingRepository);

    final Rating rating = new Rating(this, user, stars);
    ratingRepository.save(rating);

    if (averageRating == null) {
      averageRating = (double) stars.getValue();
    } else {
      averageRating += (rating.getStarsValue() - averageRating) / (numberOfRatings + 1);
    }

    numberOfRatings++;
  }

  public void deleteRating(final User user, final RatingRepository ratingRepository) {
    Validation.notNull(user);
    Validation.notNull(ratingRepository);

    final Rating rating = ratingRepository.findByUsernameAndEntity(user.getUsername(), this).orElseThrow();
    ratingRepository.delete(rating);

    if (numberOfRatings == 1) {
      averageRating = null;
    } else {
      averageRating = (averageRating * numberOfRatings - rating.getStarsValue()) / (numberOfRatings - 1);
    }

    numberOfRatings--;
  }

  public Double getAverageRating() {
    return averageRating;
  }
}
