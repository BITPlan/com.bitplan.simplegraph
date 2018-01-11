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
package com.bitplan.simplegraph.impl;

/**
 * in the current structure we do default implementations that might end up in 
 * endless recursions if we do not supply some implementation. So throwing this exception
 * is better than getting a stackoverflow.
 * @author wf
 *
 */
public class PseudoAbstractCallException extends IllegalStateException {
  /**
   * 
   */
  private static final long serialVersionUID = 4608589991938688494L;

  public PseudoAbstractCallException(String msg) {
    super("I am pseudo abstract - you tried to call me with param "+msg+" but I should be  overridden somewhere");
  }
}
