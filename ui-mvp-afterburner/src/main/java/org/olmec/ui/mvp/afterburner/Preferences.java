package org.olmec.ui.mvp.afterburner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author t0tec (t0tec.olmec@gmail.com)
 * @version $Id$
 * @since 1.0
 */
public class Preferences {

  private static final Logger logger = LoggerFactory.getLogger(Preferences.class);

  private static Preferences instance;

  private Properties preferences;

  private File file;

  private Preferences() {
    try {
      preferences = new Properties();
      file = new File("preferences.properties");
      if (file.exists()) {
        FileInputStream fileInStream = new FileInputStream(file);
        preferences.load(fileInStream);
        fileInStream.close();
      }
    } catch (IOException io) {
      logger.error("IOException: " + io.getMessage());
    }
  }

  public static synchronized Preferences getInstance() {
    if (instance == null) {
      instance = new Preferences();
    }
    return instance;
  }

  public String getValue(String key) {
    return this.preferences.getProperty(key);
  }

  public boolean exists() {
    return file.exists();
  }
}
