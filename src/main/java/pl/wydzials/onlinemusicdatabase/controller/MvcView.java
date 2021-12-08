package pl.wydzials.onlinemusicdatabase.controller;

public enum MvcView {
  INDEX("index"),
  LOGIN("login"),
  REGISTER("register"),
  SETTINGS("user/settings"),
  CHANGE_PASSWORD("user/change-password"),
  PLAYLISTS("user/playlists"),
  ALBUM("album"),
  ARTIST("artist"),
  ARTISTS("artists"),
  SEARCH("search"),
  PROFILE("profile");

  private final String path;

  MvcView(final String path) {
    this.path = path;
  }

  public String get() {
    System.out.println("Template: " + path);
    return path;
  }
}
