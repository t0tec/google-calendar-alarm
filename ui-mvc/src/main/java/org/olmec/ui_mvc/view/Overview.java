package org.olmec.ui_mvc.view;

import com.google.api.services.calendar.model.Event;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

import it.sauronsoftware.cron4j.Scheduler;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.olmec.ui_mvc.Alarm;
import org.olmec.ui_mvc.Navigator;
import org.olmec.ui_mvc.Preferences;
import org.olmec.ui_mvc.ServiceModule;
import org.olmec.ui_mvc.controller.EditEventController;
import org.olmec.ui_mvc.model.OverviewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
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
@Singleton
public class Overview extends AnchorPane {

  private static final Logger logger = LoggerFactory.getLogger(Overview.class);

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
  private ListView<String> eventList;

  private final OverviewModel model;

  private final Scheduler scheduler;
  private String scheduleId;

  private Consumer<String> showEventsBtnObserver;

  private Consumer<Integer> selectObserver;

  @Inject
  public Overview(OverviewModel model) {
    this.model = model;
    this.scheduler = new Scheduler();

    model.addEventsChangeObserver(new Runnable() {
      @Override
      public void run() {
        eventList.getItems().clear();

        final ObservableList<String> listEvents = FXCollections.observableArrayList();
        for (Event event : model.getEvents()) {
          DateTime startTime = new DateTime(event.getStart().getDateTime().getValue());
          DateTime endTime = new DateTime(event.getEnd().getDateTime().getValue());
          listEvents.add(
              event.getSummary() + ": " + startTime.toString("dd/MM/yyyy HH:mm:ss") + " - "
              + endTime.toString("dd/MM/yyyy HH:mm:ss"));
        }

        eventList.setItems(listEvents);
      }
    });

    load();
  }

  public void initialize() {
    updateTime();

    this.editEventBtn.setDisable(true);
    eventList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue<? extends String> observable, String oldValue,
                          String newValue) {
        if (selectObserver != null) {
          editEventBtn.setDisable(false);
          selectObserver.accept(eventList.getSelectionModel().getSelectedIndex());
        }
      }
    });
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
    Navigator.getInstance().setScreen(Navigator.PREFERENCES);
  }

  public void exitMenuPressed() {
    Platform.exit();
  }

  public void aboutMenuPressed() {
    final Stage dialog = new Stage();
    dialog.initModality(Modality.WINDOW_MODAL);
    dialog.initOwner(this.getScene().getWindow());

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

  public void onShowEvents(Consumer<String> observer) {
    this.showEventsBtnObserver = observer;
  }

  public void onSelect(Consumer<Integer> observer) {
    this.selectObserver = observer;
  }

  public void showEventsBtnPressed() {
    if (showEventsBtnObserver != null) {
      showEventsBtnObserver.accept(Preferences.getInstance().getValue("googleCalendarId"));
    }
  }

  public void toggleAlarmBtnPressed() {
    if (toggleAlarmBtn.isSelected()) {
      final Stage dialog = new Stage();
      dialog.initModality(Modality.WINDOW_MODAL);
      dialog.initOwner(this.getScene().getWindow());

      VBox box = new VBox();
      box.setAlignment(Pos.TOP_LEFT);
      box.setPadding(new Insets(10));
      box.getChildren().addAll(new Text("A scheduler is running to alert you for upcoming events"));
      Scene myDialogScene = new Scene(box);

      dialog.setTitle("Starting alarm scheduler!");
      dialog.setResizable(false);
      dialog.setScene(myDialogScene);
      dialog.show();

      logger.info("Alarm scheduler is about to run...");
      scheduleId = scheduler.schedule("* * * * *", new Runnable() {
        @Override
        public void run() {

          DateTimeFormatter dtFmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");

          List<Event> events = model.getEvents();
          for (Event event : events) {
            DateTime startTime = new DateTime(event.getStart().getDateTime().getValue());
            if (startTime.toString(dtFmt).equals(new DateTime().toString(dtFmt))) {
              logger.info(event.getSummary() + " has just started");

              File dir = new File(Preferences.getInstance().getValue("musicDirectory"));
              if (dir.isDirectory()) {
                try {
                  File[] files = dir.listFiles();
                  File random = files[new Random().nextInt(files.length)];
                  String path = random.toString();
                  String decodedPath = URLDecoder.decode(path, "UTF-8");

                  Thread thread = new Thread(new Alarm(decodedPath));
                  thread.setDaemon(true);
                  thread.start();

                  Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                      final Stage dialog = new Stage();
                      dialog.initModality(Modality.WINDOW_MODAL);
                      dialog.initOwner(getScene().getWindow());

                      VBox box = new VBox();
                      box.setAlignment(Pos.TOP_LEFT);
                      box.setPadding(new Insets(10));
                      box.getChildren().addAll(new Text(
                          "Following event has started: " + event.getSummary() + ".\n"
                          + "Playing: " + decodedPath));
                      Scene myDialogScene = new Scene(box);

                      dialog.setTitle("Event notifier!");
                      dialog.setResizable(false);
                      dialog.setScene(myDialogScene);
                      dialog.show();
                    }
                  });

                } catch (UnsupportedEncodingException e) {
                  throw new IllegalStateException(e);
                }
              }
            }
          }
        }
      });

      scheduler.setDaemon(true);
      scheduler.start();
      toggleAlarmBtn.setText("Stop alarm clock");
    } else {
      scheduler.stop();
      scheduler.deschedule(scheduleId);
      toggleAlarmBtn.setText("Start alarm clock");
      logger.info("Scheduler has stopped!");
      final Stage dialog = new Stage();
      dialog.initModality(Modality.WINDOW_MODAL);
      dialog.initOwner(this.getScene().getWindow());

      VBox box = new VBox();
      box.setAlignment(Pos.TOP_LEFT);
      box.setPadding(new Insets(10));
      box.getChildren()
          .addAll(new Text("A scheduler has stopped to alert you for upcoming events"));
      Scene myDialogScene = new Scene(box);

      dialog.setTitle("Stopped alarm scheduler!");
      dialog.setResizable(false);
      dialog.setScene(myDialogScene);
      dialog.show();
    }
  }

  public void addEventBtnPressed() {
    Navigator.getInstance().setScreen(Navigator.ADD_EVENT);
  }

  public void editEventBtnPressed() {
    int index = eventList.getSelectionModel().getSelectedIndex();
    Event event = model.getEvents().get(index);

    Navigator navigator = Navigator.getInstance();

    if (navigator.getScreen(Navigator.EDIT_EVENT) != null) {
      EditEventView view = (EditEventView) navigator.getScreen(Navigator.EDIT_EVENT);
      view.getModel().setEvent(event);
      navigator.setScreen(Navigator.EDIT_EVENT);
    } else {
      Injector injector = Guice.createInjector(new ServiceModule());
      final EditEventController controller = injector.getInstance(EditEventController.class);
      controller.getView().getModel().setEvent(event);

      navigator.addScreen(Navigator.EDIT_EVENT, controller.getView());
      navigator.setScreen(Navigator.EDIT_EVENT);
    }
  }

  private void load() {
    FXMLLoader fxmlLoader = new FXMLLoader();
    fxmlLoader.setLocation(this.getClass().getResource("/Overview.fxml"));
    fxmlLoader.setController(this);
    fxmlLoader.setRoot(this);

    try {
      fxmlLoader.load();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
