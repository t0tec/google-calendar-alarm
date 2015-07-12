package org.olmec.ui_mvc.model;

import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * @author t0tec (t0tec.olmec@gmail.com)
 * @version $Id$
 * @since 1.0
 */
@Singleton
public class OverviewModel {

  private ObservableList<EventTO> events = FXCollections.observableArrayList(EventTO.extractor());

  private List<Runnable> eventObservers = new ArrayList<>();

  private List<Consumer<EventTO>> selectedEventObservers = new ArrayList<>();

  public ObservableList<EventTO> getEvents() {
    return events;
  }

  public void selectEvent(EventTO event) {
    for (Consumer<EventTO> observer : this.selectedEventObservers) {
      observer.accept(event);
    }
  }

  public void setEvents(ObservableList<EventTO> events) {
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
}
