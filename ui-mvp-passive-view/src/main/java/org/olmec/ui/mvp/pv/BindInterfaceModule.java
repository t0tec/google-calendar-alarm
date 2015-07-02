package org.olmec.ui.mvp.pv;

import com.google.inject.AbstractModule;

import org.olmec.business.GoogleCalendar;
import org.olmec.business.GoogleCalendarService;
import org.olmec.ui.mvp.pv.presenter.OverviewPresenter;
import org.olmec.ui.mvp.pv.presenter.OverviewPresenterImpl;
import org.olmec.ui.mvp.pv.view.Overview;
import org.olmec.ui.mvp.pv.view.OverviewImpl;

/**
 * @author t0tec (t0tec.olmec@gmail.com)
 * @version $Id$
 * @since 1.0
 */
public class BindInterfaceModule extends AbstractModule {

  @Override
  protected void configure() {
    this.binder().bind(GoogleCalendar.class).to(GoogleCalendarService.class);
    this.binder().bind(Overview.class).to(OverviewImpl.class);
    this.binder().bind(OverviewPresenter.class).to(OverviewPresenterImpl.class);
  }
}
