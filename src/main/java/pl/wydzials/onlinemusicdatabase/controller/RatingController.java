package pl.wydzials.onlinemusicdatabase.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.wydzials.onlinemusicdatabase.model.RateableEntity;
import pl.wydzials.onlinemusicdatabase.model.Rating;
import pl.wydzials.onlinemusicdatabase.model.Rating.Stars;
import pl.wydzials.onlinemusicdatabase.model.User;
import pl.wydzials.onlinemusicdatabase.repository.RateableEntityRepository;
import pl.wydzials.onlinemusicdatabase.repository.RatingRepository;
import pl.wydzials.onlinemusicdatabase.repository.UserRepository;
import pl.wydzials.onlinemusicdatabase.utils.GlobalConfiguration;
import pl.wydzials.onlinemusicdatabase.utils.Validation;

@Controller
@Transactional
public class RatingController extends BaseController {

  private final RatingRepository ratingRepository;
  private final UserRepository userRepository;
  private final RateableEntityRepository rateableEntityRepository;

  public RatingController(final RatingRepository ratingRepository, final UserRepository userRepository,
      final RateableEntityRepository rateableEntityRepository) {
    this.ratingRepository = ratingRepository;
    this.userRepository = userRepository;
    this.rateableEntityRepository = rateableEntityRepository;
  }

  @PostMapping("/user/rating/{id}")
  public String postRating(@PathVariable final Long id, final PostRatingRequest request, final Principal principal,
      final HttpServletRequest httpServletRequest, final RedirectAttributes redirectAttributes) {
    Validation.notNull(id);
    Validation.notNull(principal);
    Validation.notNull(request);

    if(request.review != null && request.review.length() > 1000) {
      addFlashMessage(redirectAttributes, "Długość recenzji nie może przekroczyć 1000 znaków");
      redirectToReferrer(httpServletRequest);
    }

    final RateableEntity entity = rateableEntityRepository.findById(id).orElseThrow();
    final User user = userRepository.findByUsername(principal.getName()).orElseThrow();

    final Optional<Rating> ratingOptional = ratingRepository.findByUserAndEntity(user, entity);

    if (ratingOptional.isPresent())
      entity.deleteRating(user, ratingRepository);

    if (!request.isDeleteRequest()) {
      try {
        final LocalDate parsedDate = LocalDate.parse(request.date());
        if (parsedDate.isAfter(GlobalConfiguration.getCurrentDate())) {
          addFlashMessage(redirectAttributes, "Data nie może być w przyszłości.");
          return redirectToReferrer(httpServletRequest);
        }

        entity.createRating(user, request.getStarsEnum(), request.getReview(), ratingRepository, parsedDate);
      } catch (DateTimeParseException e) {
        addFlashMessage(redirectAttributes, "Podano nieprawidłową datę.");
      }
    }

    return redirectToReferrer(httpServletRequest);
  }

  @PostMapping("/user/like")
  public String postLike(final PostLikeRequest request, final Principal principal,
      final HttpServletRequest httpServletRequest) {
    Validation.notNull(request);
    Validation.notNull(principal);

    final User user = userRepository.findByUsername(principal.getName()).orElseThrow();
    final Rating rating = ratingRepository.findById(request.ratingId()).orElseThrow();
    if (request.create()) {
      rating.createLike(user);
    } else {
      rating.removeLike(user);
    }

    return redirectToReferrer(httpServletRequest);
  }

  public record PostRatingRequest(int stars, String review, String date) {

    public boolean isDeleteRequest() {
      return stars == -1;
    }

    public Stars getStarsEnum() {
      return Stars.of(stars);
    }

    public String getReview() {
      if (review != null && review.length() > 1000)
        Validation.throwIllegalArgumentException();

      return (review == null || review.isBlank()) ? null : review;
    }
  }

  public record PostLikeRequest(long ratingId, boolean create) {

  }
}
