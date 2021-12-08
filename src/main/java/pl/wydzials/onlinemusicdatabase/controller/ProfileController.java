package pl.wydzials.onlinemusicdatabase.controller;

import javax.transaction.Transactional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pl.wydzials.onlinemusicdatabase.model.User;
import pl.wydzials.onlinemusicdatabase.repository.UserRepository;
import pl.wydzials.onlinemusicdatabase.utils.Validation;

@Controller
@Transactional
public class ProfileController extends BaseController {

  private final UserRepository userRepository;

  public ProfileController(final UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @GetMapping("/profile/{id}")
  public String getProfile(@PathVariable final Long id, final Model model) {
    Validation.notNull(id);

    final User userProfile = userRepository.findById(id).orElseThrow();
    model.addAttribute("userProfile", userProfile);

    return MvcView.PROFILE.get();
  }
}
