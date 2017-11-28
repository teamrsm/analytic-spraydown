package com.sprayme.teamrsm.analyticspraydown.uicomponents;

import android.graphics.Region;

import com.sprayme.teamrsm.analyticspraydown.models.Route;

/**
 * Created by climbak on 11/22/17.
 */

public class PyramidRegion extends Region {
  private Route route;

  public PyramidRegion(int left, int top, int right, int bottom, Route route){
    super(left, top, right, bottom);
    this.route = route;
  }

  public Route getRoute() {
    return route;
  }
}
