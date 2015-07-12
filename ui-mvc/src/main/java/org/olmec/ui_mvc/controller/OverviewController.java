package org.olmec.ui_mvc.controller;

import com.google.api.services.calendar.model.Event;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

import org.olmec.business.GoogleCalendar;
import org.olmec.ui_mvc.Navigator;
import org.olmec.ui_mvc.Preferences;
import org.olmec.ui_mvc.ServiceModule;
import org.olmec.ui_mvc.model.EventTO;
import org.olmec.ui_mvc.model.OverviewModel;
import org.olmec.ui_mvc.view.EditEventView;
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

    view.onEdit(new Consumer<EventTO>() {
      @Override
      public void accept(EventTO event) {
        Navigator navigator = Navigator.getInstance();

        if (navigator.getScreen(Navigator.EDIT_EVENT) != null) {
          EditEventView view = (EditEventView) navigator.getScreen(Navigator.EDIT_EVENT);
          view.getModel().setEvent(event);
          navigator.setScreen(Navigator.EDIT_EVENT);
        } else {
          Injector injector = Guice.createInjector(new ServiceModule());
          final EditEventController
              controller =
              injector.getInstance(EditEventController.class);
          controller.getView().getModel().setEvent(event);

          navigator.addScreen(Navigator.EDIT_EVENT, controller.getView());
          navigator.setScreen(Navigator.EDIT_EVENT);
        }
      }
    });

    view.onDelete(new Consumer<EventTO>() {
      @Override
      public void accept(EventTO event) {
        if (event != null) {
          googleCalendar.deleteEvent(event.getEvent(),
                                     Preferences.getInstance().getValue("googleCalendarId"));
          model.getEvents().remove(event);
        }
      }
    });
  }

  public Overview getView() {
    return view;
  }
}
