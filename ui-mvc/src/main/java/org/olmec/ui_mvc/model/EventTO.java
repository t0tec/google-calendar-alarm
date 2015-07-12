package org.olmec.ui_mvc.model;

import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.olmec.ui_mvc.Preferences;

import java.util.TimeZone;

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

  public EventTO() {
  }

  public EventTO(Event event) {
    this.event = new SimpleObjectProperty<>(event);

    id = new SimpleStringProperty(event.getId());
    summary = new SimpleStringProperty(event.getSummary());
    description = new SimpleStringProperty(event.getDescription());
    start = new SimpleObjectProperty<>(new DateTime(event.getStart().getDateTime().getValue()));
    end = new SimpleObjectProperty<>(new DateTime(event.getEnd().getDateTime().getValue()));
  }

  private EventTO(Builder builder) {
    Event event = new Event();
    event.setSummary(builder.summary);
    event.setDescription(builder.description);
    event.setStart(builder.extractEventDateTime(builder.start));
    event.setEnd(builder.extractEventDateTime(builder.end));

    this.event = new SimpleObjectProperty<>(event);

    id = new SimpleStringProperty(event.getId());
    summary = new SimpleStringProperty(event.getSummary());
    description = new SimpleStringProperty(event.getDescription());
    start = new SimpleObjectProperty<>(new DateTime(event.getStart().getDateTime().getValue()));
    end = new SimpleObjectProperty<>(new DateTime(event.getEnd().getDateTime().getValue()));
  }

  public static Builder getBuilder() {
    return new Builder();
  }

  public final Event getEvent() {
    return eventProperty().getValue();
  }

  public ObjectProperty<Event> eventProperty() {
    return this.event;
  }

  public final void setEvent(final Event event) {
    eventProperty().setValue(event);
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
    this.summary.setValue(summary);
  }

  public final String getDescription() {
    return descriptionProperty().getValue();
  }

  public StringProperty descriptionProperty() {
    return this.description;
  }

  public final void setDescription(String description) {
    this.description.setValue(description);
  }

  public final DateTime getEnd() {
    return endProperty().getValue();
  }

  public ObjectProperty<DateTime> endProperty() {
    return this.end;
  }

  public void setEnd(DateTime end) {
    this.end.setValue(end);
  }

  public final DateTime getStart() {
    return startProperty().getValue();
  }

  public ObjectProperty<DateTime> startProperty() {
    return this.start;
  }

  public void setStart(DateTime start) {
    this.start.setValue(start);
  }

  public static Callback<EventTO, Observable[]> extractor() {
    return (EventTO e) -> new Observable[]{e.eventProperty(), e.idProperty(), e.summaryProperty(), e.descriptionProperty(), e.startProperty(), e.endProperty()};
  }

  /**
   * The builder pattern makes it easier to create objects
   */
  public static class Builder {

    private String id;
    private String summary;
    private String description;
    private DateTime start;
    private DateTime end;

    public Builder() {
    }

    public Builder id(String id) {
      this.id = id;
      return this;
    }

    public Builder summary(String summary) {
      this.summary = summary;
      return this;
    }

    public Builder description(String description) {
      this.description = description;
      return this;
    }

    public Builder start(DateTime start) {
      this.start = start;
      return this;
    }

    public Builder end(DateTime end) {
      this.end = end;
      return this;
    }

    public EventTO build() {
      EventTO build = new EventTO(this);

      return build;
    }

    public EventDateTime extractEventDateTime(final DateTime dt) {
      dt.withZone(DateTimeZone.forTimeZone(
          TimeZone.getTimeZone(Preferences.getInstance().getValue("timeZone"))));

      // output format example: 2015-06-29T09:00:00+02:00
      EventDateTime eventDateTime = new EventDateTime()
          .setDateTime(
              new com.google.api.client.util.DateTime(dt.toString("yyyy-MM-dd'T'HH:mm:ssZZ")))
          .setTimeZone(Preferences.getInstance().getValue("timeZone"));

      return eventDateTime;
    }
  }
}
