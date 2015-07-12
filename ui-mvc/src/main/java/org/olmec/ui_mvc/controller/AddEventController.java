package org.olmec.ui_mvc.controller;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.olmec.business.GoogleCalendar;
import org.olmec.ui_mvc.Preferences;
import org.olmec.ui_mvc.model.EventTO;
import org.olmec.ui_mvc.model.OverviewModel;
import org.olmec.ui_mvc.view.AddEventView;

import java.util.function.Consumer;

/**
 * @author t0tec (t0tec.olmec@gmail.com)
 * @version $Id$
 * @since 1.0
 */
@Singleton
public class AddEventController {

  private final OverviewModel model;
  private final AddEventView view;

  private GoogleCalendar googleCalendar;

  @Inject
  public AddEventController(OverviewModel model, AddEventView view,
                            GoogleCalendar googleCalendar) {
    this.model = model;
    this.view = view;
    this.googleCalendar = googleCalendar;

    view.onSave(new Consumer<EventTO>() {
      @Override
      public void accept(EventTO event) {
        Event result = googleCalendar.createEvent(event.getEvent(),
                                                  Preferences.getInstance()
                                                      .getValue("googleCalendarId"));

        // Only add events that are in the future or have already started?
        if (result.getEnd().getDateTime().getValue() >
               new DateTime(System.currentTimeMillis()).getValue()) {

          model.getEvents().add(new EventTO(result));
        }
      }
    });
  }

  public AddEventView getView() {
    return view;
  }
}
