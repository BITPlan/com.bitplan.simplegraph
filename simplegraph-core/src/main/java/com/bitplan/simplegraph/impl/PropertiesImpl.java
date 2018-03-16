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
