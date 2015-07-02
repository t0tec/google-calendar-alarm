package org.olmec.ui.mvp.pv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.applet.AudioClip;
import java.io.IOException;
import java.net.URL;

import javax.swing.*;

/**
 * @author t0tec (t0tec.olmec@gmail.com)
 * @version $Id$
 * @since 1.0
 */
public class Alarm implements Runnable {

  private static final Logger logger = LoggerFactory.getLogger(Alarm.class);

  private volatile String path;

  public Alarm(String path) {
    this.path = path;
  }

  @Override
  public void run() {
    try {
      AudioClip ac = JApplet.newAudioClip(new URL("file://" + path));
      ac.play();
      logger.info("Playing: " + path);
    } catch (IOException io) {
      logger.error("IOException: " + io.getMessage());
    }
  }
}
