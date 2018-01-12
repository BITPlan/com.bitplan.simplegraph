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
package com.bitplan.simplegraph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.junit.Test;

import com.bitplan.rythm.RythmContext;

/**
 * test the rythm template engine
 * 
 * @author wf
 *
 */
public class TestRythm extends BaseTest {

  @Test
  public void testRythmFromFile() throws Exception {
    RythmContext rythmContext = RythmContext.getInstance();
    File templateDir = new File("src/main/rythm");
    rythmContext.setTemplateRoot(templateDir.getAbsolutePath());
    // set it again - the engine should not be reconfigured
    rythmContext.setTemplateRoot(templateDir.getAbsolutePath());
    File template=new File(templateDir,"test.rythm");
    Map<String, Object> rootMap = new HashMap<String, Object>();
    rootMap.put("title", "testTitle");
    String result = rythmContext.render(template, rootMap);
    assertEquals(result.trim(), "testTitle");
  }

  @Test
  public void testRythmFromString() throws Exception {
    debug=true;
    RythmContext rythmContext = RythmContext.getInstance();
    String template="@// Rythem template\n" + 
        "@// you can try me out at http://fiddle.rythmengine.com\n" + 
        "@// Created by Wolfgang Fahl, BITPlan GmbH,  2018-01-12\n" + 
        "@args() {\n" + 
        "  String title,int times;\n" + 
        "}\n" + 
        "@for (int i=0;i<times;i++) {\n" + 
        "  @title @i\n" + 
        "}";
    if (debug) {
      // if there is something wrong with the template try it out in the Rythm fiddle
      LOGGER.log(Level.INFO,template);
    }
    Map<String, Object> rootMap = new HashMap<String, Object>();
    rootMap.put("title", "step");
    rootMap.put("times",5);
    String result = rythmContext.render(template, rootMap);
    if (debug)
      LOGGER.log(Level.INFO,result);
    assertTrue(result.contains("step 4"));
  }

}
