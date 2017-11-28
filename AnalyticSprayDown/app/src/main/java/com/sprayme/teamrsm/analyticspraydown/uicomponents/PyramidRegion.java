package com.sprayme.teamrsm.analyticspraydown.uicomponents;

import android.graphics.Region;

import com.sprayme.teamrsm.analyticspraydown.models.Tick;

/**
 * Created by climbak on 11/22/17.
 */

public class PyramidRegion extends Region {
  private Tick tick;

  public PyramidRegion(int left, int top, int right, int bottom, Tick tick){
    super(left, top, right, bottom);
    this.tick = tick;
  }

  public Tick getTick() {
    return tick;
  }
}
