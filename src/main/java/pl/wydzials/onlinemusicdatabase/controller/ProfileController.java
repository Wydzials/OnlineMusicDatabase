package pl.wydzials.onlinemusicdatabase.controller;

import java.security.Principal;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.wydzials.onlinemusicdatabase.model.FriendRequest;
import pl.wydzials.onlinemusicdatabase.model.User;
import pl.wydzials.onlinemusicdatabase.repository.FriendRequestRepository;
import pl.wydzials.onlinemusicdatabase.repository.UserRepository;
import pl.wydzials.onlinemusicdatabase.utils.Validation;

@Controller
@Transactional
public class ProfileController extends BaseController {

  private final UserRepository userRepository;
  private final FriendRequestRepository friendRequestRepository;

  public ProfileController(final UserRepository userRepository, final FriendRequestRepository friendRequestRepository) {
    this.userRepository = userRepository;
    this.friendRequestRepository = friendRequestRepository;
  }

  @GetMapping("/profile/{id}")
  public String getProfile(@PathVariable final Long id, final Model model) {
    Validation.notNull(id);

    final User userProfile = userRepository.findById(id).orElseThrow();
    model.addAttribute("userProfile", userProfile);

    return MvcView.PROFILE.get();
  }

  @GetMapping("/user/friend-requests")
  public String getFriendRequests() {
    return MvcView.FRIEND_REQUESTS.get();
  }

  @PostMapping("/user/friend-request")
  public String sendFriendRequest(final SendFriendRequest request, final Principal principal,
      final HttpServletRequest httpServletRequest, final RedirectAttributes redirectAttributes) {
    Validation.notNull(request);
    Validation.notNull(principal);

    final User sender = userRepository.findByUsername(principal.getName()).orElseThrow();
    final User recipient = userRepository.findById(request.recipientId()).orElseThrow();

    sender.createFriendRequest(recipient);

    addFlashMessage(redirectAttributes, "Zaproszenie wysłane.");
    return redirectToReferrer(httpServletRequest);
  }

  @PostMapping("/user/friend-request/respond")
  public String respondToFriendRequest(final RespondToFriendRequest request, final Principal principal,
      final HttpServletRequest httpServletRequest, final RedirectAttributes redirectAttributes) {
    Validation.notNull(request);
    Validation.notNull(principal);

    final User recipient = userRepository.findByUsername(principal.getName()).orElseThrow();
    final FriendRequest friendRequest = friendRequestRepository.findById(request.friendRequestId()).orElseThrow();

    if (request.accept()) {
      recipient.acceptFriendRequest(friendRequest);
      addFlashMessage(redirectAttributes, "Zaproszenie zostało zaakceptowane.");
    } else {
      recipient.declineFriendRequest(friendRequest);
      addFlashMessage(redirectAttributes, "Zaproszenie zostało odrzucone.");
    }

    return redirectToReferrer(httpServletRequest);
  }

  @PostMapping("/user/friend-request/cancel")
  public String cancelFriendRequest(final CancelFriendRequest request, final Principal principal,
      final HttpServletRequest httpServletRequest) {
    Validation.notNull(request);
    Validation.notNull(principal);

    System.out.println("cancel");
    final User user = userRepository.findByUsername(principal.getName()).orElseThrow();
    final FriendRequest friendRequest = friendRequestRepository.findById(request.friendRequestId()).orElseThrow();

    user.cancelFriendRequest(friendRequest);
    return redirectToReferrer(httpServletRequest);
  }

  public record SendFriendRequest(Long recipientId) {

  }

  public record RespondToFriendRequest(long friendRequestId, boolean accept) {

  }

  public record CancelFriendRequest(long friendRequestId) {

  }
}
