package org.olmec.ui.mvvmfx.viewmodel;

import com.google.api.services.calendar.model.Event;
import com.google.inject.Inject;

import de.saxsys.mvvmfx.ViewModel;

import eu.lestard.advanced_bindings.api.ObjectBindings;

import org.olmec.business.GoogleCalendar;
import org.olmec.business.GoogleCalendarService;
import org.olmec.ui.mvvmfx.Preferences;
import org.olmec.ui.mvvmfx.model.EventTO;

import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * @author t0tec (t0tec.olmec@gmail.com)
 * @version $Id$
 * @since 1.0
 */
public class MainViewModel implements ViewModel {

  private final GoogleCalendar googleCalendar;

  private StringProperty eventId = new SimpleStringProperty();
  private StringProperty eventSummary = new SimpleStringProperty();
  private StringProperty eventDescription = new SimpleStringProperty();

  private ObservableList<EventTO> events = FXCollections.observableArrayList();

  private ObjectProperty<EventTO> selectedEvent = new SimpleObjectProperty<>();

  @Inject
  public MainViewModel(GoogleCalendarService googleCalendar) {
    this.googleCalendar = googleCalendar;

    eventId.bind(ObjectBindings.map(selectedEvent, EventTO::getId));
    eventSummary.bind(ObjectBindings.map(selectedEvent, EventTO::getSummary));
    eventDescription.bind(ObjectBindings.map(selectedEvent, EventTO::getDescription));
  }

  public StringProperty eventSummaryProperty() {
    return eventSummary;
  }

  public StringProperty eventDescriptionProperty() {
    return eventDescription;
  }

  public StringProperty eventIdProperty() {
    return eventId;
  }

  public ObservableList<EventTO> eventsProperty() {
    return events;
  }

  public ObjectProperty<EventTO> selectedEventProperty() {
    return selectedEvent;
  }

  public void showEvents() {
    final List<Event> results =
        googleCalendar.getEvents(Preferences.getInstance().getValue("googleCalendarId"));

    events.clear();

    for (Event event : results) {
      events.add(new EventTO(event));
    }
  }
}
