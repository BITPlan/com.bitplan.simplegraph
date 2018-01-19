/**
 * Copyright (c) 2018 BITPlan GmbH
 *
 * http://www.bitplan.com
 *
 * This file is part of the Opensource project at:
 * https://github.com/BITPlan/com.bitplan.simplegraph
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
