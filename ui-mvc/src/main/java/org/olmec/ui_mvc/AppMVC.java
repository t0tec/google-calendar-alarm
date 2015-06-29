package org.olmec.ui_mvc;

import com.google.inject.Guice;
import com.google.inject.Injector;

import org.olmec.ui_mvc.controller.AddEventController;
import org.olmec.ui_mvc.controller.OverviewController;
import org.olmec.ui_mvc.controller.PreferencesController;
import org.olmec.ui_mvc.view.AddEventView;
import org.olmec.ui_mvc.view.Overview;
import org.olmec.ui_mvc.view.PreferencesView;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * @author t0tec (t0tec.olmec@gmail.com)
 * @version $Id$
 * @since 1.0
 */
public class AppMVC extends Application {

  @Override
  public void start(Stage primaryStage) throws Exception {
    Injector injector = Guice.createInjector(new ServiceModule());

    final OverviewController overviewController = injector.getInstance(OverviewController.class);

    final Overview overview = overviewController.getView();

    final PreferencesController preferencesController = injector.getInstance(
        PreferencesController.class);

    final PreferencesView preferencesView = preferencesController.getView();

    final AddEventController addEventController = injector.getInstance(AddEventController.class);

    final AddEventView addEventView = addEventController.getView();

    Navigator navigator = Navigator.getInstance();
    navigator.addScreen(Navigator.OVERVIEW, overview);
    navigator.addScreen(Navigator.PREFERENCES, preferencesView);
    navigator.addScreen(Navigator.ADD_EVENT, addEventView);

    navigator.setScreen(Navigator.OVERVIEW);

    AnchorPane root = new AnchorPane();
    root.getChildren().addAll(navigator);

    primaryStage.setTitle("Google Calendar Alarm MVC");
    primaryStage.setWidth(600);
    primaryStage.setHeight(300);
    primaryStage.setResizable(false);
    primaryStage.setScene(new Scene(root));
    primaryStage.show();

    primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
      @Override
      public void handle(WindowEvent event) {
        Platform.exit();
      }
    });
  }

  public static void main(String[] args) {
    launch(args);
  }
}
