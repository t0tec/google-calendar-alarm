package org.olmec.ui_mvc.controller;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.olmec.business.GoogleCalendar;
import org.olmec.ui_mvc.model.Model;
import org.olmec.ui_mvc.view.MainView;

/**
 * @author t0tec (t0tec.olmec@gmail.com)
 * @version $Id$
 * @since 1.0
 */
@Singleton
public class MainController {

  private final Model model;
  private final MainView view;

  private GoogleCalendar googleCalendar;

  @Inject
  public MainController(Model model, MainView view, GoogleCalendar googleCalendar) {
    this.model = model;
    this.view = view;
    this.googleCalendar = googleCalendar;
  }

  public MainView getView() {
    return view;
  }
}
