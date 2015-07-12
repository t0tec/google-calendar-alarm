package org.olmec.ui_mvc.view;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.joda.time.DateTime;
import org.olmec.ui_mvc.Navigator;
import org.olmec.ui_mvc.model.EventTO;
import org.olmec.ui_mvc.model.OverviewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
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
public class AddEventView extends AnchorPane {

  private static final Logger logger = LoggerFactory.getLogger(AddEventView.class);

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

  private final OverviewModel model;

  private Consumer<EventTO> saveBtnObserver;

  @Inject
  public AddEventView(OverviewModel model) {
    this.model = model;

    load();
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

  public void onSave(Consumer<EventTO> observer) {
    this.saveBtnObserver = observer;
  }

  public void saveBtnPressed() {
    if (saveBtnObserver != null && isInputValid()) {
      EventTO eventTO = new EventTO().getBuilder().summary(eventTitleTxtFld.getText())
          .description(descrEventTxtFld.getText())
          .start(extractJodaDateTime(startDatePicker.getValue(),
                                     startHourPicker.getValue(),
                                     startMinutePicker.getValue()))
          .end(extractJodaDateTime(endDatePicker.getValue(),
                                   endHourPicker.getValue(),
                                   endMinutePicker.getValue())).build();

      saveBtnObserver.accept(eventTO);

      resetValues();

      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setResizable(true);
      alert.initOwner(this.getScene().getWindow());
      alert.setTitle("Event added!");
      alert.setContentText("You've saved a new event to your calendar!");
      alert.show();

      logger.info("Event successfully added!");
    }
  }

  private void resetValues() {
    this.eventTitleTxtFld.setText("");
    this.descrEventTxtFld.setText("");
    this.startDatePicker.setValue(LocalDate.now());
    this.startHourPicker.getValueFactory().setValue(12);
    this.startMinutePicker.getValueFactory().setValue(0);
    this.endDatePicker.setValue(LocalDate.now());
    this.endHourPicker.getValueFactory().setValue(12);
    this.endMinutePicker.getValueFactory().setValue(0);
    this.repeatChkBx.setSelected(false);
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

    DateTime start = extractJodaDateTime(startDatePicker.getValue(),
                                         startHourPicker.getValue(),
                                         startMinutePicker.getValue());

    DateTime end = extractJodaDateTime(endDatePicker.getValue(),
                                       endHourPicker.getValue(),
                                       endMinutePicker.getValue());

    if (start.getMillis() > end.getMillis()) {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setResizable(true);
      alert.initOwner(this.getScene().getWindow());
      alert.setTitle("Time range not valid!");
      alert.setContentText("The specified time rang is not valid!");
      alert.show();

      isValid = false;
    }

    return isValid;
  }

  public void cancelBtnPressed() {
    Navigator.getInstance().setScreen(Navigator.OVERVIEW);
  }

  private DateTime extractJodaDateTime(final LocalDate date, final int hour,
                                       final int minutes) {
    DateTime dt = new DateTime(date.getYear(),
                               date.getMonthValue(), date.getDayOfMonth(), hour, minutes, 0);
    return dt;
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
