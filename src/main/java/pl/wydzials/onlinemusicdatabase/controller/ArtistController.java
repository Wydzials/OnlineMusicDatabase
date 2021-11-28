package pl.wydzials.onlinemusicdatabase.controller;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pl.wydzials.onlinemusicdatabase.model.Artist;
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
  public String getArtists(final Principal principal, final Model model) {
    final List<Artist> topArtists = artistRepository.findTopArtists(10);

    model.addAttribute("topArtists", topArtists);
    model.addAttribute("userRatings", createUserRatingsContainer(principal, topArtists));

    return MvcView.ARTISTS.get();
  }
}
