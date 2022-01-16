package pl.wydzials.onlinemusicdatabase.utils;

import java.util.List;
import org.apache.commons.math3.util.FastMath;

public class SecurityUtils {

  public static double getPasswordEntropy(final String password) {
    Validation.notNull(password);

    final String lowercaseLetters = "abcdefghijklmnopqrstuvwxyz";
    final String uppercaseLetters = lowercaseLetters.toUpperCase();
    final String digits = "0123456789";
    final String specialCharacters = "!\"#$%&'()*+,-./:;<=>?@[]^_`{|}~";

    final List<String> characterPools = List.of(lowercaseLetters, uppercaseLetters, digits, specialCharacters);

    int totalPool = 0;
    for (final String pool : characterPools) {
      for (final char c : password.toCharArray()) {
        if (pool.contains(String.valueOf(c))) {
          totalPool += pool.length();
          System.out.println(pool.length());
          break;
        }
      }
    }
    System.out.println(totalPool);

    return password.length() * FastMath.log(2, totalPool);
  }
}
