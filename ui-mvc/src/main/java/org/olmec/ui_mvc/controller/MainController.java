package org.olmec.ui_mvc.controller;

import com.google.api.services.calendar.model.Event;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.olmec.business.GoogleCalendar;
import org.olmec.ui_mvc.model.MainModel;
import org.olmec.ui_mvc.view.MainView;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author t0tec (t0tec.olmec@gmail.com)
 * @version $Id$
 * @since 1.0
 */
@Singleton
public class MainController {

  private final MainModel model;
  private final MainView view;

  private GoogleCalendar googleCalendar;

  @Inject
  public MainController(MainModel model, MainView view, GoogleCalendar googleCalendar) {
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
  }

  public MainView getView() {
    return view;
  }
}