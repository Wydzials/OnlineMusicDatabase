package pl.wydzials.onlinemusicdatabase.controller;

public enum MvcView {
  ALBUM("album"),
  ARTIST("artist"),
  INDEX("index"),
  LOGIN("login"),
  REGISTER("register"),
  SETTINGS("user/settings"),
  CHANGE_PASSWORD("user/change-password"),
  ARTISTS("artists");

  private final String path;

  MvcView(final String path) {
    this.path = path;
  }

  public String get() {
    System.out.println("Template: " + path);
    return path;
  }
}