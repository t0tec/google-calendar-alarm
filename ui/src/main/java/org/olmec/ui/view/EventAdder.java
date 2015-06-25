package org.olmec.ui.view;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import org.joda.time.DateTimeZone;
import org.olmec.business.GoogleCalendar;
import org.olmec.business.GoogleCalendarService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Properties;
import java.util.TimeZone;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * @author t0tec (t0tec.olmec@gmail.com)
 * @version $Id$
 * @since 1.0
 */
public class EventAdder extends Stage {

  private static final Logger logger = LoggerFactory.getLogger(EventAdder.class);

  private final GoogleCalendar gCalendarService = new GoogleCalendarService();

  private Stage repeaterWindow = new RepeaterWindow();

  private TextField eventSummaryTxt;
  private DatePicker startDatePicker;
  private DatePicker endDatePicker;

  private Spinner<Integer> startTimeHourPicker;
  private Spinner<Integer> startTimeMinutePicker;
  private Spinner<Integer> endTimeHourPicker;
  private Spinner<Integer> endTimeMinutePicker;

  private CheckBox repeatChkBx;
  private Button addEventBtn;
  private Text summaryTxt;

  public EventAdder() {
    setTitle("Add event to calendar");
    start();
  }

  private void start() {
    GridPane grid = new GridPane();
    grid.setAlignment(Pos.CENTER);
    grid.setHgap(5);
    grid.setVgap(15);
    grid.setPadding(new Insets(15, 15, 15, 15));

    eventSummaryTxt = new TextField();
    grid.add(eventSummaryTxt, 0, 1, 4, 1);
    eventSummaryTxt.setPromptText("Description or summary of the event");

    grid.add(new Label("Starting"), 0, 2, 2, 1);

    grid.add(new Label("Ending"), 2, 2, 2, 1);

    startDatePicker = new DatePicker(LocalDate.now());
    grid.add(startDatePicker, 0, 3, 2, 1);

    endDatePicker = new DatePicker(LocalDate.now());
    grid.add(endDatePicker, 2, 3, 2, 1);

    startTimeHourPicker = new Spinner<Integer>(0, 24, 12);
    grid.add(startTimeHourPicker, 0, 4);

    startTimeMinutePicker = new Spinner<Integer>(0, 60, 0);
    grid.add(startTimeMinutePicker, 1, 4);

    endTimeHourPicker = new Spinner<Integer>(0, 24, 13);
    grid.add(endTimeHourPicker, 2, 4);

    endTimeMinutePicker = new Spinner<Integer>(0, 60, 0);
    grid.add(endTimeMinutePicker, 3, 4);

    repeatChkBx = new CheckBox("Repeat");
    grid.add(repeatChkBx, 0, 5);

    grid.add(new Label("Summary: "), 0, 6);
    summaryTxt = new Text();
    grid.add(summaryTxt, 1, 6);

    addEventBtn = new Button("Add event");
    HBox hbBtn = new HBox(10);
    hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
    hbBtn.getChildren().add(addEventBtn);
    grid.add(hbBtn, 3, 7);

    VBox gridContainer = new VBox();
    gridContainer.getChildren().add(grid);

    final BorderPane root = new BorderPane();
    root.setCenter(gridContainer);
    this.setResizable(false);
    this.setScene(new Scene(root, 500, 350));

    addEventBtn.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        if (eventSummaryTxt.getText().isEmpty()) {
          Alert alert = new Alert(Alert.AlertType.ERROR);
          alert.setResizable(true);
          alert.initOwner(root.getScene().getWindow());
          alert.setTitle("Empty fields!");
          alert.setContentText("You have to fill in all the fields!");
          alert.show();
          return;
        }

        if (!repeatChkBx.isSelected()) {
          createSingleEvent();
        } else {
          // TODO: for recurring events
          createRecurringEvent();
        }
      }
    });

    repeatChkBx.selectedProperty().addListener(new ChangeListener<Boolean>() {
      @Override
      public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
                          Boolean newValue) {
        if (!repeaterWindow.isShowing() && !oldValue) {
          repeaterWindow.show();
          repeatChkBx.setDisable(true);
          repeaterWindow.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
              repeatChkBx.setDisable(false);
              repeatChkBx.setSelected(false);
            }
          });
        }
      }
    });
  }

  private void createSingleEvent() {
    Event event = new Event().setSummary(eventSummaryTxt.getText());

    try {
      File file = new File("preferences.properties");
      FileInputStream fileIn = new FileInputStream(file);
      Properties properties = new Properties();
      properties.load(fileIn);

      DateTime startDateTime =
          new DateTime(extractDateTime(startDatePicker.getValue(), startTimeHourPicker.getValue(),
                                       startTimeMinutePicker.getValue(),
                                       properties.getProperty("timeZone")));

      DateTime endDateTime =
          new DateTime(extractDateTime(endDatePicker.getValue(), endTimeHourPicker.getValue(),
                                       endTimeMinutePicker.getValue(),
                                       properties.getProperty("timeZone")));

      EventDateTime start = new EventDateTime()
          .setDateTime(startDateTime)
          .setTimeZone(properties.getProperty("timeZone"));
      event.setStart(start);

      EventDateTime end = new EventDateTime()
          .setDateTime(endDateTime)
          .setTimeZone(properties.getProperty("timeZone"));
      event.setEnd(end);

      String gCalendarId = properties.getProperty("googleCalendarId");

      fileIn.close();

      Event createdEvent = gCalendarService.createEvent(event, gCalendarId, false);

      logger.info("Single event created with unique id: " + createdEvent.getId());

      if (!event.isEmpty()) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setResizable(true);
        alert.initOwner(this.getScene().getWindow());
        alert.setTitle("Success!");
        alert.setContentText("Your successfully added a single event to your calendar!");
        alert.show();
      } else {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setResizable(true);
        alert.initOwner(this.getScene().getWindow());
        alert.setTitle("ERROR!");
        alert.setContentText("Something went wrong! Please contact the developer!");
        alert.show();
      }
    } catch (IOException io) {
      logger.error("IOException: " + io.getMessage());
    }
  }

  private void createRecurringEvent() {

  }

  // output format: 2015-06-23T09:00:00+02:00
  private String extractDateTime(final LocalDate date, final int hour, final int minutes,
                                 final String timeZone) {
    org.joda.time.DateTime dt =
        new org.joda.time.DateTime(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), hour,
                                   minutes, 0);
    dt.withZone(DateTimeZone.forTimeZone(TimeZone.getTimeZone(timeZone)));
    return dt.toString("yyyy-MM-dd'T'HH:mm:ssZZ");
  }
}
