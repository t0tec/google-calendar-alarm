package org.olmec.ui_mvc.controller;

import com.google.api.services.calendar.model.Event;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.olmec.business.GoogleCalendar;
import org.olmec.ui_mvc.model.OverviewModel;
import org.olmec.ui_mvc.view.Overview;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author t0tec (t0tec.olmec@gmail.com)
 * @version $Id$
 * @since 1.0
 */
@Singleton
public class OverviewController {

  private final OverviewModel model;
  private final Overview view;

  private GoogleCalendar googleCalendar;

  @Inject
  public OverviewController(OverviewModel model, Overview view, GoogleCalendar googleCalendar) {
    this.model = model;
    this.view = view;
    this.googleCalendar = googleCalendar;

    view.onShowEvents(new Consumer<String>() {
      @Override
      public void accept(String calendarId) {
        final List<Event> eventList = googleCalendar.getEvents(calendarId);
        model.setEvents(eventList);
      }
    });

    view.onSelect(new Consumer<Integer>() {
      @Override
      public void accept(Integer index) {
        if (index >= 0 && index < model.getEvents().size()) {
          final Event event = model.getEvents().get(index);
          model.selectEvent(event);
        } else {
          model.selectEvent(null);
        }
      }
    });
  }

  public Overview getView() {
    return view;
  }
}
