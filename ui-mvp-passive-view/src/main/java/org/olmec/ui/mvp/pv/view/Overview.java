package org.olmec.ui.mvp.pv.view;

import org.olmec.ui.mvp.pv.presenter.OverviewPresenter;

import java.util.List;

import javafx.scene.Parent;

/**
 * @author t0tec (t0tec.olmec@gmail.com)
 * @version $Id$
 * @since 1.0
 */
public interface Overview {

  void setOverviewPresenter(OverviewPresenter overviewPresenter);

  Parent getRoot();

  int getSelectedIndex();

  void setEvents(List<String> eventList);
}
