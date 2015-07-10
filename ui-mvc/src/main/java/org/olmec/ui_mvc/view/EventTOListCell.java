package org.olmec.ui_mvc.view;

import org.olmec.ui_mvc.model.EventTO;

import javafx.scene.control.ListCell;

/**
 * @author t0tec (t0tec.olmec@gmail.com)
 * @version $Id$
 * @since 1.0
 */
public class EventTOListCell extends ListCell<EventTO> {

  @Override
  public void updateItem(EventTO item, boolean empty) {
    super.updateItem(item, empty);
    if (!empty && (item != null)) {
      setText(String.format("%s: %s - %s", item.getSummary(), item.getStart().toString(
          "dd/MM/yyyy HH:mm:ss"), item.getEnd().toString("dd/MM/yyyy HH:mm:ss")));
    } else {
      setText(null);
    }
  }
}
