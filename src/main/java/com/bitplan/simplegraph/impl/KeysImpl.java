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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.bitplan.simplegraph.Keys;


/**
 * default implementation for property keys
 * @author wf
 *
 */
public class KeysImpl implements Keys {
  private List<String> keys;

  /**
   * initialize me from an array of keys
   * @param keys
   */
  public KeysImpl(String... keys) {
    this.keys = Arrays.asList(keys);
  }

  @Override
  public boolean hasKey(String key) {
    if (keys.size() == 0)
      return true;
    else
      return keys.contains(key);
  }

  @Override
  public Optional<List<String>> getKeys() {
    if (keys.size()==0)
      return Optional.empty();
    else
      return Optional.of(keys);
  }
}
