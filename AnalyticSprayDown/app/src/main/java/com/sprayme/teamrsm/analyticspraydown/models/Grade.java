package com.sprayme.teamrsm.analyticspraydown.models;

import com.sprayme.teamrsm.analyticspraydown.utilities.GradeManager;

/**
 * Created by climbak on 10/5/17.
 */

public class Grade {
    private String gradeString;
    private RouteType type;
    private int gradeValue; // todo do we want this to be an int?

    public Grade(String gradeString){ // todo do we want these to automatically get the type or store the grade values in separate lookups?
        this.gradeString = gradeString;
        // todo do lookup to get value and route type
        gradeValue = GradeManager.getGradeValue(gradeString);
    }

    public Grade(int gradeValue){
        this.gradeValue = gradeValue;
        // todo do lookup to get string and route type
        gradeString = GradeManager.getGradeString(gradeValue);
    }

    public int compareTo(Grade otherGrade) throws IllegalArgumentException{
        if (type != otherGrade.getRouteType())
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

    public RouteType getRouteType() {
        return type;
    }

    public Grade nextHardest(){
        // todo protect from max values
        return new Grade(gradeValue + 1);
    }

    public Grade nextEasiest(){
        // todo protect from min values
        return new Grade(gradeValue - 1);
    }

    @Override
    public String toString(){ return gradeString; }

    public String toShortString() { return gradeString.replace("5.", ""); }
}
