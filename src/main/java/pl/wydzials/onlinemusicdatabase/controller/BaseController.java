package pl.wydzials.onlinemusicdatabase.controller;

import java.security.Principal;
import java.util.Collections;
import java.util.NoSuchElementException;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;
import pl.wydzials.onlinemusicdatabase.utils.Validation;

public class BaseController {

  @ModelAttribute("username")
  public String getUsername(Principal principal) {
    if (principal != null)
      return principal.getName();
    return null;
  }

  public String redirectToReferrer(final HttpServletRequest httpServletRequest) {
    Validation.notNull(httpServletRequest);

    return "redirect:" + httpServletRequest.getHeader("Referer");
  }

  @ExceptionHandler(NoSuchElementException.class)
  public ModelAndView noSuchElementException() {
    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("index");
    modelAndView.addObject("messages", Collections.singletonList("Nie znaleziono podanego zasobu"));
    return modelAndView;
  }
}
