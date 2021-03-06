package pl.wydzials.onlinemusicdatabase.controller;

import java.util.Collection;
import java.util.Optional;
import pl.wydzials.onlinemusicdatabase.model.Rating;
import pl.wydzials.onlinemusicdatabase.utils.Validation;

public class UserRatingsContainer {

  private final Collection<Rating> ratings;

  public UserRatingsContainer(final Collection<Rating> ratings) {
    Validation.notNull(ratings);

    //System.out.println(Arrays.deepToString(ratings.toArray()));
    this.ratings = ratings;
  }

  public Integer getUserRating(final long entityId) {
    final Optional<Rating> optionalRating = ratings.stream()
        .filter(rating -> rating.isEntityIdEqual(entityId))
        .findFirst();

    return optionalRating.map(Rating::getStarsValue).orElse(null);
  }

  public Rating getUserRatingObject(final long entityId) {
    return ratings.stream()
        .filter(rating -> rating.isEntityIdEqual(entityId))
        .findFirst()
        .orElse(null);
  }
}
