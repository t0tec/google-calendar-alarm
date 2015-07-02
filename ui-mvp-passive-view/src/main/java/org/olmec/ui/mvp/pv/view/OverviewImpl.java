package org.olmec.ui.mvp.pv.view;

import org.olmec.ui.mvp.pv.presenter.OverviewPresenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

import javax.inject.Singleton;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;

/**
 * @author t0tec (t0tec.olmec@gmail.com)
 * @version $Id$
 * @since 1.0
 */
@Singleton
public class OverviewImpl extends AnchorPane implements Overview {

  private static final Logger logger = LoggerFactory.getLogger(OverviewImpl.class);

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

  private OverviewPresenter overviewPresenter;

  public OverviewImpl() {
    load();
  }

  public void preferencesMenuPressed() {

  }

  public void exitMenuPressed() {
    Platform.exit();
  }

  public void aboutMenuPressed() {

  }

  public void showEventsBtnPressed() {
    if (overviewPresenter != null) {
      overviewPresenter.show();
    }
  }

  public void toggleAlarmBtnPressed() {

  }

  public void addEventBtnPressed() {

  }

  public void editEventBtnPressed() {

  }

  public void initialize() {
    eventList.getSelectionModel().selectedItemProperty().addListener(
        new ChangeListener<String>() {
          @Override
          public void changed(ObservableValue<? extends String> observable, String oldValue,
                              String newValue) {
            if (overviewPresenter != null) {
              editEventBtn.setDisable(false);
              overviewPresenter.select();
            }
          }
        }
    );
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

  @Override
  public void setOverviewPresenter(OverviewPresenter overviewPresenter) {
    this.overviewPresenter = overviewPresenter;
  }

  @Override
  public Parent getRoot() {
    return this;
  }

  @Override
  public int getSelectedIndex() {
    return eventList.getSelectionModel().getSelectedIndex();
  }

  @Override
  public void setEvents(List<String> eventList) {
    this.eventList.getItems().clear();
    this.eventList.getItems().addAll(eventList);
  }
}
