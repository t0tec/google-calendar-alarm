package org.olmec.ui_mvc.model;

import com.google.api.services.calendar.model.Event;
import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author t0tec (t0tec.olmec@gmail.com)
 * @version $Id$
 * @since 1.0
 */
@Singleton
public class MainModel {

  private List<Event> events = new ArrayList<>();

  private List<Runnable> eventObservers = new ArrayList<>();

  private List<Consumer<Event>> selectedEventObservers = new ArrayList<>();

  public List<Event> getEvents() {
    return events;
  }

  public void selectEvent(Event event) {
    for (Consumer<Event> observer : this.selectedEventObservers) {
      observer.accept(event);
    }
  }

  public void setEvents(List<Event> events) {
    this.events = events;
    for (Runnable runnable : this.eventObservers) {
      runnable.run();
    }
  }

  public void addEventsChangeObserver(Runnable observer) {
    this.eventObservers.add(observer);
  }

  public void removeEventsChangeObserver(Runnable observer) {
    this.eventObservers.remove(observer);
  }

  public void addSelectedEventObserver(Consumer<Event> observer) {
    this.selectedEventObservers.add(observer);
  }

  public void removeSelectedEventObserver(Consumer<Event> observer) {
    this.selectedEventObservers.remove(observer);
  }
}
