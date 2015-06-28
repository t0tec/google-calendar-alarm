package org.olmec.ui_mvc.controller;

import com.google.api.services.calendar.model.Calendar;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.olmec.business.GoogleCalendar;
import org.olmec.ui_mvc.model.PreferencesModel;
import org.olmec.ui_mvc.Preferences;
import org.olmec.ui_mvc.view.PreferencesView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.function.Consumer;

import javafx.concurrent.Task;

/**
 * @author t0tec (t0tec.olmec@gmail.com)
 * @version $Id$
 * @since 1.0
 */
@Singleton
public class PreferencesController {

  private final PreferencesModel model;
  private final PreferencesView view;

  private GoogleCalendar googleCalendar;

  @Inject
  public PreferencesController(PreferencesModel model, PreferencesView view, GoogleCalendar googleCalendar) {
    this.model = model;
    this.view = view;
    this.googleCalendar = googleCalendar;

    if (Preferences.getInstance().exists()) {
      model.setCalendar(googleCalendar.getCalendar(Preferences.getInstance().getValue("googleCalendarId")));
      model.setMusicDirectory(Preferences.getInstance().getValue("musicDirectory"));
    }

    view.onSave(new Consumer<Calendar>() {
      @Override
      public void accept(Calendar calendar) {
        if (!googleCalendar.calendarExists(calendar)) {
          model.setCalendar(googleCalendar.createCalendar(calendar));
        } else {
          model.setCalendar(googleCalendar.updateCalendar(calendar));
        }

        Thread thread = new Thread(save);
        thread.setDaemon(true);
        thread.start();
      }
    });

  }

  Task<Boolean> save = new Task<Boolean>() {
    @Override
    public Boolean call() {
      Boolean result = false;
      try {
        Properties preferences = new Properties();
        preferences.setProperty("googleCalendarId", model.getCalendar().getId());
        preferences.setProperty("googleCalendarName", model.getCalendar().getSummary());
        preferences.setProperty("musicDirectory", model.getMusicDirectory());
        preferences.setProperty("timeZone", model.getCalendar().getTimeZone().toString());

        File file = new File("preferences.properties");
        FileOutputStream fileOutStream = new FileOutputStream(file);
        preferences.store(fileOutStream, "Google Calendar Alarm preferences");
        fileOutStream.close();
        if (file.exists()) {
          result = true;
        }
      } catch (IOException io) {
        result = false;
      }

      return result;
    }
  };

  public PreferencesView getView() {
    return view;
  }
}
