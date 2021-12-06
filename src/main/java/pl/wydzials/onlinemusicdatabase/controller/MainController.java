package pl.wydzials.onlinemusicdatabase.controller;

import java.util.List;
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
  public String search(@RequestParam final String query, final Model model) {
    if (query == null) {
      return "redirect:/";
    }

    final List<Recording> recordings = recordingRepository.search(query.toLowerCase());
    final List<Album> albums = albumRepository.search(query.toLowerCase());
    final List<Artist> artists = artistRepository.search(query.toLowerCase());

    model.addAttribute("recordings", recordings);
    model.addAttribute("albums", albums);
    model.addAttribute("artists", artists);
    model.addAttribute("query", query);

    return MvcView.SEARCH.get();
  }
}
