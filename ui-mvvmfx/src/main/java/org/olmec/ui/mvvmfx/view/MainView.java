package org.olmec.ui.mvvmfx.view;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;

import org.joda.time.DateTime;
import org.olmec.ui.mvvmfx.model.EventTO;
import org.olmec.ui.mvvmfx.viewmodel.MainViewModel;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * @author t0tec (t0tec.olmec@gmail.com)
 * @version $Id$
 * @since 1.0
 */
public class MainView implements FxmlView<MainViewModel>, Initializable {

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
  private ListView<EventTO> eventList;

  @InjectViewModel
  private MainViewModel viewModel;

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    updateTime();

    eventList.setItems(viewModel.eventsProperty());

    viewModel.selectedEventProperty().bind(eventList.getSelectionModel().selectedItemProperty());
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

  public void preferencesMenuPressed() {
  }

  public void exitMenuPressed() {
    Platform.exit();
  }

  public void aboutMenuPressed() {
    final Stage dialog = new Stage();
    dialog.initModality(Modality.NONE);

    VBox box = new VBox();
    box.setAlignment(Pos.TOP_LEFT);
    box.setPadding(new Insets(10));
    box.getChildren().addAll(new Text("Developed by t0tec (t0tec.olmec@gmail.com)"));
    Scene myDialogScene = new Scene(box);

    dialog.setTitle("About");
    dialog.setResizable(false);
    dialog.setScene(myDialogScene);
    dialog.show();
  }

  public void showEventsBtnPressed() {
    viewModel.showEvents();
  }

  public void toggleAlarmBtnPressed() {
  }

  public void addEventBtnPressed() {
  }

  public void editEventBtnPressed() {
  }
}
