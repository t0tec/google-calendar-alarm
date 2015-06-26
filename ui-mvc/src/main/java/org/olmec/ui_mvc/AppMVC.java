package org.olmec.ui_mvc;

import com.google.inject.Guice;
import com.google.inject.Injector;

import org.olmec.ui_mvc.controller.MainController;
import org.olmec.ui_mvc.view.MainView;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author t0tec (t0tec.olmec@gmail.com)
 * @version $Id$
 * @since 1.0
 */
public class AppMVC extends Application {

  @Override
  public void start(Stage primaryStage) throws Exception {

    Injector injector = Guice.createInjector(new ServiceModule());

    final MainController mainController = injector.getInstance(MainController.class);

    final MainView view = mainController.getView();

    primaryStage.setTitle("Google Calendar Alarm MVC");
    primaryStage.setWidth(600);
    primaryStage.setHeight(400);
    primaryStage.setResizable(false);
    primaryStage.setScene(new Scene(view));
    primaryStage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
