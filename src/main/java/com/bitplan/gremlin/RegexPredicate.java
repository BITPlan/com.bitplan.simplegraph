package com.bitplan.gremlin;

import java.util.function.BiPredicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tinkerpop.gremlin.process.traversal.P;

// https://groups.google.com/forum/#!topic/gremlin-users/heWLwz9xBQc
// https://stackoverflow.com/a/45652897/1497139
public class RegexPredicate implements BiPredicate<Object, Object> {
  Pattern pattern = null;

  public RegexPredicate(String regex) {
    pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
  };

  @Override
  public boolean test(final Object first, final Object second) {
    String str = first.toString();
    Matcher matcher = pattern.matcher(str);
    return matcher.matches();
  }

  /**
   * get a Regular expression predicate
   * @param regex
   * @return - the predicate
   */
  public static P<Object> regex(Object regex) {
    BiPredicate<Object, Object> b = new RegexPredicate(regex.toString());
    return new P<Object>(b, regex);
  }
}
