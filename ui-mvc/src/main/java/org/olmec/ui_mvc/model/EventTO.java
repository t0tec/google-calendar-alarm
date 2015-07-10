package org.olmec.ui_mvc.model;

import com.google.api.services.calendar.model.Event;

import org.joda.time.DateTime;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

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

  private PropertyChangeSupport changes = new PropertyChangeSupport(this);

  private final ObjectProperty<Event> event;

  private final StringProperty id;
  private final StringProperty summary;
  private final StringProperty description;
  private final ObjectProperty<DateTime> end;
  private final ObjectProperty<DateTime> start;

  public EventTO(Event event) {
    this.event = new SimpleObjectProperty<>(event);

    id = new SimpleStringProperty(event.getId());
    summary = new SimpleStringProperty(event.getSummary());
    description = new SimpleStringProperty(event.getDescription());
    start = new SimpleObjectProperty<>(new DateTime(event.getStart().getDateTime().getValue()));
    end = new SimpleObjectProperty(new DateTime(event.getEnd().getDateTime().getValue()));
  }

  public final Event getEvent() {
    return this.event.getValue();
  }

  public ObjectProperty<Event> eventProperty() {
    return this.event;
  }

  public final String getId() {
    return this.id.getValue();
  }

  public StringProperty idProperty() {
    return this.id;
  }

  public final String getSummary() {
    return this.summary.getValue();
  }

  public StringProperty summaryProperty() {
    return this.summary;
  }

  public final String getDescription() {
    return this.description.getValue();
  }

  public StringProperty descriptionProperty() {
    return this.description;
  }

  public final DateTime getEnd() {
    return this.end.getValue();
  }

  public ObjectProperty<DateTime> endProperty() {
    return this.end;
  }

  public final DateTime getStart() {
    return this.start.getValue();
  }

  public ObjectProperty<DateTime> startProperty() {
    return this.start;
  }

  public final void setEvent(Event event) {
    this.event.set(event);
  }

  public final void setId(String id) {
    this.id.setValue(id);
  }

  public final void setSummary(String summary) {
    this.summary.set(summary);
  }

  public final void setDescription(String description) {
    this.description.set(description);
  }

  public final void setEnd(DateTime end) {
    this.end.set(end);
  }

  public final void setStart(DateTime start) {
    this.start.set(start);
  }

  public void addPropertyChangeListener(PropertyChangeListener l) {
    changes.addPropertyChangeListener(l);
  }

  public void removePropertyChangeListener(PropertyChangeListener l) {
    changes.removePropertyChangeListener(l);
  }

  public static Callback<EventTO, Observable[]> extractor() {
    return (EventTO arg0) -> new Observable[]{new PojoAdapter<>(arg0)};
  }
}
