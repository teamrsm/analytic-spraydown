package com.sprayme.teamrsm.analyticspraydown;

import com.sprayme.teamrsm.analyticspraydown.models.Grade;
import com.sprayme.teamrsm.analyticspraydown.models.GradeType;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by climbak on 10/18/17.
 */

public class GradeTests {

  @Test
  public void GradeStringConstructor() throws Exception {
    // test that grades are created correctly with valid strings and types
    // RouteYosemite
    // todo mock the preference manager
    Grade grade = new Grade("5.10a", GradeType.RouteYosemite);
    assertEquals("5.10a", grade.toString());
    assertEquals(12, grade.getGradeValue());
    assertEquals(GradeType.RouteYosemite, grade.getGradeType());
    // RouteEuro
    grade = new Grade("6a", GradeType.RouteEuropean);
    assertEquals("6a", grade.toString());
    assertEquals(12, grade.getGradeValue());
    assertEquals(GradeType.RouteEuropean, grade.getGradeType());
    // BoulderHueco
    grade = new Grade("V5", GradeType.BoulderHueco);
    assertEquals("V5", grade.toString());
    assertEquals(18, grade.getGradeValue());
    assertEquals(GradeType.BoulderHueco, grade.getGradeType());
    // BoulderFont
    grade = new Grade("6a", GradeType.BoulderFont);
    assertEquals("6a", grade.toString());
    assertEquals(12, grade.getGradeValue());
    assertEquals(GradeType.BoulderFont, grade.getGradeType());

    // test grades created correctly with valid grade values and types
    // RouteYosemite
    grade = new Grade(12, GradeType.RouteYosemite);
    assertEquals("5.10a", grade.toString());
    assertEquals(12, grade.getGradeValue());
    assertEquals(GradeType.RouteYosemite, grade.getGradeType());
    // RouteEuro
    grade = new Grade(12, GradeType.RouteEuropean);
    assertEquals("6a", grade.toString());
    assertEquals(12, grade.getGradeValue());
    assertEquals(GradeType.RouteEuropean, grade.getGradeType());
    // BoulderHueco
    grade = new Grade(18, GradeType.BoulderHueco);
    assertEquals("V5", grade.toString());
    assertEquals(18, grade.getGradeValue());
    assertEquals(GradeType.BoulderHueco, grade.getGradeType());
    // BoulderFont
    grade = new Grade(12, GradeType.BoulderFont);
    assertEquals("6a", grade.toString());
    assertEquals(12, grade.getGradeValue());
    assertEquals(GradeType.BoulderFont, grade.getGradeType());
  }
}
