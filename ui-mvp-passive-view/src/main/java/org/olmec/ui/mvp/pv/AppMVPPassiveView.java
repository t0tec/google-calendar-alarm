package org.olmec.ui.mvp.pv;

import com.google.inject.Guice;
import com.google.inject.Injector;

import org.olmec.ui.mvp.pv.presenter.OverviewPresenter;

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
public class AppMVPPassiveView extends Application {

  @Override
  public void start(Stage primaryStage) throws Exception {
    Injector injector = Guice.createInjector(new BindInterfaceModule());

    final OverviewPresenter overviewPresenter = injector.getInstance(OverviewPresenter.class);

    primaryStage.setTitle("Google Calendar Alarm MVC");
    primaryStage.setWidth(600);
    primaryStage.setHeight(300);
    primaryStage.setResizable(false);
    primaryStage.setScene(new Scene(overviewPresenter.getOverview().getRoot()));
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
