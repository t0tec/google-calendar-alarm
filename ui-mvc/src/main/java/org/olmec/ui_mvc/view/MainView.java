package org.olmec.ui_mvc.view;

import com.google.api.services.calendar.model.Event;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.joda.time.DateTime;
import org.olmec.ui_mvc.model.Model;
import org.olmec.ui_mvc.preferences.Preferences;

import java.io.IOException;
import java.util.function.Consumer;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

/**
 * @author t0tec (t0tec.olmec@gmail.com)
 * @version $Id$
 * @since 1.0
 */
@Singleton
public class MainView extends AnchorPane {

  @FXML
  private Button showEventsBtn;

  @FXML
  private Button startAlarmClockBtn;

  @FXML
  private Button addEventBtn;

  @FXML
  private Button editEventBtn;

  @FXML
  private Label currentTimeLbl;

  @FXML
  private ListView<String> eventList;

  private final Model model;

  private Consumer<String> showEventsBtnObserver;

  @Inject
  public MainView(Model model) {
    this.model = model;

    model.addEventsChangeObserver(new Runnable() {
      @Override
      public void run() {
        eventList.getItems().clear();

        final ObservableList<String> listEvents = FXCollections.observableArrayList();
        for (Event event: model.getEvents()) {
          DateTime startTime = new DateTime(event.getStart().getDateTime().getValue());
          listEvents.add(event.getSummary() + " - " + startTime.toString("dd/MM/yyyy HH:mm:ss"));
        }

        eventList.setItems(listEvents);
      }
    });

    load();
  }

  public void initialize() {
    updateTime();
  }

  private void updateTime() {
    final Timeline timeline =
        new Timeline(new KeyFrame(Duration.ZERO, new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent actionEvent) {
            currentTimeLbl
                .setText("Current time: " + new DateTime().toString("dd/MM/yyyy HH:mm:ss"));
          }
        }), new KeyFrame(Duration.seconds(1))); // loop every 1 seconds

    timeline.setCycleCount(Timeline.INDEFINITE); // Do this for eternity
    timeline.play();
  }

  public void onShowEvents(Consumer<String> observer) {
    this.showEventsBtnObserver = observer;
  }

  public void showEventsBtnPressed() {
    if (showEventsBtnObserver != null) {
      showEventsBtnObserver.accept(Preferences.getInstance().getValue("googleCalendarId"));
    }
  }

  public void preferencesMenuPressed() {
    // show preferences view
  }

  public void exitMenuPressed() {
    Platform.exit();
  }

  public void aboutMenuPressed() {
    // show about view
  }

  private void load() {
    FXMLLoader fxmlLoader = new FXMLLoader();
    fxmlLoader.setLocation(this.getClass().getResource("/Main.fxml"));
    fxmlLoader.setController(this);
    fxmlLoader.setRoot(this);

    try {
      fxmlLoader.load();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
