package org.olmec.ui.view;

import com.google.api.services.calendar.model.Calendar;

import org.olmec.business.GoogleCalendar;
import org.olmec.business.GoogleCalendarService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

/**
 * @author t0tec (t0tec.olmec@gmail.com)
 * @version $Id$
 * @since 1.0
 */
public class PreferencesView extends Stage {

  private static final Logger logger = LoggerFactory.getLogger(PreferencesView.class);

  private final GoogleCalendar gCalendarService = new GoogleCalendarService();

  private final Text gCalendarIdTxt = new Text();
  private final TextField gCalendarNameTxtFld = new TextField();
  private final TextField musicDirectoryTxtFld = new TextField();
  private final ComboBox timeZoneChooser = new ComboBox();

  public PreferencesView() {
    setTitle("Application preferences");
    start();
  }

  private void start() {
    GridPane grid = new GridPane();
    grid.setAlignment(Pos.CENTER);
    grid.setHgap(5);
    grid.setVgap(15);
    grid.setPadding(new Insets(15, 15, 15, 15));

    Label gCalendarIdLbl = new Label("Google calendar id:");
    grid.add(gCalendarIdLbl, 0, 1);

    grid.add(gCalendarIdTxt, 1, 1);

    Label gCalendarNameLbl = new Label("Google calendar name:");
    grid.add(gCalendarNameLbl, 0, 2);

    grid.add(gCalendarNameTxtFld, 1, 2);

    Label musicDirectoryPathLbl = new Label("Music directory:");
    grid.add(musicDirectoryPathLbl, 0, 3);

    grid.add(musicDirectoryTxtFld, 1, 3);

    Button browseDirectoryBtn = new Button("Browse");
    grid.add(browseDirectoryBtn, 2, 3);

    Label timeZoneLbl = new Label("Select your time zone:");
    grid.add(timeZoneLbl, 0, 4);

    timeZoneChooser.getItems().addAll(getTimeZones());
    grid.add(timeZoneChooser, 1, 4);

    Button saveBtn = new Button("Save");
    HBox hbBtn = new HBox(10);
    hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
    hbBtn.getChildren().add(saveBtn);
    grid.add(hbBtn, 1, 5);

    VBox gridContainer = new VBox();
    gridContainer.getChildren().add(grid);

    final BorderPane root = new BorderPane();
    root.setCenter(gridContainer);
    this.setResizable(false);
    this.setScene(new Scene(root, 650, 250));

    loadPreferences(this);

    browseDirectoryBtn.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose music directory");
        directoryChooser.setInitialDirectory(
            new File(System.getProperty("user.home"))
        );
        File file = directoryChooser.showDialog(root.getScene().getWindow());
        if (file != null) {
          musicDirectoryTxtFld.setText(file.getAbsolutePath());
        }
      }
    });

    saveBtn.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {

        if (gCalendarNameTxtFld.getText().isEmpty() || musicDirectoryTxtFld.getText().isEmpty()
            || timeZoneChooser.getSelectionModel().isEmpty()) {
          Alert alert = new Alert(Alert.AlertType.ERROR);
          alert.setResizable(true);
          alert.initOwner(root.getScene().getWindow());
          alert.setTitle("Empty fields!");
          alert.setContentText("You have to fill in all the fields!");
          alert.show();
          return;
        }

        Task<Boolean> save = new Task<Boolean>() {
          @Override
          public Boolean call() {
            Boolean result = false;
            try {
              createGoogleCalendar();

              Properties preferences = new Properties();
              preferences.setProperty("googleCalendarId", gCalendarIdTxt.getText());
              preferences.setProperty("googleCalendarName", gCalendarNameTxtFld.getText());
              preferences.setProperty("musicDirectory", musicDirectoryTxtFld.getText());
              preferences.setProperty("timeZone",
                                      timeZoneChooser.getSelectionModel().getSelectedItem()
                                          .toString());

              File file = new File("preferences.properties");
              FileOutputStream fileOutStream = new FileOutputStream(file);
              preferences.store(fileOutStream, "Google Calendar Alarm preferences");
              fileOutStream.close();
              if (file.exists()) {
                result = true;
              }
            } catch (IOException io) {
              logger.error("IOException: " + io.getMessage());
              result = false;
            }

            return result;
          }
        };

        save.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
          @Override
          public void handle(WorkerStateEvent event) {
            if (save.getValue()) {
              Alert alert = new Alert(Alert.AlertType.INFORMATION);
              alert.setResizable(true);
              alert.initOwner(root.getScene().getWindow());
              alert.setTitle("Success!");
              alert.setContentText("Your preferences have been successfully saved! Be careful!"
                                   + "When editing your preferences, new calendars can be created on your google account!");
              alert.show();
            }
          }
        });

        save.setOnFailed(new EventHandler<WorkerStateEvent>() {
          @Override
          public void handle(WorkerStateEvent event) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setResizable(true);
            alert.initOwner(root.getScene().getWindow());
            alert.setTitle("ERROR!");
            alert.setContentText("Something went wrong! Please contact the developer!");
            alert.show();
          }
        });
        new Thread(save).start();
      }
    });
  }

  private List<String> getTimeZones() {
    ArrayList<String> list = new ArrayList<>();
    String[] timeZoneIDs = TimeZone.getAvailableIDs();
    for (String timeZone : timeZoneIDs) {
      if (timeZone.matches("^(Africa|America|Asia|Atlantic|Australia|Europe|Indian|Pacific)/.*")) {
        list.add(timeZone);
      }
    }
    Collections.sort(list);
    return list;
  }

  private void loadPreferences(Stage stage) {
    try {
      File file = new File("preferences.properties");
      if (file.exists()) {
        FileInputStream fileIn = new FileInputStream(file);
        Properties properties = new Properties();
        properties.load(fileIn);

        gCalendarIdTxt.setText(properties.getProperty("googleCalendarId"));
        gCalendarNameTxtFld.setText(properties.getProperty("googleCalendarName"));
        musicDirectoryTxtFld.setText(properties.getProperty("musicDirectory"));
        timeZoneChooser.getSelectionModel().select(properties.getProperty("timeZone"));
        fileIn.close();

        gCalendarNameTxtFld.setDisable(true);
        timeZoneChooser.setDisable(true);
      }
    } catch (IOException io) {
      logger.error("IOException: " + io.getMessage());
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setResizable(true);
      alert.initOwner(stage);
      alert.setTitle("ERROR!");
      alert.setContentText("Something went wrong! Please contact the developer!");
      alert.show();
    }
  }

  private void createGoogleCalendar() {
    // Create a specific calendar for our application but checks first if one already exists.
    boolean calendarExists = false;

    if (gCalendarService.calendarExists(new Calendar().setSummary(gCalendarNameTxtFld.getText()))) {
      calendarExists = true;
    }

    if (!calendarExists) {
      Calendar calendar = new Calendar();
      calendar.setSummary(gCalendarNameTxtFld.getText());
      calendar.setTimeZone(timeZoneChooser.getSelectionModel().getSelectedItem().toString());
      calendar.setDescription("Use this calendar to alarm you for upcoming events!");

      Calendar createdCalendar = gCalendarService.createCalendar(calendar);

      gCalendarIdTxt.setText(createdCalendar.getId());
    }
  }

}
