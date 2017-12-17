package com.sprayme.teamrsm.analyticspraydown.models;

import java.util.List;

/**
 * Created by climbak on 10/5/17.
 */

public class PyramidStep {
  private int size;
  private Grade grade;
  private List<Tick> ticks;
  private List<Route> routes;

  public PyramidStep(int size, Grade grade, List<Tick> ticks) {
    this.size = size;
    this.grade = grade;
    this.ticks = ticks;
//    this.routes = routes;
    // todo what do we do if we get a list of routes that aren't of the same grade?
    // todo what to do if the route list is empty?
  }

  public int getSize() {
    return size;
  }

  public Tick getAt(int i) {
    return ticks.size() <= i ? null : ticks.get(i);
  }

  public Grade getGrade() {
    return grade;
  }
}
