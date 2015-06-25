package org.olmec.ui.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * @author t0tec (t0tec.olmec@gmail.com)
 * @version $Id$
 * @since 1.0
 */
public class RepeatView extends Stage {

  private static final Logger logger = LoggerFactory.getLogger(RepeatView.class);

  private CheckBox chkBxMon = new CheckBox("Mon");
  private CheckBox chkBxTue = new CheckBox("Tue");
  private CheckBox chkBxWed = new CheckBox("Wed");
  private CheckBox chkBxThu = new CheckBox("Thu");
  private CheckBox chkBxFri = new CheckBox("Fri");
  private CheckBox chkBxSat = new CheckBox("Sat");
  private CheckBox chkBxSun = new CheckBox("Sun");

  private DatePicker startDatePicket = new DatePicker(LocalDate.now());
  private DatePicker endsDatePicker = new DatePicker(LocalDate.now().plusWeeks(1));

  public RepeatView() {
    setTitle("Repeat event");
    start();
  }

  private void start() {
    GridPane grid = new GridPane();
    grid.setAlignment(Pos.CENTER);
    grid.setHgap(5);
    grid.setVgap(15);
    grid.setPadding(new Insets(10, 10, 10, 10));

    grid.add(new Label("Repeat on:"), 0, 1);

    HBox hbChkBx = new HBox(10);
    hbChkBx.setAlignment(Pos.BOTTOM_RIGHT);
    hbChkBx.getChildren().addAll(chkBxMon, chkBxTue, chkBxWed, chkBxThu, chkBxFri, chkBxSat, chkBxSun);
    grid.add(hbChkBx, 1, 1);

    grid.add(new Label("Starts:"), 0, 2);

    grid.add(startDatePicket, 1, 2);

    grid.add(new Label("Ends:"), 0, 3);

    grid.add(endsDatePicker, 1, 3);

    Button saveBtn = new Button("Save");
    HBox hbBtn = new HBox(10);
    hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
    hbBtn.getChildren().add(saveBtn);
    grid.add(hbBtn, 1, 4);

    VBox gridContainer = new VBox();
    gridContainer.getChildren().add(grid);

    final BorderPane root = new BorderPane();
    root.setCenter(gridContainer);
    this.setResizable(false);
    this.setScene(new Scene(root, 500, 175));
  }
}
