package org.olmec.ui.mvp.pv.presenter;

import org.olmec.ui.mvp.pv.view.Overview;

/**
 * @author t0tec (t0tec.olmec@gmail.com)
 * @version $Id$
 * @since 1.0
 */
public interface OverviewPresenter {

  void show();

  void select();

  Overview getOverview();
}
