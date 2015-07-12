package org.olmec.business;

import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.Event;

import java.util.List;

/**
 * @author t0tec (t0tec.olmec@gmail.com)
 * @version $Id$
 * @since 1.0
 */
public interface GoogleCalendar {

  Calendar getCalendar(String id);

  Calendar createCalendar(Calendar calendar);

  Calendar updateCalendar(Calendar calendar);

  boolean calendarExists(Calendar calendar);

  List<Event> getEvents(String calendarId);

  Event getEvent(String eventId, String calendarId);

  Event createEvent(Event event, String calendarId);

  Event updateEvent(Event event, String calendarId);

  void deleteEvent(Event event, String calendarId);
}
