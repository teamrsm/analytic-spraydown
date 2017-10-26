package com.sprayme.teamrsm.analyticspraydown.models;

import com.sprayme.teamrsm.analyticspraydown.utilities.MPQueryRoutesTask;
import com.sprayme.teamrsm.analyticspraydown.utilities.MPQueryTicksTask;
import com.sprayme.teamrsm.analyticspraydown.utilities.MPQueryUserTask;

import java.util.List;

/**
 * Created by climbak on 10/4/17.
 */

public class MPModel {
  public interface MPModelListener {
    // These methods are the different events and
    // need to pass relevant arguments related to the event triggered
    void onRoutesLoaded(List<Route> routes);

    void onTicksLoaded(List<Tick> ticks);

    void onUserLoaded(User user);
  }

  private MPModelListener listener;

  public MPModel(MPModelListener listener) {
    this.listener = listener;
  }

  public void requestUser(String emailAddress, String apiKey) {
    MPQueryUserTask mpQuery = new MPQueryUserTask(emailAddress, apiKey,
            output -> {
              listener.onUserLoaded(output);
            });
    mpQuery.execute();
  }

  public void requestTicks(long userId, String apiKey) {
    MPQueryTicksTask mpQuery = new MPQueryTicksTask(userId, apiKey, output -> {
      listener.onTicksLoaded(output);
    });
    mpQuery.execute();
  }

  public void requestRoutes(String apiKey, Long[] routeIds) {

    MPQueryRoutesTask mpQuery = new MPQueryRoutesTask(apiKey, output -> {
      listener.onRoutesLoaded(output);
    });
    mpQuery.execute(routeIds);
  }
}
