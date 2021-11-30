package pl.wydzials.onlinemusicdatabase.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class GlobalConfiguration {

  private static boolean isGeneratingData = false;

  public static LocalDate getCurrentDate() {
    return LocalDate.now();
  }

  public static LocalTime getCurrentTime() {
    return LocalTime.now();
  }

  public static LocalDateTime getCurrentDateTime() {
    return LocalDateTime.now();
  }

  public static boolean isIsGeneratingData() {
    return isGeneratingData;
  }

  public static void setIsGeneratingData(final boolean isGeneratingData) {
    GlobalConfiguration.isGeneratingData = isGeneratingData;
  }
}
