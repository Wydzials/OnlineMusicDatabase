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
import pl.wydzials.onlinemusicdatabase.model.Album;
import pl.wydzials.onlinemusicdatabase.model.Artist;
import pl.wydzials.onlinemusicdatabase.model.FriendRequest;
import pl.wydzials.onlinemusicdatabase.model.RateableEntity;
import pl.wydzials.onlinemusicdatabase.model.Rating;
import pl.wydzials.onlinemusicdatabase.model.Recording;
import pl.wydzials.onlinemusicdatabase.model.User;
import pl.wydzials.onlinemusicdatabase.repository.FriendRequestRepository;
import pl.wydzials.onlinemusicdatabase.repository.RatingRepository;
import pl.wydzials.onlinemusicdatabase.repository.RatingRepository.RatingsCountByArtist;
import pl.wydzials.onlinemusicdatabase.repository.RatingRepository.RatingsCountByGenre;
import pl.wydzials.onlinemusicdatabase.repository.RatingRepository.RatingsCountByStars;
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

  @GetMapping("/profile")
  public String getMyProfile(final Principal principal) {
    if (principal == null) {
      return "redirect:/";
    }

    final User user = userRepository.findByUsername(principal.getName()).orElseThrow();
    return "redirect:/profile/" + user.getId();
  }

  @GetMapping("/profile/{id}")
  public String getProfile(@PathVariable final Long id, @RequestParam(required = false) String tab,
      @RequestParam(required = false) final Integer page, final Model model, final Principal principal) {
    Validation.notNull(id);

    if (Tab.fromString(tab).isEmpty()) {
      return "redirect:/profile/" + id + "?tab=recordings&page=1";
    }

    if (page == null || page < 0) {
      return "redirect:/profile/" + id + "?tab=" + tab + "&page=1";
    }

    final User userProfile = userRepository.findById(id).orElseThrow();
    model.addAttribute("userProfile", userProfile);

    final Tab activeTab = Tab.fromString(tab).orElseThrow();

    switch (activeTab) {
      case RECORDINGS -> prepareModelWithRatings(userProfile, model, principal, page, Recording.class);
      case ALBUMS -> prepareModelWithRatings(userProfile, model, principal, page, Album.class);
      case ARTISTS -> prepareModelWithRatings(userProfile, model, principal, page, Artist.class);
      case STATISTICS -> {
        final long totalCount = ratingRepository.countRatingsByUser(userProfile);
        final Double averageRating = ratingRepository.averateRatingByUser(userProfile);

        model.addAttribute("totalCount", totalCount);
        model.addAttribute("averageRating", averageRating);

        final List<RatingsCountByStars> ratingsCountByStars = ratingRepository.countUserRatingsGroupByStars(
            userProfile);

        final int ratingsCountByStarsMax = ratingsCountByStars.stream()
            .mapToInt(RatingsCountByStars::getCount)
            .max()
            .orElse(0);
        model.addAttribute("ratingsCountByStars", ratingsCountByStars);
        model.addAttribute("ratingsCountByStarsMax", ratingsCountByStarsMax);

        final List<RatingsCountByGenre> ratingsCountByArtistGenre = ratingRepository
            .countUserRatingsGroupByArtistGenre(userProfile);

        final int ratingsCountByArtistGenreMax = ratingsCountByArtistGenre.stream()
            .mapToInt(RatingsCountByGenre::getCount)
            .max()
            .orElse(0);
        model.addAttribute("ratingsCountByArtistGenre", ratingsCountByArtistGenre);
        model.addAttribute("ratingsCountByArtistGenreMax", ratingsCountByArtistGenreMax);

        final List<RatingsCountByArtist> ratingsCountByArtist = ratingRepository.countUserRatingsGroupByArtist(
            userProfile, PageRequest.of(0, 5));

        final int ratingsCountByArtistMax = ratingsCountByArtist.stream()
            .mapToInt(RatingsCountByArtist::getCount)
            .max()
            .orElse(0);
        model.addAttribute("ratingsCountByArtist", ratingsCountByArtist);
        model.addAttribute("ratingsCountByArtistMax", ratingsCountByArtistMax);

      }
    }

    Map<String, String> tabs = new LinkedHashMap<>();
    tabs.put("statistics", "Statystyki");
    tabs.put("recordings", "Oceny utworów");
    tabs.put("albums", "Oceny albumów");
    tabs.put("artists", "Oceny artystów");

    model.addAttribute("tabs", tabs);
    model.addAttribute("page", page);
    model.addAttribute("tab", tab);

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

  @GetMapping("/user/find-friends")
  public String findFriends(final Model model,
      @RequestParam(required = false) final String query,
      @RequestParam(required = false) final Integer page) {

    if (query == null) {
      model.addAttribute("query", null);
      return MvcView.FIND_FRIENDS.get();
    }

    if (page == null || page < 1) {
      model.addAttribute("query", query);

      return "redirect:/user/find-friends?query=" + query + "&page=1";
    }

    final List<User> users = userRepository.searchByUsername(query.toLowerCase(), PageRequest.of(page - 1, 10));
    model.addAttribute("users", users);
    model.addAttribute("query", query);
    model.addAttribute("page", page);

    System.out.println(query);
    System.out.println(Arrays.deepToString(users.toArray()));

    return MvcView.FIND_FRIENDS.get();
  }

  public record SendFriendRequest(Long recipientId) {

  }

  public record RespondToFriendRequest(long friendRequestId, boolean accept) {

  }

  public record CancelFriendRequest(long friendRequestId) {

  }

  private <T extends RateableEntity> void prepareModelWithRatings(final User userProfile, final Model model,
      final Principal principal, final int page, final Class<T> clazz) {

    final PageRequest pageRequest = PageRequest.of(page - 1, 10);
    final List<Rating> userProfileRatings = ratingRepository.findByUserOrderByDateDesc(userProfile, clazz, pageRequest);

    final List<T> entities = userProfileRatings.stream()
        .map(Rating::getEntity)
        .filter(entity -> clazz.isAssignableFrom(entity.getClass()))
        .map(clazz::cast)
        .collect(Collectors.toList());

    model.addAttribute(clazz.getSimpleName().toLowerCase() + "s", entities);

    model.addAttribute("userProfileRatings", new UserRatingsContainer(userProfileRatings));
    model.addAttribute("userRatings", createUserRatingsContainer(principal, entities));

    final long totalCount = ratingRepository.countRatingsByUser(userProfile, clazz);
    model.addAttribute("totalCount", totalCount);
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
