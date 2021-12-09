package pl.wydzials.onlinemusicdatabase.controller;

import java.security.Principal;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.wydzials.onlinemusicdatabase.model.FriendRequest;
import pl.wydzials.onlinemusicdatabase.model.Rating;
import pl.wydzials.onlinemusicdatabase.model.Recording;
import pl.wydzials.onlinemusicdatabase.model.User;
import pl.wydzials.onlinemusicdatabase.repository.FriendRequestRepository;
import pl.wydzials.onlinemusicdatabase.repository.RatingRepository;
import pl.wydzials.onlinemusicdatabase.repository.UserRepository;
import pl.wydzials.onlinemusicdatabase.utils.Validation;

@Controller
@Transactional
public class ProfileController extends BaseController {

  private final UserRepository userRepository;
  private final FriendRequestRepository friendRequestRepository;
  private final RatingRepository ratingRepository;

  public ProfileController(final UserRepository userRepository, final FriendRequestRepository friendRequestRepository,
      final RatingRepository ratingRepository) {
    this.userRepository = userRepository;
    this.friendRequestRepository = friendRequestRepository;
    this.ratingRepository = ratingRepository;
  }

  @GetMapping("/profile/{id}")
  public String getProfile(@PathVariable final Long id, @RequestParam(required = false) String tab,
      final Model model, final Principal principal) {
    Validation.notNull(id);

    if (Tab.fromString(tab).isEmpty()) {
      return "redirect:/profile/" + id + "?tab=recordings";
    }

    final User userProfile = userRepository.findById(id).orElseThrow();
    model.addAttribute("userProfile", userProfile);

    final Tab activeTab = Tab.fromString(tab).orElseThrow();

    switch (activeTab) {
      case RECORDINGS -> {
        final List<Rating> userProfileRatings = ratingRepository.findByUserOrderByDateDesc(userProfile, Recording.class,
            PageRequest.of(0, 10));

        final List<Recording> recordings = userProfileRatings.stream()
            .map(Rating::getEntity)
            .filter(entity -> entity instanceof Recording)
            .map(entity -> (Recording) entity)
            .collect(Collectors.toList());
        model.addAttribute("recordings", recordings);

        model.addAttribute("userProfileRatings", new UserRatingsContainer(userProfileRatings));
        model.addAttribute("userRatings", createUserRatingsContainer(principal, recordings));

        final long totalCount = ratingRepository.countRatingsByUser(userProfile, Recording.class);
        model.addAttribute("totalCount", totalCount);
      }
      case ALBUMS, ARTISTS, STATISTICS -> {}
    }

    Map<String, String> tabs = new LinkedHashMap<>();
    tabs.put("statistics", "Statystyki");
    tabs.put("recordings", "Oceny utworów");
    tabs.put("albums", "Oceny albumów");
    tabs.put("artists", "Oceny artystów");
    model.addAttribute("tabs", tabs);

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

  private enum Tab {
    STATISTICS("statistics"),
    ARTISTS("artists"),
    ALBUMS("albums"),
    RECORDINGS("recordings");

    private final String value;

    Tab(final String value) {
      this.value = value;
    }

    public static Optional<Tab> fromString(final String value) {
      return Arrays.stream(values())
          .filter(tab -> Objects.equals(tab.value, value))
          .findFirst();
    }

    public String getValue() {
      return value;
    }
  }
}
