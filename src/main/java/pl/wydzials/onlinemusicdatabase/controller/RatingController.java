package pl.wydzials.onlinemusicdatabase.controller;

import java.security.Principal;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.wydzials.onlinemusicdatabase.model.RateableEntity;
import pl.wydzials.onlinemusicdatabase.model.Rating;
import pl.wydzials.onlinemusicdatabase.model.Rating.Stars;
import pl.wydzials.onlinemusicdatabase.model.User;
import pl.wydzials.onlinemusicdatabase.repository.RateableEntityRepository;
import pl.wydzials.onlinemusicdatabase.repository.RatingRepository;
import pl.wydzials.onlinemusicdatabase.repository.UserRepository;
import pl.wydzials.onlinemusicdatabase.utils.Validation;

@Controller
@RequestMapping("/user/rating")
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

  @PostMapping("/{id}")
  @Transactional
  public String postRating(@PathVariable final Long id, final PostRatingRequest request, final Principal principal,
      final HttpServletRequest httpServletRequest) {
    Validation.notNull(id);
    Validation.notNull(principal);
    Validation.notNull(request);

    final RateableEntity entity = rateableEntityRepository.findById(id).orElseThrow();
    final User user = userRepository.findByUsername(principal.getName()).orElseThrow();

    final Optional<Rating> ratingOptional = ratingRepository.findByUsernameAndEntity(user.getUsername(), entity);

    if (ratingOptional.isPresent())
      entity.deleteRating(user, ratingRepository);

    if (!request.isDeleteRequest())
      entity.createRating(user, request.getStarsEnum(), ratingRepository);

    return redirectToReferrer(httpServletRequest);
  }

  public record PostRatingRequest(int stars) {

    public boolean isDeleteRequest() {
      return stars == -1;
    }

    public Stars getStarsEnum() {
      return Stars.of(stars);
    }
  }
}
