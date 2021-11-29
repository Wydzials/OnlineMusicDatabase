package pl.wydzials.onlinemusicdatabase.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ConfigurationProvider {

  private static boolean isGeneratingData = false;

  public static LocalDate getCurrentDate() {
    return LocalDate.now();
  }

  public static LocalDateTime getCurrentDateTime() {
    return LocalDateTime.now();
  }

  public static boolean isIsGeneratingData() {
    return isGeneratingData;
  }

  public static void setIsGeneratingData(final boolean isGeneratingData) {
    ConfigurationProvider.isGeneratingData = isGeneratingData;
  }
}
