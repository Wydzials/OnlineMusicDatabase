package pl.wydzials.onlinemusicdatabase.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.wydzials.onlinemusicdatabase.model.Album;
import pl.wydzials.onlinemusicdatabase.model.Rating;
import pl.wydzials.onlinemusicdatabase.repository.AlbumRepository;
import pl.wydzials.onlinemusicdatabase.repository.RatingRepository;
import pl.wydzials.onlinemusicdatabase.utils.Validation;

@Controller
@RequestMapping("/album")
public class AlbumController extends BaseController {

  private final AlbumRepository albumRepository;
  private final RatingRepository ratingRepository;

  public AlbumController(final AlbumRepository albumRepository,
      final RatingRepository ratingRepository) {
    this.albumRepository = albumRepository;
    this.ratingRepository = ratingRepository;
  }

  @GetMapping("{id}")
  public String getAlbum(@PathVariable final Long id, final Principal principal, final Model model) {
    Validation.notNull(id);

    final Album album = albumRepository.findAlbumById(id).orElseThrow();

    final List<Rating> ratings;
    if (principal != null) {
      ratings = ratingRepository.findByUsernameAndEntities(principal.getName(), album.getRecordings());
    } else {
      ratings = new ArrayList<>();
    }

    model.addAttribute("album", album);
    model.addAttribute("userRatings", new UserRatingsContainer(ratings));
    return "album";
  }
}
