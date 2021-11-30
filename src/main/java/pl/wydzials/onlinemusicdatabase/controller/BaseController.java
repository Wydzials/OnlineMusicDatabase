package pl.wydzials.onlinemusicdatabase.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.wydzials.onlinemusicdatabase.model.RateableEntity;
import pl.wydzials.onlinemusicdatabase.model.Rating;
import pl.wydzials.onlinemusicdatabase.model.User;
import pl.wydzials.onlinemusicdatabase.repository.RatingRepository;
import pl.wydzials.onlinemusicdatabase.repository.UserRepository;
import pl.wydzials.onlinemusicdatabase.utils.GlobalConfiguration;
import pl.wydzials.onlinemusicdatabase.utils.Validation;

public class BaseController {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RatingRepository ratingRepository;

  public BaseController() {}

  @ModelAttribute("username")
  public String getUsername(final Principal principal) {
    if (principal != null)
      return principal.getName();
    return null;
  }

  @ModelAttribute("user")
  public User getUser(final Principal principal) {
    if (principal != null)
      return userRepository.findByUsername(principal.getName()).orElse(null);
    return null;
  }

  @ModelAttribute("flashMessages")
  public List<String> getFlashMessages() {
    return Collections.emptyList();
  }

  public String redirectToReferrer(final HttpServletRequest httpServletRequest) {
    Validation.notNull(httpServletRequest);

    return "redirect:" + httpServletRequest.getHeader("Referer");
  }

  @ModelAttribute("currentDate")
  public LocalDate getCurrentDate() {
    return GlobalConfiguration.getCurrentDate();
  }

  @SuppressWarnings("unchecked")
  public void addFlashMessage(final RedirectAttributes redirectAttributes, final String message) {
    Validation.notNull(redirectAttributes);
    Validation.notEmpty(message);

    final Map<String, ?> attributes = redirectAttributes.getFlashAttributes();

    final Object flashMessages = attributes.get("flashMessages");

    if (flashMessages instanceof List) {
      ((List<String>) flashMessages).add(message);
    } else if (flashMessages == null) {
      redirectAttributes.addFlashAttribute("flashMessages", Collections.singletonList(message));
    } else {
      Validation.throwIllegalArgumentException();
    }
  }

  @SafeVarargs
  public final UserRatingsContainer createUserRatingsContainer(final Principal principal,
      Collection<? extends RateableEntity>... entityCollections) {
    if (principal == null) {
      return new UserRatingsContainer(Collections.emptyList());
    }

    Validation.notNull(entityCollections);

    final Set<RateableEntity> entitySet = new HashSet<>();
    for (Collection<? extends RateableEntity> entityCollection : entityCollections) {
      Validation.notNull(entityCollection);
      entitySet.addAll(entityCollection);
    }

    final List<Rating> ratings = ratingRepository.findByUsernameAndEntities(principal.getName(), entitySet);
    return new UserRatingsContainer(ratings);
  }

  @ExceptionHandler(NoSuchElementException.class)
  public ModelAndView noSuchElementException() {
    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName(MvcView.INDEX.get());
    modelAndView.addObject("flashMessages", Collections.singletonList("Nie znaleziono podanego zasobu"));
    return modelAndView;
  }
}
