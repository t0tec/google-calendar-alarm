package org.olmec.ui_mvc.view;

import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.olmec.ui_mvc.Navigator;
import org.olmec.ui_mvc.Preferences;
import org.olmec.ui_mvc.model.EventModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.util.TimeZone;
import java.util.function.Consumer;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

/**
 * @author t0tec (t0tec.olmec@gmail.com)
 * @version $Id$
 * @since 1.0
 */
@Singleton
public class EditEventView extends AnchorPane {

  private static final Logger logger = LoggerFactory.getLogger(EditEventView.class);

  @FXML
  private TextField eventTitleTxtFld;

  @FXML
  private TextField descrEventTxtFld;

  @FXML
  private DatePicker startDatePicker;

  @FXML
  private Spinner<Integer> startHourPicker;

  @FXML
  private Spinner<Integer> startMinutePicker;

  @FXML
  private DatePicker endDatePicker;

  @FXML
  private Spinner<Integer> endHourPicker;

  @FXML
  private Spinner<Integer> endMinutePicker;

  @FXML
  private CheckBox repeatChkBx;

  @FXML
  private Button saveBtn;

  @FXML
  private Button cancelBtn;

  private final EventModel model;

  private Consumer<Event> saveBtnObserver;

  @Inject
  public EditEventView(EventModel model) {
    this.model = model;

    model.addChangeObserver(new Runnable() {
      @Override
      public void run() {
        if (model.getEvent() != null) {
          eventTitleTxtFld.setText(model.getEvent().getSummary());
          descrEventTxtFld.setText(model.getEvent().getDescription());

          DateTime dt = new DateTime(model.getEvent().getStart().getDateTime().getValue());
          startDatePicker.setValue(LocalDate.of(dt.getYear(), dt.getMonthOfYear(), dt.getDayOfMonth()));
          startHourPicker.getValueFactory().setValue(dt.getHourOfDay());
          startMinutePicker.getValueFactory().setValue(dt.getMinuteOfHour());

          dt = new DateTime(model.getEvent().getEnd().getDateTime().getValue());
          endDatePicker.setValue(LocalDate.of(dt.getYear(), dt.getMonthOfYear(), dt.getDayOfMonth()));
          endHourPicker.getValueFactory().setValue(dt.getHourOfDay());
          endMinutePicker.getValueFactory().setValue(dt.getMinuteOfHour());
        }
      }
    });

    load();
  }

  public EventModel getModel() {
    return this.model;
  }

  public void initialize() {
    startDatePicker.setValue(LocalDate.now());
    startHourPicker
        .setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 12, 1));
    startHourPicker.setEditable(true);
    startMinutePicker
        .setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0, 1));
    startMinutePicker.setEditable(true);
    endDatePicker.setValue(LocalDate.now());
    endHourPicker.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 13, 1));
    endHourPicker.setEditable(true);
    endMinutePicker
        .setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0, 1));
    endMinutePicker.setEditable(true);

    cancelBtn.setCancelButton(true);
  }

  public void onSave(Consumer<Event> observer) {
    this.saveBtnObserver = observer;
  }

  public void saveBtnPressed() {
    if (saveBtnObserver != null && isInputValid()) {
      Event event = model.getEvent()
          .setSummary(eventTitleTxtFld.getText())
          .setDescription(descrEventTxtFld.getText())
          .setStart(extractEventDateTime(startDatePicker.getValue(), startHourPicker.getValue(),
                                         startMinutePicker.getValue()))
          .setEnd(extractEventDateTime(endDatePicker.getValue(), endHourPicker.getValue(),
                                       endMinutePicker.getValue()));

      saveBtnObserver.accept(event);

      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setResizable(true);
      alert.initOwner(this.getScene().getWindow());
      alert.setTitle("Event added!");
      alert.setContentText("You've updated  event to your calendar!");
      alert.show();
    }
  }

  private boolean isInputValid() {
    boolean isValid = true;
    if (eventTitleTxtFld.getText().isEmpty() || descrEventTxtFld.getText().isEmpty()) {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setResizable(true);
      alert.initOwner(this.getScene().getWindow());
      alert.setTitle("Empty fields!");
      alert.setContentText("You have to fill in all the fields!");
      alert.show();

      isValid = false;
    }
    return isValid;
  }

  public void cancelBtnPressed() {
    Navigator.getInstance().setScreen(Navigator.OVERVIEW);
  }

  private EventDateTime extractEventDateTime(final LocalDate date, final int hour,
                                             final int minutes) {
    DateTime dt = new DateTime(date.getYear(),
                               date.getMonthValue(), date.getDayOfMonth(), hour, minutes, 0);

    dt.withZone(DateTimeZone.forTimeZone(
        TimeZone.getTimeZone(Preferences.getInstance().getValue("timeZone"))));

    // output format example: 2015-06-29T09:00:00+02:00
    EventDateTime eventDateTime = new EventDateTime()
        .setDateTime(
            new com.google.api.client.util.DateTime(dt.toString("yyyy-MM-dd'T'HH:mm:ssZZ")))
        .setTimeZone(Preferences.getInstance().getValue("timeZone"));

    return eventDateTime;
  }

  private void load() {
    FXMLLoader fxmlLoader = new FXMLLoader();
    fxmlLoader.setLocation(this.getClass().getResource("/Event.fxml"));
    fxmlLoader.setController(this);
    fxmlLoader.setRoot(this);

    try {
      fxmlLoader.load();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
