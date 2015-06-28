package org.olmec.ui_mvc.model;

import com.google.api.services.calendar.model.Calendar;
import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.List;

/**
 * @author t0tec (t0tec.olmec@gmail.com)
 * @version $Id$
 * @since 1.0
 */
@Singleton
public class PreferencesModel {

  private Calendar calendar;

  private String musicDirectory;

  private List<Runnable> observers = new ArrayList<>();

  public Calendar getCalendar() {
    return this.calendar;
  }

  public void setCalendar(Calendar calendar) {
    this.calendar = calendar;
    for (Runnable runnable : this.observers) {
      runnable.run();
    }
  }

  public String getMusicDirectory() {
    return this.musicDirectory;
  }

  public void setMusicDirectory(String musicDirectory) {
    this.musicDirectory = musicDirectory;
    for (Runnable runnable : this.observers) {
      runnable.run();
    }
  }

  public void addChangeObserver(Runnable observer) {
    this.observers.add(observer);
  }
}
