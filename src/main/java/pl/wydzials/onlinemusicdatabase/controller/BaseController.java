package pl.wydzials.onlinemusicdatabase.controller;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.wydzials.onlinemusicdatabase.model.User;
import pl.wydzials.onlinemusicdatabase.repository.UserRepository;
import pl.wydzials.onlinemusicdatabase.utils.Validation;

public class BaseController {

  @Autowired
  private UserRepository userRepository;

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
      return userRepository.findByUsername(principal.getName()).orElseThrow();
    return null;
  }

  public String redirectToReferrer(final HttpServletRequest httpServletRequest) {
    Validation.notNull(httpServletRequest);

    return "redirect:" + httpServletRequest.getHeader("Referer");
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

  @ExceptionHandler(NoSuchElementException.class)
  public ModelAndView noSuchElementException() {
    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("index");
    modelAndView.addObject("messages", Collections.singletonList("Nie znaleziono podanego zasobu"));
    return modelAndView;
  }
}
