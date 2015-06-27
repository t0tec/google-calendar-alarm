package org.olmec.business;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author t0tec (t0tec.olmec@gmail.com)
 * @version $Id$
 * @since 1.0
 */
public class GoogleCalendarService implements GoogleCalendar {

  private static final Logger logger = LoggerFactory.getLogger(GoogleCalendarService.class);

  /**
   * Application name.
   */
  private static final String APPLICATION_NAME =
      "Google Calendar Alarm";

  /**
   * Directory to store user credentials.
   */
  private static final File DATA_STORE_DIR = new File(
      System.getProperty("user.home"), ".credentials/google-calendar-alarm");

  /**
   * Global instance of the {@link FileDataStoreFactory}.
   */
  private static FileDataStoreFactory DATA_STORE_FACTORY;

  /**
   * Global instance of the JSON factory.
   */
  private static final JsonFactory JSON_FACTORY =
      JacksonFactory.getDefaultInstance();

  /**
   * Global instance of the HTTP transport.
   */
  private static HttpTransport HTTP_TRANSPORT;

  /**
   * Global instance of the scopes required by this quickstart.
   */
  private static final List<String> SCOPES =
      Arrays.asList(CalendarScopes.CALENDAR);

  static {
    try {
      HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
      DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
    } catch (Throwable t) {
      t.printStackTrace();
      System.exit(1);
    }
  }

  /**
   * Creates an authorized Credential object.
   *
   * @return an authorized Credential object.
   */
  private static Credential authorize() throws IOException {
    // Load client secrets.
    InputStream in =
        GoogleCalendarService.class.getResourceAsStream("/client_secret.json");
    GoogleClientSecrets clientSecrets =
        GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

    // Build flow and trigger user authorization request.
    GoogleAuthorizationCodeFlow flow =
        new GoogleAuthorizationCodeFlow.Builder(
            HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
            .setDataStoreFactory(DATA_STORE_FACTORY)
            .setAccessType("offline")
            .build();
    Credential credential = new AuthorizationCodeInstalledApp(
        flow, new LocalServerReceiver()).authorize("user");
    logger.info("Credentials saved/loaded to/from " + DATA_STORE_DIR.getAbsolutePath());
    return credential;
  }

  /**
   * Build and return an authorized Calendar client service.
   *
   * @return an authorized Calendar client service
   */
  private static com.google.api.services.calendar.Calendar
  getCalendarService() throws IOException {
    Credential credential = authorize();
    return new com.google.api.services.calendar.Calendar.Builder(
        HTTP_TRANSPORT, JSON_FACTORY, credential)
        .setApplicationName(APPLICATION_NAME)
        .build();
  }

  public Calendar getCalendar(String id) {
    Calendar calendar = new Calendar();
    try {
      calendar = getCalendarService().calendars().get(id).execute();
    } catch (IOException io) {
      logger.error("IOException: " + io.getMessage());
    }
    return calendar;
  }

  public Calendar createCalendar(Calendar calendar) {
    Calendar createdCalendar = new Calendar();
    try {
      createdCalendar = getCalendarService().calendars().insert(calendar).execute();
    } catch (IOException io) {
      logger.error("IOException: " + io.getMessage());
    }
    return createdCalendar;
  }

  public boolean calendarExists(String calendarName) {
    boolean exists = false;
    try {
      CalendarList calendarList = getCalendarService().calendarList().list().execute();

      for (CalendarListEntry calendarListEntry : calendarList.getItems()) {
        if (calendarListEntry.getSummary().equals(calendarName)) {
          exists = true;
        }
      }
    } catch (IOException io) {
      logger.error("IOException: " + io.getMessage());
    }
    return exists;
  }

  public List<Event> getEvents(String calendarId) {
    List<Event> events = new ArrayList<Event>();
    try {
      events = getCalendarService().events().list(calendarId)
          .setMaxResults(10)
          .setTimeMin(new DateTime(System.currentTimeMillis()))
          .setOrderBy("startTime")
          .setSingleEvents(true)
          .execute()
          .getItems();
    } catch (IOException io) {
      logger.error("IOException: " + io.getMessage());
    }
    return events;
  }

  public Event createEvent(Event event, String calendarId, boolean isRecurring) {
    Event createdEvent = new Event();
    try {
      createdEvent = getCalendarService().events().insert(calendarId, event).execute();
    } catch (IOException io) {
      logger.error("IOException: " + io.getMessage());
    }
    return createdEvent;
  }
}
