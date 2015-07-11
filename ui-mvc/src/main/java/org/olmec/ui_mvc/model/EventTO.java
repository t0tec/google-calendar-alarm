package org.olmec.ui_mvc.model;

import com.google.api.services.calendar.model.Event;

import org.joda.time.DateTime;

import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Callback;


/**
 * @author t0tec (t0tec.olmec@gmail.com)
 * @version $Id$
 * @since 1.0
 */
public class EventTO {

  private ObjectProperty<Event> event;

  private StringProperty id;
  private StringProperty summary;
  private StringProperty description;
  private ObjectProperty<DateTime> end;
  private ObjectProperty<DateTime> start;

  public EventTO(Event event) {
    this.event = new SimpleObjectProperty<>(event);

    id = new SimpleStringProperty(event.getId());
    summary = new SimpleStringProperty(event.getSummary());
    description = new SimpleStringProperty(event.getDescription());
    start = new SimpleObjectProperty<DateTime>(new DateTime(event.getStart().getDateTime().getValue()));
    end = new SimpleObjectProperty<DateTime>(new DateTime(event.getEnd().getDateTime().getValue()));
  }


  public final Event getEvent() {
    return eventProperty().getValue();
  }

  public ObjectProperty<Event> eventProperty() {
    return this.event;
  }

  public final void setEvent(final Event event) {
    eventProperty().set(event);
  }

  public final String getId() {
    return idProperty().getValue();
  }

  public StringProperty idProperty() {
    return this.id;
  }

  public void setId(String id) {
    this.id.set(id);
  }

  public final String getSummary() {
    return summaryProperty().getValue();
  }

  public StringProperty summaryProperty() {
    return this.summary;
  }

  public final void setSummary(String summary) {
    this.summary.set(summary);
  }

  public final String getDescription() {
    return descriptionProperty().getValue();
  }

  public StringProperty descriptionProperty() {
    return this.description;
  }

  public final void setDescription(String description) {
    this.description.set(description);
  }

  public final DateTime getEnd() {
    return endProperty().getValue();
  }

  public ObjectProperty<DateTime> endProperty() {
    return this.end;
  }

  public void setEnd(DateTime end) {
    this.end.set(end);
  }

  public final DateTime getStart() {
    return startProperty().getValue();
  }

  public ObjectProperty<DateTime> startProperty() {
    return this.start;
  }

  public void setStart(DateTime start) {
    this.start.set(start);
  }

  public static Callback<EventTO, Observable[]> extractor() {
    return (EventTO e) -> new Observable[]{e.eventProperty(), e.idProperty(), e.summaryProperty(), e.descriptionProperty(), e.startProperty(), e.endProperty()};
  }
}
