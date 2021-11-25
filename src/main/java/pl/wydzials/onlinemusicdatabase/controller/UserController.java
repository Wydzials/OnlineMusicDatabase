package pl.wydzials.onlinemusicdatabase.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
    redirectAttributes.addFlashAttribute("messages", Collections.singletonList("Niepoprawne dane logowania"));
    return "redirect:/login";
  }

  @GetMapping("/register")
  public String getRegister() {
    return "register";
  }

  @PostMapping("/register")
  public String postRegister(final PostRegisterRequest request, final RedirectAttributes redirectAttributes) {
    Validation.notNull(request);

    List<String> flashMessages = new ArrayList<>();

    if (!request.isUsernameCorrect())
      flashMessages.add("Nieprawidłowa nazwa użytkownika");

    if (!request.isPasswordCorrect())
      flashMessages.add("Nieprawidłowe hasło");

    if (userRepository.findByUsername(request.username).isPresent())
      flashMessages.add("Nazwa użytkownika jest zajęta");

    if (!flashMessages.isEmpty()) {
      redirectAttributes.addFlashAttribute("messages", flashMessages);
      return "redirect:/register";
    }

    User user = new User(request.username, passwordEncoder.encode(request.password1));
    userRepository.save(user);

    redirectAttributes.addFlashAttribute("messages", Collections.singletonList("Konto zostało utworzone"));
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

    List<String> flashMessages = new ArrayList<>();

    final User user = userRepository.findByUsername(principal.getName()).orElseThrow();

    if (userRepository.findByUsername(request.username).isPresent())
      flashMessages.add("Nazwa użytkownika jest zajęta");

    if (!flashMessages.isEmpty()) {
      redirectAttributes.addFlashAttribute("messages", flashMessages);
      return "redirect:/user/profile";
    }

    user.updateDetails(request.username);
    SecurityContextHolder.getContext().setAuthentication(null);

    flashMessages.add("Dane konta zostały zaktualizowane, zaloguj się ponownie");
    redirectAttributes.addFlashAttribute("messages", flashMessages);
    return "redirect:/login";
  }

  @PostMapping("/user/update-password")
  @Transactional
  public String updatePassword(final UpdatePasswordRequest request, final Principal principal,
      final RedirectAttributes redirectAttributes) {
    Validation.notNull(principal);

    List<String> flashMessages = new ArrayList<>();

    final User user = userRepository.findByUsername(principal.getName()).orElseThrow();

    if (request.oldPassword != null && !user.isPasswordEqual(request.oldPassword, passwordEncoder))
      flashMessages.add("Stare hasło jest nieprawidłowe");

    if (!request.isPasswordCorrect())
      flashMessages.add("Nowe hasło jest nieprawidłowe");

    if (!flashMessages.isEmpty()) {
      redirectAttributes.addFlashAttribute("messages", flashMessages);
      return "redirect:/user/profile";
    }

    user.updatePassword(request.newPassword1, passwordEncoder);
    SecurityContextHolder.getContext().setAuthentication(null);

    flashMessages.add("Hasło zostało zaktualizowane, zaloguj się ponownie");
    redirectAttributes.addFlashAttribute("messages", flashMessages);
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
      return newPassword1 != null && newPassword1.length() > 0 && newPassword1.equals(newPassword2);
    }
  }
}
