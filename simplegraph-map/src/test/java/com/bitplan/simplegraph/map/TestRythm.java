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
package com.bitplan.simplegraph.map;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.logging.Logger;

import org.junit.Test;

import com.bitplan.rythm.RythmContext;
import com.bitplan.simplegraph.map.MapSystem;

/**
 * test the rythm template engine
 * 
 * @author wf
 *
 */
public class TestRythm  {
  public static boolean debug = false;
  protected static Logger LOGGER = Logger.getLogger("com.bitplan.map");
  
  @Test
  public void testGenerateFromVertex() throws Exception {
    // get us a Rythm context to be able to render via a template
    RythmContext rythmContext = RythmContext.getInstance();
    // choose a Rythm template that will work on our graph
    File template = new File("../simplegraph-map/src/main/rythm/carmaphtml.rythm");
    MapSystem ms = TestMapSystem.getCarMapSystem();
    String html=rythmContext.render(template, ms.getStartNode().getVertex());
    // debug=true;
    if (debug)
      System.out.println(html);
    assertTrue(html.contains("<a href='https://www.wikidata.org/wiki/Q27586"));
  }
}
