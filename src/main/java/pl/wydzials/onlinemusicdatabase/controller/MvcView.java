package pl.wydzials.onlinemusicdatabase.controller;

public enum MvcView {
  INDEX("index"),
  LOGIN("login"),
  REGISTER("register"),
  SETTINGS("user/settings"),
  CHANGE_PASSWORD("user/change-password"),
  PLAYLISTS("user/playlists"),
  FRIEND_REQUESTS("user/friend-requests"),
  FIND_FRIENDS("user/find-friends"),
  ALBUM("album"),
  ARTIST("artist"),
  ARTISTS("artists"),
  ALBUMS("albums"),
  RECORDINGS("recordings"),
  SEARCH("search"),
  PROFILE("profile"),
  NOT_FOUND("not-found");

  private final String path;

  MvcView(final String path) {
    this.path = path;
  }

  public String get() {
    System.out.println("Template: " + path);
    return path;
  }
}
