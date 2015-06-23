package org.gcalendar.view;

import org.joda.time.DateTime;

import java.io.File;

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
public class App extends Application {

  private MenuBar topMenu;
  private Button showEventsBtn;
  private Button startBtn;
  private Button addEventBtn;

  private TextArea outputArea;
  private Label clockDateLbl;

  private File preferences = new File("preferences.properties");

  @Override
  public void start(final Stage primaryStage) throws Exception {
    primaryStage.setTitle("Google Calendar Alarm");

    GridPane currentDateGrid = new GridPane();
    currentDateGrid.setAlignment(Pos.TOP_LEFT);
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

      }
    });

    startBtn.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {

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
    if (!preferences.exists()) {
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

  public static void main(String[] args) {
    launch(args);
  }

}
