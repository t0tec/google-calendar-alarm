package org.olmec.ui_mvc.view;

import com.google.api.services.calendar.model.Calendar;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.olmec.ui_mvc.Navigator;
import org.olmec.ui_mvc.model.PreferencesModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;
import java.util.function.Consumer;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;

/**
 * @author t0tec (t0tec.olmec@gmail.com)
 * @version $Id$
 * @since 1.0
 */
@Singleton
public class PreferencesView extends AnchorPane {

  private static final Logger logger = LoggerFactory.getLogger(PreferencesView.class);

  @FXML
  private Text gCalendarIdTxt;

  @FXML
  private TextField gCalendarNameTxtFld;

  @FXML
  private TextField gCalendarDescrTxtFld;

  @FXML
  private TextField musicDirectoryTxtFld;

  @FXML
  private Button browseBtn;

  @FXML
  private ComboBox timeZoneComboBox;

  @FXML
  private Button saveBtn;

  @FXML
  private Button cancelBtn;

  private final PreferencesModel model;

  private Consumer<Calendar> saveBtnObserver;

  @Inject
  public PreferencesView(PreferencesModel model) {
    this.model = model;

    model.addChangeObserver(new Runnable() {
      @Override
      public void run() {
        if (model.getCalendar() != null) {
          gCalendarIdTxt.setText(model.getCalendar().getId());
          gCalendarNameTxtFld.setText(model.getCalendar().getSummary());
          gCalendarDescrTxtFld.setText(model.getCalendar().getDescription());
          musicDirectoryTxtFld.setText(model.getMusicDirectory());
          timeZoneComboBox.getSelectionModel().select(model.getCalendar().getTimeZone().toString());
        }
      }
    });

    load();
  }

  public void initialize() {
    timeZoneComboBox.getItems().addAll(getTimeZones());
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

  public void onBrowseBtnPressed() {
    DirectoryChooser directoryChooser = new DirectoryChooser();
    directoryChooser.setTitle("Choose music directory");
    directoryChooser.setInitialDirectory(
        new File(System.getProperty("user.home"))
    );

    File file = directoryChooser.showDialog(this.getScene().getWindow());
    if (file != null) {
      musicDirectoryTxtFld.setText(file.getAbsolutePath());
    }
  }

  public void onSave(Consumer<Calendar> observer) {
    this.saveBtnObserver = observer;
  }

  public void saveBtnPressed() {
    if (saveBtnObserver != null && isInputValid()) {
      Calendar calendar = new Calendar().setSummary(gCalendarNameTxtFld.getText())
          .setDescription(gCalendarDescrTxtFld.getText())
          .setTimeZone(timeZoneComboBox.getSelectionModel().getSelectedItem().toString());

      if (!gCalendarIdTxt.getText().isEmpty()) {
        calendar.setId(gCalendarIdTxt.getText());
      }

      model.setMusicDirectory(musicDirectoryTxtFld.getText());

      saveBtnObserver.accept(calendar);
    }

    // TODO: show success or go back to main screen
  }

  private boolean isInputValid() {
    boolean isValid = true;
    if (gCalendarNameTxtFld.getText().isEmpty() || gCalendarDescrTxtFld.getText().isEmpty()
        || musicDirectoryTxtFld.getText().isEmpty()
        || timeZoneComboBox.getSelectionModel().isEmpty()) {
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

  private void load() {
    FXMLLoader fxmlLoader = new FXMLLoader();
    fxmlLoader.setLocation(this.getClass().getResource("/Preferences.fxml"));
    fxmlLoader.setController(this);
    fxmlLoader.setRoot(this);

    try {
      fxmlLoader.load();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
