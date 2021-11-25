package pl.wydzials.onlinemusicdatabase.utils;

import java.util.Objects;

public class Validation extends org.apache.commons.lang3.Validate {

  public static void isNull(final Object object) {
    if (Objects.nonNull(object))
      throw new IllegalArgumentException(String.format("The validated object is not null: %s", object));
  }

  public static void isFalse(final boolean expression) {
    isTrue(!expression);
  }

  public static void throwIllegalArgumentException() {
    throw new IllegalArgumentException();
  }
}

