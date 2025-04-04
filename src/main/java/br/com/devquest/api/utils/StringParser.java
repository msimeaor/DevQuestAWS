package br.com.devquest.api.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringParser {

  private StringParser() {}

  public static String getContentBetweenFlags(String text, String flag1, String flag2) {
    int start = text.indexOf(flag1) + flag1.length();
    int end;

    if (flag2 != null) {
      end = text.indexOf(flag2);
      return text.substring(start, end).trim();
    }

    return text.substring(start).trim();
  }

  public static List<String> getEnumeratorBetweenFlags(String text, String flag1, String flag2) {
    String enumerationString = "";
    int start = text.indexOf(flag1) + flag1.length();
    int end;

    if (flag2 != null) {
      end = text.indexOf(flag2);
      enumerationString = text.substring(start, end).trim();
    } else {
      enumerationString = text.substring(start).trim();
    }

    return new ArrayList<>(Arrays.asList(enumerationString.split("\\n")));
  }

  public static String[] getArrayWithEnumeratorIndicatorAndText(String enumerator, String flag) {
    return enumerator.split(flag, 2);
  }

}
