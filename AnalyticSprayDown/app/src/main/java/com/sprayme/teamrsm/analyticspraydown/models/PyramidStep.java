package com.sprayme.teamrsm.analyticspraydown.models;

import java.util.List;

/**
 * Created by climbak on 10/5/17.
 */

public class PyramidStep {
    private int size;
    private Grade grade;
    private List<Route> routes;

    public PyramidStep(int size, Grade grade, List<Route> routes){
        this.size = size;
        this.grade = grade;
        this.routes = routes;
        // todo what do we do if we get a list of routes that aren't of the same grade?
        // todo what to do if the route list is empty?
    }
}
