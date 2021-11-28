package pl.wydzials.onlinemusicdatabase.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pl.wydzials.onlinemusicdatabase.model.Artist;
import pl.wydzials.onlinemusicdatabase.model.RateableEntity;
import pl.wydzials.onlinemusicdatabase.model.Rating;
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

    final Set<RateableEntity> rateableEntities = new HashSet<>(artist.getSingleRecordings());
    rateableEntities.addAll(artist.getAlbums());
    rateableEntities.add(artist);

    final List<Rating> ratings;
    if (principal != null) {
      ratings = ratingRepository.findByUsernameAndEntities(principal.getName(), rateableEntities);
    } else {
      ratings = new ArrayList<>();
    }

    model.addAttribute("artist", artist);
    model.addAttribute("userRatings", new UserRatingsContainer(ratings));

    return MvcView.ARTIST.get();
  }

  @GetMapping("/artists")
  public String getArtists(final Principal principal, final Model model) {
    final List<Artist> topArtists = artistRepository.findTopArtists(10);

    final List<Rating> ratings;
    if (principal != null) {
      ratings = ratingRepository.findByUsernameAndEntities(principal.getName(), new HashSet<>(topArtists));
    } else {
      ratings = new ArrayList<>();
    }

    model.addAttribute("topArtists", topArtists);
    model.addAttribute("userRatings", new UserRatingsContainer(ratings));

    return MvcView.ARTISTS.get();
  }
}
