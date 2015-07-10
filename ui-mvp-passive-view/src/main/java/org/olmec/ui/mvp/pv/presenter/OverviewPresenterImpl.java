package org.olmec.ui.mvp.pv.presenter;

import com.google.api.services.calendar.model.Event;
import com.google.inject.Inject;

import org.joda.time.DateTime;
import org.olmec.ui.mvp.pv.model.OverviewModel;
import org.olmec.ui.mvp.pv.view.Overview;

import javax.inject.Singleton;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * @author t0tec (t0tec.olmec@gmail.com)
 * @version $Id$
 * @since 1.0
 */
@Singleton
public class OverviewPresenterImpl implements OverviewPresenter {

  private final OverviewModel model;
  private final Overview overview;

  @Inject
  public OverviewPresenterImpl(OverviewModel model, Overview overview) {
    this.model = model;
    this.overview = overview;
    overview.setOverviewPresenter(this);

    model.addEventsChangeObserver(new Runnable() {
      @Override
      public void run() {

        final ObservableList<String> listEvents = FXCollections.observableArrayList();
        for (Event event : model.getEvents()) {
          DateTime startTime = new DateTime(event.getStart().getDateTime().getValue());
          DateTime endTime = new DateTime(event.getEnd().getDateTime().getValue());
          listEvents.add(
              event.getSummary() + ": " + startTime.toString("dd/MM/yyyy HH:mm:ss") + " - "
              + endTime.toString("dd/MM/yyyy HH:mm:ss"));
        }

        overview.setEvents(listEvents);
      }
    });
  }

  @Override
  public void show() {
    model.showEvents();
  }

  @Override
  public void select() {

  }

  @Override
  public Overview getOverview() {
    return overview;
  }
}
