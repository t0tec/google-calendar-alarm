package org.olmec.ui_mvc;

import java.util.HashMap;

import javafx.scene.Node;
import javafx.scene.layout.StackPane;

/**
 * @author t0tec (t0tec.olmec@gmail.com)
 * @version $Id$
 * @since 1.0
 */
public class Navigator extends StackPane {

  private final HashMap<String, Node> screens;

  public static final String OVERVIEW = "Overview";
  public static final String PREFERENCES = "Preferences";
  public static final String ADD_EVENT = "AddEvent";

  private static Navigator instance;

  private Navigator() {
    screens = new HashMap<String, Node>();
  }

  public static synchronized Navigator getInstance() {
    if (instance == null) {
      instance = new Navigator();
    }
    return instance;
  }

  public void addScreen(String name, Node screen) {
    this.screens.put(name, screen);
  }

  public boolean setScreen(String name) {
    if (screens.containsKey(name)) {
      if (!getChildren().isEmpty()) {
        getChildren().add(0, screens.get(name));
        Node screenToRemove = getChildren().get(1);
        getChildren().remove(screenToRemove);
      } else {
        getChildren().add(screens.get(name));
      }
      return true;
    } else {
      return false;
    }
  }
}
