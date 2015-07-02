package org.olmec.ui.mvp.pv.model;

import com.google.api.services.calendar.model.Event;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.olmec.business.GoogleCalendar;
import org.olmec.ui.mvp.pv.Preferences;

import java.util.ArrayList;
import java.util.List;

/**
 * @author t0tec (t0tec.olmec@gmail.com)
 * @version $Id$
 * @since 1.0
 */
@Singleton
public class OverviewModel {

  private List<Event> events = new ArrayList<>();

  private List<Runnable> eventObservers = new ArrayList<>();

  private final GoogleCalendar googleCalendar;

  @Inject
  public OverviewModel(GoogleCalendar googleCalendar){
    this.googleCalendar = googleCalendar;
  }

  public void showEvents() {
    events.clear();

    events = googleCalendar.getEvents(Preferences.getInstance().getValue("googleCalendarId"));

    for (Runnable runnable: eventObservers) {
      runnable.run();
    }
  }

  public List<Event> getEvents() {
    return events;
  }

  public void addEventsChangeObserver(Runnable observer) {
    this.eventObservers.add(observer);
  }

  public void removeEventsChangeObserver(Runnable observer) {
    this.eventObservers.remove(observer);
  }
}
