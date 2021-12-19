package pl.wydzials.onlinemusicdatabase.controller;

import java.io.IOException;
import java.time.Duration;
import javax.transaction.Transactional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.wydzials.onlinemusicdatabase.model.Album;
import pl.wydzials.onlinemusicdatabase.model.Artist;
import pl.wydzials.onlinemusicdatabase.model.Artist.ArtistType;
import pl.wydzials.onlinemusicdatabase.model.Artist.Genre;
import pl.wydzials.onlinemusicdatabase.repository.AlbumRepository;
import pl.wydzials.onlinemusicdatabase.repository.ArtistRepository;
import pl.wydzials.onlinemusicdatabase.repository.RecordingRepository;
import pl.wydzials.onlinemusicdatabase.utils.ImageStorageService;
import pl.wydzials.onlinemusicdatabase.utils.Validation;

@Controller
@Transactional
@RequestMapping("/admin")
public class AdminController extends BaseController {

  private final RecordingRepository recordingRepository;
  private final AlbumRepository albumRepository;
  private final ArtistRepository artistRepository;
  private final ImageStorageService imageStorageService;

  public AdminController(final RecordingRepository recordingRepository, final AlbumRepository albumRepository,
      final ArtistRepository artistRepository, final ImageStorageService imageStorageService) {
    this.recordingRepository = recordingRepository;
    this.albumRepository = albumRepository;
    this.artistRepository = artistRepository;
    this.imageStorageService = imageStorageService;
  }

  @GetMapping("/add-recording")
  public String getAddRecording() {
    return MvcView.ADD_RECORDING.get();
  }

  @PostMapping("/add-recording")
  public String postAddRecording(final PostAddRecordingRequest request, final RedirectAttributes redirectAttributes) {
    Validation.notNull(request);

    final Album album = albumRepository.findAlbumById(request.albumId()).orElseThrow();
    album.createAlbumRecording(request.title(), request.getDuration(), request.albumPosition());

    addFlashMessage(redirectAttributes, "Utwór został dodany.");
    return "redirect:/album/" + request.albumId();
  }

  @GetMapping("/add-album")
  public String getAddAlbum() {
    return MvcView.ADD_ALBUM.get();
  }

  @PostMapping("/add-album")
  public String postAddAlbum(final PostAddAlbumRequest request, final RedirectAttributes redirectAttributes) {
    Validation.notNull(request);

    final Artist artist = artistRepository.findById(request.artistId()).orElseThrow();
    final Album album = artist.createAlbum(request.name(), request.year());

    try {
      album.addImage(request.image(), imageStorageService);
    } catch (IOException e) {
      addFlashMessage(redirectAttributes, "Nieprawidłowy format zdjęcia.");
      return "redirect:/artist/" + request.artistId();
    }

    addFlashMessage(redirectAttributes, "Album został dodany.");
    return "redirect:/artist/" + request.artistId();
  }

  @GetMapping("/add-artist")
  public String getAddArtist(final Model model) {
    model.addAttribute("genres", Genre.values());
    model.addAttribute("artistTypes", ArtistType.values());

    return MvcView.ADD_ARTIST.get();
  }

  @PostMapping("/add-artist")
  public String postAddArtist(final PostAddArtistRequest request, final RedirectAttributes redirectAttributes) {
    Validation.notNull(request);

    final Artist artist = new Artist(request.name(), request.description(), request.getArtistType(),
        request.getGenre());

    try {
      artist.addImage(request.image(), imageStorageService);
      artistRepository.save(artist);
    } catch (IOException e) {
      addFlashMessage(redirectAttributes, "Nieprawidłowy format zdjęcia.");
      return "redirect:/admin/add-artist";
    }

    addFlashMessage(redirectAttributes, "Artysta został dodany.");
    return "redirect:/artist/" + artist.getId();
  }

  public record PostAddRecordingRequest(long albumId, String title, int minutes, int seconds, int albumPosition) {

    public Duration getDuration() {
      return Duration.ofMinutes(minutes).plusSeconds(seconds);
    }
  }

  public record PostAddAlbumRequest(long artistId, String name, int year, MultipartFile image) {
  }

  public record PostAddArtistRequest(String name, String description, String type, String genre, MultipartFile image) {

    public ArtistType getArtistType() {
      return ArtistType.of(type).orElseThrow();
    }

    public Genre getGenre() {
      return Genre.of(genre).orElseThrow();
    }
  }
}
