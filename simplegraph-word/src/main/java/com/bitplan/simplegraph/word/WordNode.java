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
package com.bitplan.simplegraph.word;

import java.util.Map;

import com.bitplan.simplegraph.core.SimpleGraph;
import com.bitplan.simplegraph.impl.SimpleNodeImpl;

/**
 * wraps a word file
 * @author wf
 *
 */
public class WordNode extends SimpleNodeImpl {

  private Word word;
  private Word97 word97;

  public WordNode(SimpleGraph simpleGraph, String kind, String[] keys) {
    super(simpleGraph, kind, keys);
  }

  public WordNode(WordSystem simpleGraph, Word word, String[] keys) {
    super(simpleGraph,"word",keys);
    this.word=word;
    super.setVertexFromMap();
  }

  public WordNode(WordSystem simpleGraph, Word97 word97, String[] keys) {
    super(simpleGraph,"word",keys);
    this.word97=word97;
    super.setVertexFromMap();
  }

  @Override
  public Map<String, Object> initMap() {
    if (word!=null) {
      map.put("text",word.we.getText());
    } if (word97!=null) {
      map.put("text",word97.we.getText());     
    }
    return map;
  }

}
