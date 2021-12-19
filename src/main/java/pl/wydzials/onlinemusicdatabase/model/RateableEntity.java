package pl.wydzials.onlinemusicdatabase.model;

import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import pl.wydzials.onlinemusicdatabase.model.Rating.Stars;
import pl.wydzials.onlinemusicdatabase.repository.RatingRepository;
import pl.wydzials.onlinemusicdatabase.utils.GlobalConfiguration;
import pl.wydzials.onlinemusicdatabase.utils.Validation;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class RateableEntity extends BaseEntity {

  private Double averageRating;
  private int numberOfRatings;

  public void createRating(final User user, final Stars stars, final String review,
      final RatingRepository ratingRepository, final LocalDate date) {
    Validation.notNull(user);
    Validation.notNull(stars);
    if (review != null)
      Validation.notEmpty(review);
    Validation.notNull(ratingRepository);

    if (!GlobalConfiguration.isGeneratingData() && ratingRepository.findByUserAndEntity(user, this).isPresent())
      Validation.throwIllegalStateException();

    final Rating rating = new Rating(this, user, stars, review, date);
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

    final Rating rating = ratingRepository.findByUserAndEntity(user, this).orElseThrow();
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

  public int getNumberOfRatings() {
    return numberOfRatings;
  }
}
