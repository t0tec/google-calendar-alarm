package org.olmec.ui_mvc.view;

import com.google.api.services.calendar.model.Event;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.olmec.ui_mvc.Navigator;
import org.olmec.ui_mvc.model.EventModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.util.function.Consumer;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
  private Spinner startHourPicker;

  @FXML
  private Spinner startMinutePicker;

  @FXML
  private DatePicker endDatePicker;

  @FXML
  private Spinner endHourPicker;

  @FXML
  private Spinner endMinutePicker;

  @FXML
  private CheckBox repeatChkBx;

  @FXML
  private Button saveBtn;

  @FXML
  private Button cancelBtn;

  private final EventModel model;

  private Consumer<Event> saveBtnObserver;

  @Inject
  public AddEventView(EventModel model) {
    this.model = model;

    model.addChangeObserver(new Runnable() {
      @Override
      public void run() {
        if (model.getEvent() != null) {
          eventTitleTxtFld.setText(model.getEvent().getSummary());
          descrEventTxtFld.setText(model.getEvent().getDescription());
        }
      }
    });

    load();
  }

  public void initialize() {
    startDatePicker.setValue(LocalDate.now());
    startHourPicker.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 12, 1));
    startHourPicker.setEditable(true);
    startMinutePicker.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0, 1));
    startMinutePicker.setEditable(true);
    endDatePicker.setValue(LocalDate.now());
    endHourPicker.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 13, 1));
    endHourPicker.setEditable(true);
    endMinutePicker.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0, 1));
    endMinutePicker.setEditable(true);
  }

  public void onSave(Consumer<Event> observer) {
    this.saveBtnObserver = observer;
  }

  public void saveBtnPressed() {
    if (saveBtnObserver != null && isInputValid()) {

    }
  }

  private boolean isInputValid() {
    boolean isValid = true;

    return isValid;
  }

  public void cancelBtnPressed() {
    Navigator.getInstance().setScreen(Navigator.OVERVIEW);
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
