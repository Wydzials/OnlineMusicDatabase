package pl.wydzials.onlinemusicdatabase.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController extends BaseController {

  @GetMapping("/")
  public String get() {
    return MvcView.INDEX.get();
  }
}
