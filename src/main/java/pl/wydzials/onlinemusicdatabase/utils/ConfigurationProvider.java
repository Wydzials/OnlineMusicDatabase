package pl.wydzials.onlinemusicdatabase.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ConfigurationProvider {

  public static LocalDate getCurrentDate() {
    return LocalDate.now();
  }

  public static LocalDateTime getCurrentDateTime() {
    return LocalDateTime.now();
  }
}
