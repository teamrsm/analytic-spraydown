package com.sprayme.teamrsm.analyticspraydown.models;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by climbak on 10/5/17.
 */

public class Pyramid {
    private int height;
    private int stepChangeSize;
    private PyramidStepType stepType;
    private PyramidStep[] steps;

    public Pyramid (List<Route> routes, int height, int stepChangeSize, PyramidStepType stepType){
        this.height = height;
        this.stepChangeSize = stepChangeSize;
        this.stepType = stepType;

        Route hardestRoute = routes.stream().max(
                (route1, route2) -> route1.getGrade().compareTo(route2.getGrade())).orElse(null);
        if (hardestRoute == null)
            return; // todo do something useful here

        Grade hardestGrade = hardestRoute.getGrade();

        // limit our height to the easiest grade of this type
//        height = height >= hardestGrade.getGradeValue() ? height : hardestGrade.getGradeValue();

        steps = buildPyramidSteps(routes, height, stepChangeSize, stepType, hardestGrade);
    }

    public Pyramid (List<Route> routes, int height, int stepChangeSize, PyramidStepType stepType, Grade goal){
        this.height = height;
        this.stepChangeSize = stepChangeSize;
        this.stepType = stepType;

        Grade currentGrade = goal;

        // limit our height to the easiest grade of this type
//        height = height >= currentGrade.getGradeValue() ? height : currentGrade.getGradeValue();

        steps = buildPyramidSteps(routes, height, stepChangeSize, stepType, goal);
    }

    private PyramidStep[] buildPyramidSteps(List<Route> routes, int height, int stepChangeSize, PyramidStepType stepType, Grade startingGrade) {
        PyramidStep[] steps = new PyramidStep[height];
        Grade grade = startingGrade;
        int size = 1;
        for (int i=0; i<height; i++) {
            Grade currentGrade = grade;

            List<Route> stepRoutes = routes.stream()
                    .filter((route) -> route.getGrade().compareTo(currentGrade) == 0)
                    .collect(Collectors.toList());

            steps[i] = new PyramidStep(size, currentGrade, stepRoutes);
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

    public int getWidth(){
        return steps[steps.length-1].getSize();
    }

    public int getHeight(){
        return height;
    }

    public PyramidStep[] getSteps(){
        return steps;
    }
}
