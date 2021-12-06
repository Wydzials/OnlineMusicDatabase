package pl.wydzials.onlinemusicdatabase.controller;

import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.wydzials.onlinemusicdatabase.model.Album;
import pl.wydzials.onlinemusicdatabase.model.Artist;
import pl.wydzials.onlinemusicdatabase.model.Recording;
import pl.wydzials.onlinemusicdatabase.repository.AlbumRepository;
import pl.wydzials.onlinemusicdatabase.repository.ArtistRepository;
import pl.wydzials.onlinemusicdatabase.repository.RecordingRepository;

@Controller
public class MainController extends BaseController {

  private final RecordingRepository recordingRepository;
  private final AlbumRepository albumRepository;
  private final ArtistRepository artistRepository;

  public MainController(final RecordingRepository recordingRepository, final AlbumRepository albumRepository,
      final ArtistRepository artistRepository) {
    this.recordingRepository = recordingRepository;
    this.albumRepository = albumRepository;
    this.artistRepository = artistRepository;
  }

  @GetMapping("/")
  public String get() {
    return MvcView.INDEX.get();
  }

  @GetMapping("/search")
  public String search(final Model model,
      @RequestParam(required = false) final String query,
      @RequestParam(required = false) final Integer page) {

    if (query == null) {
      return "redirect:/";
    }

    if (page == null || page < 1) {
      return "redirect:/search?query=" + query + "&page=1";
    }

    final List<Recording> recordings = recordingRepository.search(query.toLowerCase(), PageRequest.of(page - 1, 7));
    final List<Album> albums = albumRepository.search(query.toLowerCase(), PageRequest.of(page - 1, 8));
    final List<Artist> artists = artistRepository.search(query.toLowerCase(), PageRequest.of(page - 1, 8));

    model.addAttribute("recordings", recordings);
    model.addAttribute("albums", albums);
    model.addAttribute("artists", artists);
    model.addAttribute("query", query);
    model.addAttribute("page", page);

    return MvcView.SEARCH.get();
  }
}
