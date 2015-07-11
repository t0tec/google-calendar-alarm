package org.olmec.ui_mvc.controller;

import com.google.api.services.calendar.model.Event;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.olmec.business.GoogleCalendar;
import org.olmec.ui_mvc.model.EventTO;
import org.olmec.ui_mvc.model.OverviewModel;
import org.olmec.ui_mvc.view.Overview;

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
        ObservableList<EventTO> eventList = FXCollections.observableArrayList(EventTO.extractor());
        List<Event> results = googleCalendar.getEvents(calendarId);
        for (Event event : results) {
          eventList.add(new EventTO(event));
        }
        model.setEvents(eventList);
      }
    });

    view.onSelect(new Consumer<EventTO>() {
      @Override
      public void accept(EventTO event) {
        if (event != null) {
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
