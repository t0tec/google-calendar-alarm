package org.olmec.ui_mvc;

import com.google.inject.Guice;
import com.google.inject.Injector;

import org.olmec.ui_mvc.controller.OverviewController;
import org.olmec.ui_mvc.controller.PreferencesController;
import org.olmec.ui_mvc.view.Overview;
import org.olmec.ui_mvc.view.PreferencesView;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * @author t0tec (t0tec.olmec@gmail.com)
 * @version $Id$
 * @since 1.0
 */
public class AppMVC extends Application {

  private static Stage stage;

  @Override
  public void start(Stage primaryStage) throws Exception {
    this.stage = primaryStage;

    Injector injector = Guice.createInjector(new ServiceModule());

    final OverviewController overviewController = injector.getInstance(OverviewController.class);

    final Overview overview = overviewController.getView();

    final PreferencesController preferencesController = injector.getInstance(
        PreferencesController.class);

    final PreferencesView preferencesView = preferencesController.getView();


    Navigator navigator = Navigator.getInstance();
    navigator.addScreen(Navigator.OVERVIEW, overview);
    navigator.addScreen(Navigator.PREFERENCES, preferencesView);

    navigator.setScreen(Navigator.OVERVIEW);

    Group root = new Group();
    root.getChildren().addAll(navigator);

    primaryStage.setTitle("Google Calendar Alarm MVC");
    primaryStage.setWidth(600);
    primaryStage.setHeight(400);
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
