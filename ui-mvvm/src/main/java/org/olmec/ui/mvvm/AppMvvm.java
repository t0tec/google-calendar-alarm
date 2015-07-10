package org.olmec.ui.mvvm;

import com.google.inject.Guice;
import com.google.inject.Injector;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

/**
 * @author t0tec (t0tec.olmec@gmail.com)
 * @version $Id$
 * @since 1.0
 */
public class AppMvvm extends Application {

  @Override
  public void start(Stage primaryStage) throws Exception {
    Injector injector = Guice.createInjector(new ServiceModule());

    FXMLLoader fxmlLoader = new FXMLLoader();

    // Creating javafx-controller using Guice
    fxmlLoader.setControllerFactory(new Callback<Class<?>, Object>() {
      @Override
      public Object call(Class<?> type) {
        return injector.getInstance(type);
      }
    });

    fxmlLoader.setLocation(this.getClass().getResource("/Overview.fxml"));

    final Parent root = fxmlLoader.load();

    primaryStage.setTitle("Google Calendar Alarm MVVM");
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
