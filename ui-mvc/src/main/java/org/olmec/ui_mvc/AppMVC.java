package org.olmec.ui_mvc;

import com.google.inject.Guice;
import com.google.inject.Injector;

import org.olmec.ui_mvc.controller.OverviewController;
import org.olmec.ui_mvc.view.Overview;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
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

//    final PreferencesController preferencesController = injector.getInstance(
//        PreferencesController.class);
//
//    final PreferencesView preferencesView = preferencesController.getView();

    primaryStage.setTitle("Google Calendar Alarm MVC");
    primaryStage.setWidth(600);
    primaryStage.setHeight(400);
    primaryStage.setResizable(false);
    primaryStage.setScene(new Scene(overview));
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
