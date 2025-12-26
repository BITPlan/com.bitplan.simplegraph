/**
 * Copyright (c) 2018-2025 BITPlan GmbH
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

import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.PBiPredicate;

// https://groups.google.com/forum/#!topic/gremlin-users/heWLwz9xBQc
// https://stackoverflow.com/a/45652897/1497139
public class ToStringEqualsPredicate implements PBiPredicate<Object, Object> {

  public ToStringEqualsPredicate() {
  }

  @Override
	public boolean test(final Object first, final Object second) {
		return first.toString().equals(second.toString());
	}

	@Override
	public PBiPredicate<Object, Object> negate() {
		return new PBiPredicate<Object, Object>() {
			@Override
			public boolean test(Object o1, Object o2) {
				return !ToStringEqualsPredicate.this.test(o1, o2);
			}

			@Override
			public String getPredicateName() {
				return "not(" + ToStringEqualsPredicate.this.getPredicateName() + ")";
			}
		};
	}

	@Override
	public String getPredicateName() {
		return "toStringEquals";
	}

	/**
	 * get a .toString() comparison predicate
	 * 
	 * @param compare
	 * @return - the predicate
	 */
	public static P<Object> compare(String compare) {
		PBiPredicate<Object, Object> b = new ToStringEqualsPredicate();
		return new P<Object>(b, compare);
	}
}