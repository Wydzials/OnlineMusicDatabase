package pl.wydzials.onlinemusicdatabase.controller;

import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.wydzials.onlinemusicdatabase.model.Artist;
import pl.wydzials.onlinemusicdatabase.model.Artist.ArtistType;
import pl.wydzials.onlinemusicdatabase.model.Artist.Genre;
import pl.wydzials.onlinemusicdatabase.repository.ArtistRepository;
import pl.wydzials.onlinemusicdatabase.utils.Validation;

@Controller
public class ArtistController extends BaseController {

  private final ArtistRepository artistRepository;

  public ArtistController(final ArtistRepository artistRepository) {
    this.artistRepository = artistRepository;
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
      @RequestParam(required = false) String genre,
      @RequestParam(required = false) String minRatings,
      @RequestParam(required = false) String maxRatings) {

    final ArtistType artistTypeFilter = ArtistType.of(artistType).orElse(null);
    final Genre genreFilter = Genre.of(genre).orElse(null);
    final int minRatingsFilter = parseToInteger(minRatings).orElse(0);
    final int maxRatingsFilter = parseToInteger(maxRatings).orElse(Integer.MAX_VALUE);

    if (minRatingsFilter > maxRatingsFilter) {
      addFlashMessage(redirectAttributes, "Minimalna liczba ocen nie może być większa od maksymalnej liczby ocen.");
      return "redirect:/artists?artistType=" + artistType;
    }

    final Set<ArtistType> artistTypes;
    if (artistTypeFilter == null) {
      artistTypes = Set.of(ArtistType.BAND, ArtistType.PERSON);
    } else {
      artistTypes = Set.of(artistTypeFilter);
    }

    final Set<Genre> genres;
    if (genreFilter == null) {
      genres = new HashSet<>(Arrays.asList(Genre.values()));
    } else {
      genres = Set.of(genreFilter);
    }

    final List<Artist> topArtists = artistRepository.findTopArtists(artistTypes, genres,
        minRatingsFilter, maxRatingsFilter, PageRequest.of(0, 10));

    model.addAttribute("topArtists", topArtists);
    model.addAttribute("userRatings", createUserRatingsContainer(principal, topArtists));
    model.addAttribute("genres", Genre.values());

    return MvcView.ARTISTS.get();
  }
}
