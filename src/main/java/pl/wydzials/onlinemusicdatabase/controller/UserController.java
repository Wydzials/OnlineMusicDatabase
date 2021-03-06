package pl.wydzials.onlinemusicdatabase.controller;

import java.io.IOException;
import java.security.Principal;
import javax.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.wydzials.onlinemusicdatabase.model.User;
import pl.wydzials.onlinemusicdatabase.repository.UserRepository;
import pl.wydzials.onlinemusicdatabase.utils.GlobalConfiguration;
import pl.wydzials.onlinemusicdatabase.utils.ImageStorageService;
import pl.wydzials.onlinemusicdatabase.utils.SecurityUtils;
import pl.wydzials.onlinemusicdatabase.utils.Validation;

@Controller
@Transactional
public class UserController extends BaseController {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final ImageStorageService imageStorageService;

  public UserController(final UserRepository userRepository, final PasswordEncoder passwordEncoder,
      final ImageStorageService imageStorageService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.imageStorageService = imageStorageService;
  }

  @GetMapping("/login")
  public String getLogin() {
    return MvcView.LOGIN.get();
  }

  @GetMapping("/login-failure")
  public String getLoginFailure(final RedirectAttributes redirectAttributes) {
    addFlashMessage(redirectAttributes, "Niepoprawne dane logowania.");
    return "redirect:/login";
  }

  @GetMapping("/register")
  public String getRegister() {
    return MvcView.REGISTER.get();
  }

  @PostMapping("/register")
  public String postRegister(final PostRegisterRequest request, final RedirectAttributes redirectAttributes) {
    Validation.notNull(request);

    if (!request.isUsernameCorrect())
      addFlashMessage(redirectAttributes, "Nieprawid??owa nazwa u??ytkownika.");

    if (request.isPasswordCorrect()) {
      final double entropy = SecurityUtils.getPasswordEntropy(request.password1());
      final double minEntropy = GlobalConfiguration.getMinPasswordEntropy();
      if (entropy < minEntropy) {
        addFlashMessage(redirectAttributes, ("Zbyt s??abe has??o. Minimalna entropia jest r??wna %.0f, "
            + "entropia twojego has??a wynosi??a %.2f.").formatted(minEntropy, entropy));
      }
    } else {
      addFlashMessage(redirectAttributes, "Nieprawid??owe has??o.");
    }

    if (userRepository.findByUsername(request.username).isPresent())
      addFlashMessage(redirectAttributes, "Nazwa u??ytkownika jest zaj??ta.");

    if (!redirectAttributes.getFlashAttributes().isEmpty()) {
      return "redirect:/register";
    }

    User user = new User(request.username, passwordEncoder.encode(request.password1), false);
    userRepository.save(user);

    addFlashMessage(redirectAttributes, "Konto zosta??o utworzone, mo??esz si?? zalogowa??.");
    return "redirect:/login";
  }

  @GetMapping("/user/settings")
  public String getSettings() {
    return MvcView.SETTINGS.get();
  }

  @PostMapping("/user/update-image")
  public String updateImage(final UpdateImageRequest request, final Principal principal,
      final RedirectAttributes redirectAttributes) {

    final User user = userRepository.findByUsername(principal.getName()).orElseThrow();

    if (request.profileImage != null && !request.profileImage.isEmpty()) {
      try {
        user.addImage(request.profileImage, imageStorageService);
      } catch (IOException e) {
        addFlashMessage(redirectAttributes, "Nieprawid??owy format zdj??cia.");
      }
    } else {
      addFlashMessage(redirectAttributes, "Nieprawid??owy format zdj??cia.");
    }

    return "redirect:/user/settings";
  }

  @PostMapping("/user/update-details")
  public String updateUser(final UpdateUserDetailsRequest request, final Principal principal,
      final RedirectAttributes redirectAttributes) {
    Validation.notNull(principal);

    final User user = userRepository.findByUsername(principal.getName()).orElseThrow();

    if (request.isUsernameCorrect()) {
      if (principal.getName().equals(request.username())) {
        addFlashMessage(redirectAttributes, "Nowa nazwa u??ytkownika nie mo??e by?? taka sama jak poprzednia.");
        return "redirect:/user/settings";
      }

      if (userRepository.findByUsername(request.username()).isPresent()) {
        addFlashMessage(redirectAttributes, "Nazwa u??ytkownika jest zaj??ta.");
        return "redirect:/user/settings";
      }

      user.updateDetails(request.username());
      SecurityContextHolder.getContext().setAuthentication(null);
      addFlashMessage(redirectAttributes, "Dane konta zosta??y zaktualizowane, zaloguj si?? ponownie.");
      return "redirect:/login";
    }

    return "redirect:/user/settings";
  }

  @GetMapping("/user/change-password")
  public String getChangePassword() {
    return MvcView.CHANGE_PASSWORD.get();
  }

  @PostMapping("/user/change-password")
  public String postUpdatePassword(final UpdatePasswordRequest request, final Principal principal,
      final RedirectAttributes redirectAttributes) {
    Validation.notNull(principal);

    final User user = userRepository.findByUsername(principal.getName()).orElseThrow();

    if (request.oldPassword != null && !user.isPasswordEqual(request.oldPassword, passwordEncoder))
      addFlashMessage(redirectAttributes, "Stare has??o jest nieprawid??owe.");

    if (request.isPasswordCorrect()) {
      final double entropy = SecurityUtils.getPasswordEntropy(request.newPassword1());
      final double minEntropy = GlobalConfiguration.getMinPasswordEntropy();
      if (entropy < minEntropy) {
        addFlashMessage(redirectAttributes, ("Zbyt s??abe has??o. Minimalna entropia jest r??wna %.0f, "
            + "entropia twojego has??a wynosi??a %.2f.").formatted(minEntropy, entropy));
      }
    } else {
      addFlashMessage(redirectAttributes, "Nowe has??o jest nieprawid??owe.");
    }

    if (!redirectAttributes.getFlashAttributes().isEmpty()) {
      return "redirect:/user/change-password";
    }

    user.updatePassword(request.newPassword1, passwordEncoder);
    SecurityContextHolder.getContext().setAuthentication(null);

    addFlashMessage(redirectAttributes, "Has??o zosta??o zaktualizowane, zaloguj si?? ponownie.");
    return "redirect:/login";
  }

  public record PostRegisterRequest(String username, String password1, String password2) {

    private boolean isUsernameCorrect() {
      return username != null && username.length() > 0;
    }

    private boolean isPasswordCorrect() {
      return password1 != null && password1.length() > 0 && password1.equals(password2);
    }
  }

  public record UpdateImageRequest(MultipartFile profileImage) {}

  public record UpdateUserDetailsRequest(String username) {

    private boolean isUsernameCorrect() {
      return username != null && username.length() > 0;
    }
  }

  public record UpdatePasswordRequest(String oldPassword, String newPassword1, String newPassword2) {

    private boolean isPasswordCorrect() {
      return newPassword1 != null
          && newPassword1.length() > 0
          && newPassword1.equals(newPassword2);
    }
  }
}
