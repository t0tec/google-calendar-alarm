package org.olmec.ui_mvc.model;

import com.google.api.services.calendar.model.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * @author t0tec (t0tec.olmec@gmail.com)
 * @version $Id$
 * @since 1.0
 */
public class EventModel {

  private Event event;

  private List<Runnable> observers = new ArrayList<>();

  public Event getEvent() {
    return this.event;
  }

  public void setEvent(Event event) {
    this.event = event;
    for (Runnable runnable : this.observers) {
      runnable.run();
    }
  }

  public void addChangeObserver(Runnable observer) {
    this.observers.add(observer);
  }
}
