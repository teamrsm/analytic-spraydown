package com.sprayme.teamrsm.analyticspraydown.models;

import java.util.List;

import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

/**
 * Created by climbak on 10/5/17.
 */

public class Pyramid {
  private int height;
  private int stepChangeSize;
  private PyramidStepType stepType;
  private PyramidStep[] steps;
  private RouteType routeType;

  public Pyramid(List<Tick> ticks, int height, int stepChangeSize, PyramidStepType stepType, RouteType routeType) {
    this.height = height;
    this.stepChangeSize = stepChangeSize;
    this.stepType = stepType;
    this.routeType = routeType;

    Tick hardestTick = StreamSupport.stream(ticks).max(
            (tick1, tick2) -> tick1.getRoute().getGrade().compareTo(tick2.getRoute().getGrade())).orElse(null);
    if (hardestTick == null)
      return; // todo do something useful here

    Grade hardestGrade = hardestTick.getRoute().getGrade();

    // limit our height to the easiest grade of this type
//        height = height >= hardestGrade.getGradeValue() ? height : hardestGrade.getGradeValue();

    steps = buildPyramidSteps(ticks, height, stepChangeSize, stepType, hardestGrade);
  }

  public Pyramid(List<Tick> ticks, int height, int stepChangeSize, PyramidStepType stepType, RouteType routeType, Grade goal) {
    this.height = height;
    this.stepChangeSize = stepChangeSize;
    this.stepType = stepType;
    this.routeType = routeType;

//    Grade currentGrade = goal;

    // limit our height to the easiest grade of this type
//        height = height >= currentGrade.getGradeValue() ? height : currentGrade.getGradeValue();

    steps = buildPyramidSteps(ticks, height, stepChangeSize, stepType, goal);
  }

  private PyramidStep[] buildPyramidSteps(List<Tick> ticks, int height, int stepChangeSize, PyramidStepType stepType, Grade startingGrade) {
    PyramidStep[] steps = new PyramidStep[height];
    Grade grade = startingGrade;
    int size = 1;
    for (int i = 0; i < height; i++) {
      if (grade == null)
        break;
      Grade currentGrade = grade;

      List<Tick> stepTicks = StreamSupport.stream(ticks)
              .filter((tick) -> tick.getRoute().getGrade().compareTo(currentGrade) == 0)
              .collect(Collectors.toList());

      steps[i] = new PyramidStep(size, currentGrade, stepTicks);
      grade = grade.nextEasiest();

      if (size == 1)
        size = 2;
      else if (stepType == PyramidStepType.Multiplicative)
        size *= stepChangeSize;
      else
        size += stepChangeSize;
    }

    return steps;
  }

  public int getWidth() {
    return steps != null ? steps[steps.length - 1].getSize() : 0;
  }

  public int getHeight() {
    return height;
  }

  public PyramidStep[] getSteps() {
    return steps;
  }

  public RouteType getRouteType() {
    return routeType;
  }
}
