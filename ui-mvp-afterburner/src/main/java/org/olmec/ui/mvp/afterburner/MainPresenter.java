package org.olmec.ui.mvp.afterburner;

import com.google.api.services.calendar.model.Event;

import org.joda.time.DateTime;
import org.olmec.business.GoogleCalendar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;

/**
 * @author t0tec (t0tec.olmec@gmail.com)
 * @version $Id$
 * @since 1.0
 */
public class MainPresenter {

  private static final Logger logger = LoggerFactory.getLogger(MainPresenter.class);

  @FXML
  private Button showEventsBtn;

  @FXML
  private ToggleButton toggleAlarmBtn;

  @FXML
  private Button addEventBtn;

  @FXML
  private Button editEventBtn;

  @FXML
  private Label currentTimeLbl;

  @FXML
  public ListView<Event> eventList;

  private ObservableList<Event> events = FXCollections.observableArrayList();

  @Inject
  GoogleCalendar googleCalendar;

  public void initialize() {
    eventList.setItems(events);

    eventList.setCellFactory((ListView<Event> param) -> new ListCell<Event>() {
      @Override
      protected void updateItem(Event event, boolean empty) {
        super.updateItem(event, empty);
        if (event != null) {
          DateTime startTime = new DateTime(event.getStart().getDateTime().getValue());
          DateTime endTime = new DateTime(event.getEnd().getDateTime().getValue());
          setText(event.getSummary() + ": " + startTime.toString("dd/MM/yyyy HH:mm:ss") + " - "
                  + endTime.toString("dd/MM/yyyy HH:mm:ss"));
        }
      }
    });

    eventList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Event>() {
      @Override
      public void changed(ObservableValue<? extends Event> observable, Event oldValue,
                          Event newValue) {
        if (newValue != null) {
          editEventBtn.setDisable(false);
        }
      }
    });
  }


  public void preferencesMenuPressed() {
  }

  public void exitMenuPressed() {
  }

  public void aboutMenuPressed() {
  }

  public void showEventsBtnPressed() {
    events.clear();
    events.addAll(googleCalendar.getEvents(Preferences.getInstance().getValue("googleCalendarId")));
  }

  public void toggleAlarmBtnPressed() {
  }

  public void addEventBtnPressed() {
  }

  public void editEventBtnPressed() {
  }
}
