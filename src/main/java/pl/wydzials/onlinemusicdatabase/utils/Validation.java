package pl.wydzials.onlinemusicdatabase.utils;

import java.util.Objects;

public class Validation extends org.apache.commons.lang3.Validate {

  public static void isNull(final Object object) {
    if (Objects.nonNull(object))
      throw new IllegalArgumentException(String.format("The validated object is not null: %s", object));
  }


  public static void throwIllegalArgumentException(final Object... args) {
    throw new IllegalArgumentException(buildMessage(args));
  }

  public static void throwIllegalStateException(final Object... args) {
    throw new IllegalStateException(buildMessage(args));
  }

  private static String buildMessage(final Object... args) {
    final StringBuilder message = new StringBuilder();

    for (final Object arg : args) {
      message.append(arg).append(" ");
    }
    return message.toString();
  }
}

