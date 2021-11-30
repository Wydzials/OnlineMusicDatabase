package pl.wydzials.onlinemusicdatabase.controller;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.wydzials.onlinemusicdatabase.model.Artist;
import pl.wydzials.onlinemusicdatabase.model.Artist.ArtistType;
import pl.wydzials.onlinemusicdatabase.repository.ArtistRepository;
import pl.wydzials.onlinemusicdatabase.repository.RatingRepository;
import pl.wydzials.onlinemusicdatabase.utils.Validation;

@Controller
public class ArtistController extends BaseController {

  private final ArtistRepository artistRepository;
  private final RatingRepository ratingRepository;

  public ArtistController(final ArtistRepository artistRepository,
      final RatingRepository ratingRepository) {
    this.artistRepository = artistRepository;
    this.ratingRepository = ratingRepository;
  }

  @GetMapping("/artist/{id}")
  public String getArtist(@PathVariable final Long id, final Principal principal, final Model model) {
    Validation.notNull(id);

    final Artist artist = artistRepository.findById(id).orElseThrow();

    final UserRatingsContainer userRatingsContainer = createUserRatingsContainer(principal,
        artist.getSingleRecordings(),
        artist.getAlbums(),
        Collections.singleton(artist));

    model.addAttribute("artist", artist);
    model.addAttribute("userRatings", userRatingsContainer);

    return MvcView.ARTIST.get();
  }

  @GetMapping("/artists")
  public String getArtists(final Principal principal, final Model model, final RedirectAttributes redirectAttributes,
      @RequestParam(required = false) String artistType,
      @RequestParam(required = false) String minRatings,
      @RequestParam(required = false) String maxRatings) {

    final Integer minRatingsFilter = parseToInteger(minRatings).orElse(0);
    final Integer maxRatingsFilter = parseToInteger(maxRatings).orElse(Integer.MAX_VALUE);
    final ArtistType artistTypeFilter = ArtistType.of(artistType).orElse(null);

    if (minRatingsFilter > maxRatingsFilter) {
      addFlashMessage(redirectAttributes, "Minimalna liczba ocen nie może być większa od maksymalnej liczby ocen");
      return "redirect:/artists?artistType=" + artistType;
    }

    final List<Artist> topArtists = artistRepository.findTopArtists(10, artistTypeFilter,
        minRatingsFilter, maxRatingsFilter);

    model.addAttribute("topArtists", topArtists);
    model.addAttribute("userRatings", createUserRatingsContainer(principal, topArtists));

    return MvcView.ARTISTS.get();
  }

  private Optional<Integer> parseToInteger(final String value) {
    try {
      return Optional.of(Integer.parseInt(value));
    } catch (NumberFormatException e) {
      return Optional.empty();
    }
  }
}
