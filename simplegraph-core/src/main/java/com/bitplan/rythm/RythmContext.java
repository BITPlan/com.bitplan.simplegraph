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
package com.bitplan.rythm;

import static org.rythmengine.conf.RythmConfigurationKey.HOME_TEMPLATE;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.rythmengine.RythmEngine;
import org.rythmengine.conf.RythmConfigurationKey;

/**
 * Rythm Context
 * 
 * @author wf
 *
 */
public class RythmContext {
  protected RythmEngine engine;
  Map<String, Object> conf = new HashMap<String, Object>();
  File templateRoot;

  /**
   * set the template Root
   * 
   * @see http://rythmengine.org/doc/configuration.md#home_template_dir
   * @param path
   */
  public void setTemplateRoot(String path) {
    Object currentFile = conf.get(RythmConfigurationKey.HOME_TEMPLATE.getKey());
    // avoid resetting the engine if the path doesn't change
    if (currentFile != null && currentFile.equals(templateRoot)) {
      return;
    }
    System.getProperties().remove(HOME_TEMPLATE.getKey());
    templateRoot = new File(path);
    conf.put(RythmConfigurationKey.HOME_TEMPLATE.getKey(), templateRoot);
    engine = null;
    getEngine();
  }

  /**
   * get the Rythm engine
   * 
   * @return
   */
  public RythmEngine getEngine() {
    if (engine == null) {
      conf.put("codegen.compact.enabled", false);
      engine = new RythmEngine(conf);
    }
    return engine;
  }
  
 

  /**
   * render a node
   * 
   * @param template
   * @param node
   * @param props
   * @return the resulting string
   * @throws Exception
   */
  public String render(File template, Vertex node, String... props)
      throws Exception {
    Map<String, Object> rootMap = new HashMap<String, Object>();
    // is the property number not even?
    // that is not allowed!
    if (props.length % 2 != 0)
      throw new IllegalArgumentException(
          "property names have to be pairs for mapping but found odd "
              + props.length + " number of props");
    // if there is a property mapping
    // use it
    if (props.length > 0) {
      for (int i = 0; i < props.length; i += 2) {
        String src = props[i];
        String target = props[i + 1];
        if (node.property(src).isPresent())
          rootMap.put(target, node.property(src).value());
      }
    } else {
      // else use all properties
      node.properties().forEachRemaining(prop->{
        rootMap.put(prop.label(), prop.value());
      });
    }
    String result = render(template, rootMap);
    return result;
  }

  /**
   * render the given rootMap with the given File
   * 
   * @param template
   * @param rootMap
   * @return
   * @throws Exception
   */
  public String render(File template, Map<String, Object> rootMap)
      throws Exception {
    RythmEngine engine = getEngine();
    String result = engine.render(template, rootMap);
    return result;
  }
  
  /**
   * render the given rootMap with the given template from the given URL
   * 
   * @param templateURL
   * @param rootMap
   * @return the rendered result
   * @throws Exception
   */
  public String render(URL templateURL, Map<String, Object> rootMap)
      throws Exception {
    RythmEngine engine = getEngine();
    String template=IOUtils.toString(templateURL,"UTF-8");
    String result = engine.render(template, rootMap);
    return result;
  }
  

  /**
   * render with the given template
   * 
   * @param template
   * @param rootMap
   * @return
   * @throws Exception
   */
  public String render(String template, Map<String, Object> rootMap)
      throws Exception {
    RythmEngine engine = getEngine();
    String result = engine.render(template, rootMap);
    return result;
  }

  /***
   * enforce singleton
   */
  protected RythmContext() {

  }

}
