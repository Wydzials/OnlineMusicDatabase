package pl.wydzials.onlinemusicdatabase.controller;

import java.security.Principal;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.wydzials.onlinemusicdatabase.model.Playlist;
import pl.wydzials.onlinemusicdatabase.model.User;
import pl.wydzials.onlinemusicdatabase.repository.PlaylistRepository;
import pl.wydzials.onlinemusicdatabase.repository.UserRepository;
import pl.wydzials.onlinemusicdatabase.utils.Validation;

@Controller
public class PlaylistController extends BaseController {

  private final PlaylistRepository playlistRepository;
  private final UserRepository userRepository;

  public PlaylistController(final PlaylistRepository playlistRepository,
      final UserRepository userRepository) {
    this.playlistRepository = playlistRepository;
    this.userRepository = userRepository;
  }

  @GetMapping("/user/playlists")
  public String getPlaylists(final Principal principal, final Model model) {

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
      final HttpServletRequest httpServletRequest, final RedirectAttributes redirectAttributes) {
    Validation.notNull(id);
    Validation.notNull(principal);

    final User user = userRepository.findByUsername(principal.getName()).orElseThrow();
    final Playlist playlist = playlistRepository.findById(id).orElseThrow();

    user.deletePlaylist(playlist, playlistRepository);

    addFlashMessage(redirectAttributes, "Playlista została usunięta");
    return redirectToReferrer(httpServletRequest);
  }
}
