package pl.wydzials.onlinemusicdatabase.controller;

import java.security.Principal;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.wydzials.onlinemusicdatabase.model.Album;
import pl.wydzials.onlinemusicdatabase.repository.AlbumRepository;
import pl.wydzials.onlinemusicdatabase.repository.RatingRepository;
import pl.wydzials.onlinemusicdatabase.utils.Validation;

@Controller
public class AlbumController extends BaseController {

  private final AlbumRepository albumRepository;
  private final RatingRepository ratingRepository;

  public AlbumController(final AlbumRepository albumRepository,
      final RatingRepository ratingRepository) {
    this.albumRepository = albumRepository;
    this.ratingRepository = ratingRepository;
  }

  @GetMapping("/album/{id}")
  public String getAlbum(@PathVariable final Long id, final Principal principal, final Model model) {
    Validation.notNull(id);

    final Album album = albumRepository.findAlbumById(id).orElseThrow();

    model.addAttribute("album", album);
    model.addAttribute("userRatings", createUserRatingsContainer(principal, album.getAllRateableEntities()));

    return MvcView.ALBUM.get();
  }

  @GetMapping("/albums")
  public String getAlbums(final Principal principal, final Model model, final RedirectAttributes redirectAttributes,
      @RequestParam(required = false) String minRatings,
      @RequestParam(required = false) String maxRatings,
      @RequestParam(required = false) String minYear,
      @RequestParam(required = false) String maxYear) {

    final int minRatingsFilter = parseToInteger(minRatings).orElse(0);
    final int maxRatingsFilter = parseToInteger(maxRatings).orElse(Integer.MAX_VALUE);

    if (minRatingsFilter > maxRatingsFilter) {
      addFlashMessage(redirectAttributes, "Minimalna liczba ocen nie może być większa od maksymalnej liczby ocen.");
      return "redirect:/albums?minYear=" + minYear + "&maxYear=" + maxYear;
    }

    final int minYearFilter = parseToInteger(minYear).orElse(0);
    final int maxYearFilter = parseToInteger(maxYear).orElse(3000);

    if (minYearFilter > maxYearFilter) {
      addFlashMessage(redirectAttributes, "Minimalny rok wydania nie może być większy od maksymalnego roku.");
      return "redirect:/albums?minRatings=" + minRatings + "&maxRatings=" + maxRatings;
    }

    final List<Album> topAlbums = albumRepository.findTopAlbums(minRatingsFilter, maxRatingsFilter,
        minYearFilter, maxYearFilter, PageRequest.of(0, 10));

    model.addAttribute("topAlbums", topAlbums);
    model.addAttribute("userRatings", createUserRatingsContainer(principal, topAlbums));

    return MvcView.ALBUMS.get();
  }
}
