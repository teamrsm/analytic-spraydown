package com.sprayme.teamrsm.analyticspraydown.models;

import com.sprayme.teamrsm.analyticspraydown.MainActivity;
import com.sprayme.teamrsm.analyticspraydown.SettingsActivity;
import com.sprayme.teamrsm.analyticspraydown.utilities.GradeManager;

/**
 * Created by climbak on 10/5/17.
 */

public class Grade {
  private String gradeString;
  private GradeType type;
  private RouteType routeType;
  private int gradeValue; // todo do we want this to be an int?

  public Grade(String gradeString, GradeType type) { // todo do we want these to automatically get the type or store the grade values in separate lookups?
    this.gradeString = gradeString;
    this.type = type;
    // todo do lookup to get value and route type
    gradeValue = GradeManager.getGradeValue(gradeString, type);
  }

  public Grade(int gradeValue, GradeType type) {
    this.gradeValue = gradeValue;
    this.type = type;
    // todo do lookup to get string and route type
    gradeString = GradeManager.getGradeString(gradeValue, type);
  }

  public int compareTo(Grade otherGrade) throws IllegalArgumentException {
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

  public Grade nextHardest() {
    // todo protect from max values and support euro grades
    if (type != GradeType.BoulderHueco)
      return new Grade(gradeValue + 1, type);
    else {
      Grade grade = new Grade(gradeValue + 1, type);
      while (grade.toString().contains("-") || grade.toString().contains("+"))
        grade = new Grade(grade.getGradeValue() + 1, type);
      return grade;
    }
  }

  public Grade nextEasiest() {
    // todo protect from min values and support euro grades better than this
    if (gradeValue <= 1)
      return null;
    if (type != GradeType.BoulderHueco)
      return new Grade(gradeValue - 1, type);
    else {
      Grade grade = new Grade(gradeValue - 1, type);
      while (grade.toString().contains("-") || grade.toString().contains("+"))
        grade = new Grade(grade.getGradeValue() - 1, type);
      return grade;
    }
  }

  @Override
  public String toString() {
    if ((type == GradeType.RouteYosemite || type == GradeType.BoulderHueco) &&
            MainActivity.mSharedPref.getBoolean(SettingsActivity.KEY_PREF_GRADE_LOCALE, false))
      return GradeManager.toEuro(this).toString();
    else if ((type == GradeType.RouteEuropean || type == GradeType.BoulderFont) &&
            !MainActivity.mSharedPref.getBoolean(SettingsActivity.KEY_PREF_GRADE_LOCALE, false))
      return GradeManager.toAmerican(this).toString();
    else
      return gradeString;
  }

  public String toShortString() {
    if ((type == GradeType.RouteYosemite || type == GradeType.BoulderHueco) &&
            MainActivity.mSharedPref.getBoolean(SettingsActivity.KEY_PREF_GRADE_LOCALE, false))
      return GradeManager.toEuro(this).toShortString();
    else if ((type == GradeType.RouteEuropean || type == GradeType.BoulderFont) &&
            !MainActivity.mSharedPref.getBoolean(SettingsActivity.KEY_PREF_GRADE_LOCALE, false))
      return GradeManager.toAmerican(this).toShortString();
    else
      return gradeString.replace("5.", "");
  }
}
