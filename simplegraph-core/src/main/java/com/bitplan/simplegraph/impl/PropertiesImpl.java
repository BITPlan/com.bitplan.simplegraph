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

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * property file handler
 * 
 * @author wf
 *
 */
public class PropertiesImpl {
  protected static Logger LOGGER = Logger
      .getLogger("com.bitplan.simplegraph.impl");

  /**
   * get the path to an initialization files
   * 
   * @return the path
   */
  public static File getPropertyFile(String propertyKind) {
    File iniPath = new File(
        System.getProperty("user.home") + "/." + propertyKind);
    return iniPath;
  }

  private Properties properties;

  public Object getProperty(String propertyName) {
    return properties.getProperty(propertyName);
  }
  /**
   * get the Properties for the given propertyKind
   * @param propertyKind
   */
  public PropertiesImpl(String propertyKind) {
    File propertyFile = getPropertyFile(propertyKind);
    try {
      properties = new Properties();
      properties.load(new FileInputStream(propertyFile));
    } catch (Exception e) {
      LOGGER.log(Level.WARNING, "error " + e.getMessage() + " for propertyFile "
          + propertyFile.getAbsolutePath());
    }
  }
}
