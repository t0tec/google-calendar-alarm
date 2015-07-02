package org.olmec.ui.mvp.afterburner;

import com.airhacks.afterburner.injection.Injector;

import org.olmec.business.GoogleCalendar;
import org.olmec.business.GoogleCalendarService;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author t0tec (t0tec.olmec@gmail.com)
 * @version $Id$
 * @since 1.0
 */
public class AppMVPAB extends Application {

  @Override
  public void start(Stage primaryStage) throws Exception {
    Injector.setModelOrService(GoogleCalendar.class, new GoogleCalendarService());

    MainView mainView = new MainView();

    primaryStage.setTitle("Library MVP AfterBurner");
    ;
    primaryStage.setMaxWidth(600);
    primaryStage.setMaxHeight(300);
    primaryStage.setResizable(false);
    primaryStage.setScene(new Scene(mainView.getView()));
    primaryStage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
