package com.sprayme.teamrsm.analyticspraydown.models;

import com.sprayme.teamrsm.analyticspraydown.utilities.GradeManager;

/**
 * Created by climbak on 10/5/17.
 */

public class Grade {
    private String gradeString;
    private GradeType type;
    private RouteType routeType;
    private int gradeValue; // todo do we want this to be an int?

    public Grade(String gradeString, GradeType type){ // todo do we want these to automatically get the type or store the grade values in separate lookups?
        this.gradeString = gradeString;
        this.type = type;
        // todo do lookup to get value and route type
        gradeValue = GradeManager.getGradeValue(gradeString, type);
    }

    public Grade(int gradeValue, GradeType type){
        this.gradeValue = gradeValue;
        this.type = type;
        // todo do lookup to get string and route type
        gradeString = GradeManager.getGradeString(gradeValue, type);
    }

    public int compareTo(Grade otherGrade) throws IllegalArgumentException{
        if (routeType != otherGrade.getRouteType())
            throw new IllegalArgumentException("Can't compare different grade types");
        int otherGradeValue = otherGrade.getGradeValue();
        if (gradeValue == otherGradeValue)
            return 0;
        else if (gradeValue > otherGradeValue)
            return 1;
        else
            return -1;
    }

    public int getGradeValue() {
        return gradeValue;
    }

    public GradeType getGradeType() {
        return type;
    }

    public RouteType getRouteType() {
        return routeType;
    }

    public Grade nextHardest(){
        // todo protect from max values
        return new Grade(gradeValue + 1, type);
    }

    public Grade nextEasiest(){
        // todo protect from min values
        return new Grade(gradeValue - 1, type);
    }

    @Override
    public String toString(){ return gradeString; }

    public String toShortString() { return gradeString.replace("5.", ""); }
}
