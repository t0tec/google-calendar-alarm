package org.olmec.ui.mvvm;

import com.google.inject.AbstractModule;

import org.olmec.business.GoogleCalendar;
import org.olmec.business.GoogleCalendarService;

/**
 * @author t0tec (t0tec.olmec@gmail.com)
 * @version $Id$
 * @since 1.0
 */
public class ServiceModule extends AbstractModule {

  @Override
  protected void configure() {
    this.binder().bind(GoogleCalendar.class).to(GoogleCalendarService.class);
  }
}
