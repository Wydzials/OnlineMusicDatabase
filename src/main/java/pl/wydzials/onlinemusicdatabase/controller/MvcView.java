package pl.wydzials.onlinemusicdatabase.controller;

public enum MvcView {
  INDEX("index"),
  LOGIN("login"),
  REGISTER("register"),
  SETTINGS("user/settings"),
  CHANGE_PASSWORD("user/change-password"),
  ALBUM("album"),
  ARTIST("artist"),
  ARTISTS("artists"),
  PLAYLISTS("user/playlists");

  private final String path;

  MvcView(final String path) {
    this.path = path;
  }

  public String get() {
    System.out.println("Template: " + path);
    return path;
  }
}
