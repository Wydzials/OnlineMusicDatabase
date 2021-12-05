package pl.wydzials.onlinemusicdatabase.controller;

import java.security.Principal;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.wydzials.onlinemusicdatabase.model.Playlist;
import pl.wydzials.onlinemusicdatabase.model.Recording;
import pl.wydzials.onlinemusicdatabase.model.User;
import pl.wydzials.onlinemusicdatabase.repository.PlaylistRepository;
import pl.wydzials.onlinemusicdatabase.repository.RecordingRepository;
import pl.wydzials.onlinemusicdatabase.repository.UserRepository;
import pl.wydzials.onlinemusicdatabase.utils.Validation;

@Controller
@Transactional
public class PlaylistController extends BaseController {

  private final PlaylistRepository playlistRepository;
  private final UserRepository userRepository;
  private final RecordingRepository recordingRepository;

  public PlaylistController(final PlaylistRepository playlistRepository, final UserRepository userRepository,
      final RecordingRepository recordingRepository) {
    this.playlistRepository = playlistRepository;
    this.userRepository = userRepository;
    this.recordingRepository = recordingRepository;
  }

  @GetMapping("/user/playlists")
  public String getPlaylists(final Principal principal, final Model model,
      @RequestParam(required = false) Long activePlaylistId) {
    Validation.notNull(principal);

    model.addAttribute("activePlaylist", null);

    if (activePlaylistId != null) {
      final Playlist activePlaylist = playlistRepository.findById(activePlaylistId).orElseThrow();
      model.addAttribute("activePlaylist", activePlaylist);
    }

    return MvcView.PLAYLISTS.get();
  }

  @PostMapping("/user/playlist")
  public String createPlaylist(final CreatePlaylistRequest request, final Principal principal,
      final HttpServletRequest httpServletRequest) {
    Validation.notNull(request);
    Validation.notNull(principal);
    request.validate();

    final User user = userRepository.findByUsername(principal.getName()).orElseThrow();

    user.createPlaylist(request.name());
    return redirectToReferrer(httpServletRequest);
  }

  public record CreatePlaylistRequest(String name) {

    public void validate() {
      Validation.notEmpty(name);
    }
  }

  @PostMapping("/user/playlist/delete/{id}")
  public String deletePlaylist(@PathVariable final Long id, final Principal principal,
      final RedirectAttributes redirectAttributes) {
    Validation.notNull(id);
    Validation.notNull(principal);

    final User user = userRepository.findByUsername(principal.getName()).orElseThrow();
    final Playlist playlist = playlistRepository.findById(id).orElseThrow();

    if (playlist.isUserEqualTo(user)) {
      user.deletePlaylist(playlist);
    } else {
      addFlashMessage(redirectAttributes, "Brak uprawnień!");
    }

    return "redirect:/user/playlists";
  }

  @PostMapping("/user/playlist/add-recording")
  public String addToPlaylist(final AddOrRemoveFromPlaylistRequest request, final Principal principal,
      final RedirectAttributes redirectAttributes, final HttpServletRequest httpServletRequest) {
    Validation.notNull(request);
    Validation.notNull(principal);

    if (!request.isValid()) {
      addFlashMessage(redirectAttributes, "Nie udało się dodać utworu do playlisty.");
      return redirectToReferrer(httpServletRequest);
    }

    final User user = userRepository.findByUsername(principal.getName()).orElseThrow();
    final Playlist playlist = playlistRepository.findById(request.playlistId()).orElseThrow();
    final Recording recording = recordingRepository.findById(request.recordingId()).orElseThrow();

    if (playlist.isUserEqualTo(user)) {
      if (playlist.contains(recording)) {
        addFlashMessage(redirectAttributes, "Utwór znajdował się już na tej playliście.");
        return redirectToReferrer(httpServletRequest);
      }

      playlist.addRecording(recording);
      addFlashMessage(redirectAttributes, "Utwór '" + recording.getTitle() + "' został dodany do playlisty.");
    } else {
      addFlashMessage(redirectAttributes, "Brak uprawnień!");
    }

    return redirectToReferrer(httpServletRequest);
  }

  @PostMapping("/user/playlist/delete-recording")
  public String deleteFromPlaylist(final AddOrRemoveFromPlaylistRequest request, final Principal principal,
      final RedirectAttributes redirectAttributes, final HttpServletRequest httpServletRequest) {
    Validation.notNull(request);
    Validation.notNull(principal);

    if (!request.isValid()) {
      addFlashMessage(redirectAttributes, "Nie udało się usunąć utworu z playlisty.");
      return redirectToReferrer(httpServletRequest);
    }

    final User user = userRepository.findByUsername(principal.getName()).orElseThrow();
    final Playlist playlist = playlistRepository.findById(request.playlistId()).orElseThrow();
    final Recording recording = recordingRepository.findById(request.recordingId()).orElseThrow();

    if (playlist.isUserEqualTo(user)) {
      if (!playlist.contains(recording)) {
        addFlashMessage(redirectAttributes, "Utwór nie znajdował się na tej playliście.");
        return redirectToReferrer(httpServletRequest);
      }

      playlist.deleteRecording(recording);
      addFlashMessage(redirectAttributes, "Utwór '" + recording.getTitle() + "' został usunięty z playlisty.");
    } else {
      addFlashMessage(redirectAttributes, "Brak uprawnień!");
    }

    return "redirect:/user/playlists?activePlaylistId=" + playlist.getId();
  }

  public record AddOrRemoveFromPlaylistRequest(Long playlistId, Long recordingId) {

    public boolean isValid() {
      return playlistId != null && recordingId != null;
    }
  }
}
