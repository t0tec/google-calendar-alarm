package org.olmec.ui.mvvmfx.model;

import com.google.api.services.calendar.model.Event;

import org.joda.time.DateTime;


/**
 * @author t0tec (t0tec.olmec@gmail.com)
 * @version $Id$
 * @since 1.0
 */
public class EventTO {

  private final Event event;

  public EventTO(Event event) {
    this.event = event;
  }

  public String getId() {
    return event.getId();
  }

  public String getSummary() {
    return event.getSummary();
  }

  public String getDescription() {
    return event.getDescription();
  }

  public DateTime getStart() {
    return new DateTime(event.getStart().getDateTime().getValue());
  }

  public DateTime getEnd() {
    return new DateTime(event.getEnd().getDateTime().getValue());
  }

  @Override
  public String toString() {
    return getSummary();
  }
}
