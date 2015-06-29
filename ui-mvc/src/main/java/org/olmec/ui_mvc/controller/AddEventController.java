package org.olmec.ui_mvc.controller;

import com.google.api.services.calendar.model.Event;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.olmec.business.GoogleCalendar;
import org.olmec.ui_mvc.Preferences;
import org.olmec.ui_mvc.model.EventModel;
import org.olmec.ui_mvc.view.AddEventView;

import java.util.function.Consumer;

/**
 * @author t0tec (t0tec.olmec@gmail.com)
 * @version $Id$
 * @since 1.0
 */
@Singleton
public class AddEventController {

  private final EventModel model;
  private final AddEventView view;

  private GoogleCalendar googleCalendar;

  @Inject
  public AddEventController(EventModel model, AddEventView view,
                            GoogleCalendar googleCalendar) {
    this.model = model;
    this.view = view;
    this.googleCalendar = googleCalendar;

    view.onSave(new Consumer<Event>() {
      @Override
      public void accept(Event event) {
        googleCalendar.createEvent(event, Preferences.getInstance().getValue("googleCalendarId"));
      }
    });
  }

  public AddEventView getView() {
    return view;
  }
}
