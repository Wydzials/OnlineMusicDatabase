package pl.wydzials.onlinemusicdatabase.controller;

import java.security.Principal;
import javax.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.wydzials.onlinemusicdatabase.model.User;
import pl.wydzials.onlinemusicdatabase.repository.UserRepository;
import pl.wydzials.onlinemusicdatabase.utils.Validation;

@Controller
public class UserController extends BaseController {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UserController(final UserRepository userRepository, final PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @GetMapping("/login")
  public String getLogin() {
    return "login";
  }

  @PostMapping("/login-failure")
  public String getLoginFailure(final RedirectAttributes redirectAttributes) {
    addFlashMessage(redirectAttributes, "Niepoprawne dane logowania");
    return "redirect:/login";
  }

  @GetMapping("/register")
  public String getRegister() {
    return "register";
  }

  @PostMapping("/register")
  public String postRegister(final PostRegisterRequest request, final RedirectAttributes redirectAttributes) {
    Validation.notNull(request);

    if (!request.isUsernameCorrect())
      addFlashMessage(redirectAttributes, "Nieprawidłowa nazwa użytkownika");

    if (!request.isPasswordCorrect())
      addFlashMessage(redirectAttributes, "Nieprawidłowe hasło");

    if (userRepository.findByUsername(request.username).isPresent())
      addFlashMessage(redirectAttributes, "Nazwa użytkownika jest zajęta");

    if (redirectAttributes.getFlashAttributes().isEmpty()) {
      return "redirect:/register";
    }

    User user = new User(request.username, passwordEncoder.encode(request.password1));
    userRepository.save(user);

    return "redirect:/login";
  }

  @GetMapping("/user/profile")
  public String getProfile() {
    return "profile";
  }

  @PostMapping("/user/update")
  @Transactional
  public String updateUser(final UpdateUserRequest request, final Principal principal,
      final RedirectAttributes redirectAttributes) {
    Validation.notNull(principal);

    final User user = userRepository.findByUsername(principal.getName()).orElseThrow();

    if (userRepository.findByUsername(request.username).isPresent())
      addFlashMessage(redirectAttributes, "Nazwa użytkownika jest zajęta");

    if (!redirectAttributes.getFlashAttributes().isEmpty()) {
      return "redirect:/user/profile";
    }

    user.updateDetails(request.username);
    SecurityContextHolder.getContext().setAuthentication(null);

    addFlashMessage(redirectAttributes, "Dane konta zostały zaktualizowane, zaloguj się ponownie");
    return "redirect:/login";
  }

  @PostMapping("/user/update-password")
  @Transactional
  public String updatePassword(final UpdatePasswordRequest request, final Principal principal,
      final RedirectAttributes redirectAttributes) {
    Validation.notNull(principal);

    final User user = userRepository.findByUsername(principal.getName()).orElseThrow();

    if (request.oldPassword != null && !user.isPasswordEqual(request.oldPassword, passwordEncoder))
      addFlashMessage(redirectAttributes, "Stare hasło jest nieprawidłowe");

    if (!request.isPasswordCorrect())
      addFlashMessage(redirectAttributes, "Nowe hasło jest nieprawidłowe");

    if (!redirectAttributes.getFlashAttributes().isEmpty()) {
      return "redirect:/user/profile";
    }

    user.updatePassword(request.newPassword1, passwordEncoder);
    SecurityContextHolder.getContext().setAuthentication(null);

    addFlashMessage(redirectAttributes, "Hasło zostało zaktualizowane, zaloguj się ponownie");
    return "redirect:/login";
  }

  public record PostRegisterRequest(String username, String password1, String password2) {

    boolean isUsernameCorrect() {
      return username != null && username.length() > 0;
    }

    boolean isPasswordCorrect() {
      return password1 != null && password1.length() > 0 && password1.equals(password2);
    }
  }

  public record UpdateUserRequest(String username) {

    boolean isUsernameCorrect() {
      return username != null && username.length() > 0;
    }
  }

  public record UpdatePasswordRequest(String oldPassword, String newPassword1, String newPassword2) {

    boolean isPasswordCorrect() {
      return newPassword1 != null
          && newPassword1.length() > 0
          && newPassword1.equals(newPassword2);
    }
  }
}
