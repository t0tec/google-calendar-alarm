package org.olmec.ui.view;

import com.google.api.services.calendar.model.Event;

import it.sauronsoftware.cron4j.Scheduler;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.olmec.business.GoogleCalendar;
import org.olmec.business.GoogleCalendarService;
import org.olmec.ui.preferences.Preferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.applet.AudioClip;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.List;
import java.util.Random;

import javax.swing.*;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * @author t0tec (t0tec.olmec@gmail.com)
 * @version $Id$
 * @since 1.0
 */
public class MainView extends Application {

  private static final Logger logger = LoggerFactory.getLogger(MainView.class);

  private final GoogleCalendar gCalendarService = new GoogleCalendarService();

  private final EventAdderView eventAdderview = new EventAdderView();
  private final PreferencesView preferencesView = new PreferencesView();

  private MenuBar topMenu;
  private Button showEventsBtn;
  private Button startBtn;
  private Button addEventBtn;

  private TextArea outputArea;
  private Label clockDateLbl;

  @Override
  public void start(final Stage primaryStage) throws Exception {
    primaryStage.setTitle("Google Calendar Alarm");

    GridPane currentDateGrid = new GridPane();
    currentDateGrid.setAlignment(Pos.CENTER);
    currentDateGrid.setHgap(5);
    currentDateGrid.setVgap(5);
    currentDateGrid.setPadding(new Insets(25, 5, 5, 0));
    Label currentDateLbl = new Label("Current date:");
    clockDateLbl = new Label();
    clockDateLbl.setFont(Font.font("Verdana", FontWeight.BOLD, 13));
    currentDateGrid.add(currentDateLbl, 0, 0);
    currentDateGrid.add(clockDateLbl, 1, 0);

    GridPane grid = new GridPane();
    grid.setAlignment(Pos.TOP_LEFT);
    grid.setHgap(10);
    grid.setVgap(20);
    grid.setPadding(new Insets(25, 25, 25, 25));

    showEventsBtn = new Button("Show events");
    showEventsBtn.setPrefWidth(150);
    grid.add(showEventsBtn, 0, 0);

    startBtn = new Button("Start alarm clock");
    startBtn.setPrefWidth(150);
    grid.add(startBtn, 0, 1);

    addEventBtn = new Button("Add event");
    addEventBtn.setPrefWidth(150);
    grid.add(addEventBtn, 0, 2);

    outputArea = new TextArea();
    outputArea.setPrefWidth(275);
    outputArea.setPrefHeight(200);
    outputArea.setWrapText(true);
    outputArea.setEditable(false);

    final BorderPane root = new BorderPane();
    VBox topContainer = new VBox();

    createMenu(primaryStage);

    topContainer.getChildren().add(topMenu);
    root.setTop(topContainer);

    VBox btnContainer = new VBox();
    btnContainer.getChildren().add(grid);

    root.setLeft(btnContainer);

    VBox textAreaContainer = new VBox();
    textAreaContainer.setPadding(new Insets(5, 25, 25, 15));
    textAreaContainer.getChildren().addAll(currentDateGrid, outputArea);
    root.setRight(textAreaContainer);

    final Scene scene = new Scene(root, 500, 300);

    showEventsBtn.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        String gCalendarId = Preferences.getInstance().getValue("googleCalendarId");

        List<Event> items = gCalendarService.getEvents(gCalendarId);
        if (items.size() == 0) {
          outputArea.setText("NO upcoming events found!");
        } else {
          outputArea.setText("Following upcoming events found: \n");
          for (Event item : items) {

            DateTime startTime = new DateTime(item.getStart().getDateTime().getValue());
            if (startTime == null) {
              startTime = new DateTime(item.getStart().getDate().getValue());
            }

            outputArea.appendText(
                "\t" + startTime.toString("dd/MM/yyyy HH:mm:ss") + " - " + item.getSummary()
                + "\n");
          }
        }
      }
    });

    startBtn.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        outputArea.setText("Alarm procedure started. \n");
        Scheduler scheduler = new Scheduler();
        scheduler.schedule("* * * * *", new Runnable() {
          @Override
          public void run() {
            outputArea.setText("Alarm running... \n");

            DateTimeFormatter dtFmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");

            String gCalendarId = Preferences.getInstance().getValue("googleCalendarId");

            List<Event> items = gCalendarService.getEvents(gCalendarId);
            outputArea.appendText("Wake up scheduled for:\n");
            for (Event item : items) {
              DateTime startTime = new DateTime(item.getStart().getDateTime().getValue());
              outputArea.appendText(
                  "\t" + startTime.toString("dd/MM/yyyy HH:mm:ss") + " - " + item.getSummary()
                  + "\n");
              if (startTime.toString(dtFmt).equals(new DateTime().toString(dtFmt))) {
                Thread thread = new Thread(alarm);
                thread.setDaemon(true);
                thread.start();
              }
            }
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setResizable(true);
            alert.initOwner(root.getScene().getWindow());
            alert.setTitle("ERROR!");
            alert.setContentText("Something went wrong! Please contact the developer!");
            alert.show();
          }
        });
        scheduler.setDaemon(true);
        scheduler.start();
      }
    });

    addEventBtn.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        if (!eventAdderview.isShowing()) {
          eventAdderview.show();
        }
        eventAdderview.requestFocus();
      }
    });

    this.updateCurrentTime();
    primaryStage.setScene(scene);
    primaryStage.setResizable(false);
    primaryStage.show();

    this.checkPreferenceFileExists(primaryStage);
  }

  private void createMenu(final Stage stage) {
    topMenu = new MenuBar();

    Menu menu = new Menu("Menu");

    MenuItem preferencesMenu = createMenuItem("Preferences", "Alt+p");
    preferencesMenu.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        if (!preferencesView.isShowing()) {
          preferencesView.show();
        }
        preferencesView.requestFocus();
      }
    });

    MenuItem exitMenu = createMenuItem("Exit", "Alt+x");
    exitMenu.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        Platform.exit();
      }
    });

    Menu helpMenu = new Menu("Help");
    MenuItem about = createMenuItem("About", "F1");
    about.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        final Stage dialog = new Stage();
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initOwner(stage);

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
    });

    menu.getItems().addAll(preferencesMenu, exitMenu);
    helpMenu.getItems().add(about);

    topMenu.getMenus().addAll(menu, helpMenu);
  }

  private MenuItem createMenuItem(String text, String keyCombination) {
    MenuItem item = new MenuItem(text);
    item.setAccelerator(KeyCombination.keyCombination(keyCombination));
    return item;
  }

  private void updateCurrentTime() {
    final Timeline timeline =
        new Timeline(new KeyFrame(Duration.ZERO, new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent actionEvent) {
            clockDateLbl.setText(new DateTime().toString("dd/MM/yyyy HH:mm:ss"));
          }
        }), new KeyFrame(Duration.seconds(1))); // loop every 1 seconds

    timeline.setCycleCount(Timeline.INDEFINITE); // Do this for eternity
    timeline.play();
  }

  private void checkPreferenceFileExists(final Stage stage) {
    if (!Preferences.getInstance().exists()) {
      Alert alert = new Alert(Alert.AlertType.WARNING);
      alert.setResizable(true);
      alert.initOwner(stage);
      alert.setTitle("Preferences not set!");
      alert.setContentText("You have to create a preference file first! Open Menu -> Preferences");
      alert.show();

      showEventsBtn.setDisable(true);
      startBtn.setDisable(true);
      addEventBtn.setDisable(true);
    }
  }

  Thread alarm = new Thread(new Runnable() {
    @Override
    public void run() {
      try {
        String path = Preferences.getInstance().getValue("musicDirectory");

        File dir = new File(path);
        if (dir.isDirectory()) {
          File[] files = dir.listFiles();
          File selected = files[new Random().nextInt(files.length)];
          String absolutePath = selected.toString();
          String decodedPath = URLDecoder.decode(absolutePath, "UTF-8");
          // TODO: Use Media and MediaPlayer instead but having problem to use this on linux platforms
          // Missing codec/library on my system (Linux Manjaro-Arch-based-x64)
          AudioClip ac = JApplet.newAudioClip(new URL("file://" + decodedPath));
          ac.play();
          logger.info("Playing: " + decodedPath);
          outputArea.appendText("Playing: " + decodedPath);
        }
      } catch (IOException io) {
        logger.error("IOException: " + io.getMessage());
      }
    }
  });

  public static void main(String[] args) {
    launch(args);
  }

}
