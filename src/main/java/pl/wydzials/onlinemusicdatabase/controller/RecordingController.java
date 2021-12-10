package pl.wydzials.onlinemusicdatabase.controller;

import java.security.Principal;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.wydzials.onlinemusicdatabase.model.Recording;
import pl.wydzials.onlinemusicdatabase.repository.RecordingRepository;

@Controller
public class RecordingController extends BaseController {

  private final RecordingRepository recordingRepository;

  public RecordingController(final RecordingRepository recordingRepository) {
    this.recordingRepository = recordingRepository;
  }

  @GetMapping("/recordings")
  public String getRecordings(final Principal principal, final Model model, final RedirectAttributes redirectAttributes,
      @RequestParam(required = false) String minRatings,
      @RequestParam(required = false) String maxRatings) {

    final int minRatingsFilter = parseToInteger(minRatings).orElse(0);
    final int maxRatingsFilter = parseToInteger(maxRatings).orElse(Integer.MAX_VALUE);

    if (minRatingsFilter > maxRatingsFilter) {
      addFlashMessage(redirectAttributes, "Minimalna liczba ocen nie może być większa od maksymalnej liczby ocen.");
      return "redirect:/recordings";
    }
    final List<Recording> topRecordings = recordingRepository.findTopRecordings(minRatingsFilter, maxRatingsFilter,
        PageRequest.of(0, 10));

    model.addAttribute("topRecordings", topRecordings);
    model.addAttribute("userRatings", createUserRatingsContainer(principal, topRecordings));

    return MvcView.RECORDINGS.get();
  }
}
