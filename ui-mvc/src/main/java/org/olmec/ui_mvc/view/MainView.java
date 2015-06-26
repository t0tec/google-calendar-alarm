package org.olmec.ui_mvc.view;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.joda.time.DateTime;
import org.olmec.ui_mvc.model.Model;

import java.io.IOException;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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


  @Inject
  public MainView(Model model) {
    this.model = model;

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
            currentTimeLbl.setText("Current time: " + new DateTime().toString("dd/MM/yyyy HH:mm:ss"));
          }
        }), new KeyFrame(Duration.seconds(1))); // loop every 1 seconds

    timeline.setCycleCount(Timeline.INDEFINITE); // Do this for eternity
    timeline.play();
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
