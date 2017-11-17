package com.sprayme.teamrsm.analyticspraydown.utilities;

import com.sprayme.teamrsm.analyticspraydown.models.Grade;
import com.sprayme.teamrsm.analyticspraydown.models.GradeType;
import com.sprayme.teamrsm.analyticspraydown.models.RouteType;

import java.util.HashMap;
import java.util.Set;

import static com.sprayme.teamrsm.analyticspraydown.models.GradeType.RouteEuropean;

/**
 * Created by climbak on 10/5/17.
 */

public class GradeManager {

  // todo make resources

  public static int getGradeValue(String gradeStr, GradeType type) {
    int index = gradeStr.indexOf(" ");
    if (index > 0)
      gradeStr = gradeStr.substring(0, index);
    index = gradeStr.indexOf("-");
    if (index > 0)
      gradeStr = gradeStr.substring(0, index);
    index = gradeStr.indexOf("+");
    if (index > 0)
      gradeStr = gradeStr.substring(0, index);

    // todo come up with a better way to remove danger ratings
    gradeStr = gradeStr.replace("PG13", "").replace("R", "").replace("X", "");

    switch (type) {
      case RouteYosemite:
        if (yosemiteGrades.containsKey(gradeStr))
          return yosemiteGrades.get(gradeStr);
        else
          return 0;
      case RouteEuropean:
        if (euroGrades.containsKey(gradeStr))
          return euroGrades.get(gradeStr);
        else
          return 0;
      case BoulderHueco:
        gradeStr = gradeStr.replace("-", "").replace("+", "");
        if (huecoGrades.containsKey(gradeStr))
          return huecoGrades.get(gradeStr);
        else
          return 0;
      case BoulderFont:
        if (fontGrades.containsKey(gradeStr))
          return fontGrades.get(gradeStr);
        else
          return 0;
      case Ice:
        if (waterIceGrades.containsKey(gradeStr))
          return waterIceGrades.get(gradeStr);
        else
          return 0;
      default:
        return 0;
    }
  }

  public static String getGradeString(int gradeValue, GradeType type) {
    switch (type) {
      case RouteYosemite:
        if (yosemiteReverseGrades.containsKey(gradeValue))
          return yosemiteReverseGrades.get(gradeValue);
        return null;
      case RouteEuropean:
        if (euroReverseGrades.containsKey(gradeValue))
          return euroReverseGrades.get(gradeValue);
        return null;
      case BoulderHueco:
        if (huecoReverseGrades.containsKey(gradeValue))
          return huecoReverseGrades.get(gradeValue);
        return null;
      case BoulderFont:
        if (fontReverseGrades.containsKey(gradeValue))
          return fontReverseGrades.get(gradeValue);
        return null;
      case Ice:
        if (waterIceReverseGrades.containsKey(gradeValue))
          return waterIceReverseGrades.get(gradeValue);
        return null;
      default:
        return null;
    }
  }

  public static Grade toAmerican(Grade grade) {
    RouteType type = grade.getRouteType();
    switch (type) {
      case Sport:
      case Trad:
        return routeToYosemite(grade);
      case Boulder:
        return boulderToHueco(grade);
      case Ice:
        return grade; // also handle AI
      case Aid:
        return grade;
      case Mixed:
        return grade;
      default:
        return null;
    }
  }

  public static Grade toEuro(Grade grade) {
    RouteType type = grade.getRouteType();
    switch (type) {
      case Sport:
      case Trad:
        return routeToEuro(grade);
      case Boulder:
        return boulderToFont(grade);
      case Ice:
        return grade; // also handle AI
      case Aid:
        return grade;
      case Mixed:
        return grade;
      default:
        return null;
    }
  }

  // todo implement conversions
  private static Grade routeToEuro(Grade grade) {
    if (grade.getRouteType() != RouteType.Sport && grade.getRouteType() != RouteType.Trad)
      return null;
    if (grade.getGradeType() == RouteEuropean)
      return grade;

    return null;
  }

  private static Grade routeToYosemite(Grade grade) {
    return null;
  }

  private static Grade boulderToFont(Grade grade) {
    return null;
  }

  private static Grade boulderToHueco(Grade grade) {
    return null;
  }

  private static HashMap<String, Integer> yosemiteGrades = createYosemiteMap();

  private static HashMap<String, Integer> createYosemiteMap() {
    HashMap<String, Integer> grades = new HashMap<>();
    grades.put("5.1", 1);
    grades.put("5.2", 2);
    grades.put("5.3", 3);
    grades.put("5.4", 4);
    grades.put("5.5", 5);
    grades.put("5.6", 6);
    grades.put("5.7", 7);
    grades.put("5.8", 8);
    grades.put("5.9", 9);
    grades.put("5.10a", 10);
    grades.put("5.10b", 11);
    grades.put("5.10c", 12);
    grades.put("5.10d", 13);
    grades.put("5.11a", 14);
    grades.put("5.11b", 15);
    grades.put("5.11c", 16);
    grades.put("5.11d", 17);
    grades.put("5.12a", 18);
    grades.put("5.12b", 19);
    grades.put("5.12c", 20);
    grades.put("5.12d", 21);
    grades.put("5.13a", 22);
    grades.put("5.13b", 23);
    grades.put("5.13c", 24);
    grades.put("5.13d", 25);
    grades.put("5.14a", 26);
    grades.put("5.14b", 27);
    grades.put("5.14c", 28);
    grades.put("5.14d", 29);
    grades.put("5.15a", 30);
    grades.put("5.15b", 31);
    grades.put("5.15c", 32);
    grades.put("5.15d", 33);
    grades.put("5.9-", 9);
    grades.put("5.9+", 9);
    grades.put("5.10-", 10);
    grades.put("5.10", 12);
    grades.put("5.10+", 13);
    grades.put("5.11-", 14);
    grades.put("5.11", 16);
    grades.put("5.11+", 17);
    grades.put("5.12-", 18);
    grades.put("5.12", 20);
    grades.put("5.12+", 21);
    grades.put("5.13-", 22);
    grades.put("5.13", 24);
    grades.put("5.13+", 25);
    grades.put("5.14-", 26);
    grades.put("5.14", 28);
    grades.put("5.14+", 29);
    grades.put("5.15-", 30);
    grades.put("5.15", 32);
    grades.put("5.15+", 33);
    return grades;
  }

  private static HashMap<Integer, String> yosemiteReverseGrades = createYosemiteReverseMap();

  private static HashMap<Integer, String> createYosemiteReverseMap() {
    HashMap<Integer, String> grades = new HashMap<>();
    grades.put(1, "5.1");
    grades.put(2, "5.2");
    grades.put(3, "5.3");
    grades.put(4, "5.4");
    grades.put(5, "5.5");
    grades.put(6, "5.6");
    grades.put(7, "5.7");
    grades.put(8, "5.8");
    grades.put(9, "5.9");
    grades.put(10, "5.10a");
    grades.put(11, "5.10b");
    grades.put(12, "5.10c");
    grades.put(13, "5.10d");
    grades.put(14, "5.11a");
    grades.put(15, "5.11b");
    grades.put(16, "5.11c");
    grades.put(17, "5.11d");
    grades.put(18, "5.12a");
    grades.put(19, "5.12b");
    grades.put(20, "5.12c");
    grades.put(21, "5.12d");
    grades.put(22, "5.13a");
    grades.put(23, "5.13b");
    grades.put(24, "5.13c");
    grades.put(25, "5.13d");
    grades.put(26, "5.14a");
    grades.put(27, "5.14b");
    grades.put(28, "5.14c");
    grades.put(29, "5.14d");
    grades.put(30, "5.15a");
    grades.put(31, "5.15b");
    grades.put(32, "5.15c");
    grades.put(33, "5.15d");
    return grades;
  }

  private static HashMap<String, Integer> euroGrades = createEuroMap();

  private static HashMap<String, Integer> createEuroMap() {
    HashMap<String, Integer> grades = new HashMap<String, Integer>();
    grades.put("4a", 4);
    grades.put("4b", 5);
    grades.put("4c", 6);
    grades.put("5a", 7);
    grades.put("5b", 8);
    grades.put("5c", 9);
    grades.put("6a", 10);
    grades.put("6a+", 11);
    grades.put("6b", 12);
    grades.put("6b+", 13);
    grades.put("6c", 14);
//        grades.put("6c+",15);
    grades.put("6c+", 16);
    grades.put("7a", 17);
    grades.put("7a+", 18);
    grades.put("7b", 19);
    grades.put("7b+", 20);
    grades.put("7c", 21);
    grades.put("7c+", 22);
    grades.put("8a", 23);
    grades.put("8a+", 24);
    grades.put("8b", 25);
    grades.put("8b+", 26);
    grades.put("8c", 27);
    grades.put("8c+", 28);
    grades.put("9a", 29);
    grades.put("9a+", 30);
    grades.put("9b", 31);
    grades.put("9b+", 32);
    grades.put("9c", 33);
    return grades;
  }

  private static HashMap<Integer, String> euroReverseGrades = createReverseEuroMap();

  private static HashMap<Integer, String> createReverseEuroMap() {
    HashMap<Integer, String> grades = new HashMap<>();
    grades.put(4, "4a");
    grades.put(5, "4b");
    grades.put(6, "4c");
    grades.put(7, "5a");
    grades.put(8, "5b");
    grades.put(9, "5c");
    grades.put(10, "6a");
    grades.put(11, "6a+");
    grades.put(12, "6b");
    grades.put(13, "6b+");
    grades.put(14, "6c");
    grades.put(15, "6c+");
    grades.put(16, "6c+");
    grades.put(17, "7a");
    grades.put(18, "7a+");
    grades.put(19, "7b");
    grades.put(20, "7b+");
    grades.put(21, "7c");
    grades.put(22, "7c+");
    grades.put(23, "8a");
    grades.put(24, "8a+");
    grades.put(25, "8b");
    grades.put(26, "8b+");
    grades.put(27, "8c");
    grades.put(28, "8c+");
    grades.put(29, "9a");
    grades.put(30, "9a+");
    grades.put(31, "9b");
    grades.put(32, "9b+");
    grades.put(33, "9c");
    return grades;
  }

  private static HashMap<String, Integer> huecoGrades = createHuecoMap();

  private static HashMap<String, Integer> createHuecoMap() {
    HashMap<String, Integer> grades = new HashMap<String, Integer>();
    grades.put("VB", 1);
    grades.put("V0-", 2);
    grades.put("V0", 3);
    grades.put("V0+", 4);
    grades.put("V1-", 5);
    grades.put("V1", 6);
    grades.put("V1+", 7);
    grades.put("V2-", 8);
    grades.put("V2", 9);
    grades.put("V2+", 10);
    grades.put("V3-", 11);
    grades.put("V3", 12);
    grades.put("V3+", 13);
    grades.put("V4-", 14);
    grades.put("V4", 15);
    grades.put("V4+", 16);
    grades.put("V5-", 17);
    grades.put("V5", 18);
    grades.put("V5+", 19);
    grades.put("V6-", 20);
    grades.put("V6", 21);
    grades.put("V6+", 22);
    grades.put("V7-", 23);
    grades.put("V7", 24);
    grades.put("V7+", 25);
    grades.put("V8-", 26);
    grades.put("V8", 27);
    grades.put("V8+", 28);
    grades.put("V9-", 29);
    grades.put("V9", 30);
    grades.put("V9+", 31);
    grades.put("V10-", 32);
    grades.put("V10", 33);
    grades.put("V10+", 34);
    grades.put("V11-", 35);
    grades.put("V11", 36);
    grades.put("V11+", 37);
    grades.put("V12-", 38);
    grades.put("V12", 39);
    grades.put("V12+", 40);
    grades.put("V13-", 41);
    grades.put("V13", 42);
    grades.put("V13+", 43);
    grades.put("V14-", 44);
    grades.put("V14", 45);
    grades.put("V14+", 46);
    grades.put("V15-", 47);
    grades.put("V15", 48);
    grades.put("V15+", 49);
    grades.put("V16-", 50);
    grades.put("V16", 51);
    grades.put("V16+", 52);
    grades.put("V17-", 53);
    grades.put("V17", 54);
    grades.put("V17+", 55);
    return grades;
  }

  private static HashMap<Integer, String> huecoReverseGrades = createReverseHuecoMap();

  private static HashMap<Integer, String> createReverseHuecoMap() {
    HashMap<Integer, String> grades = new HashMap<>();
    grades.put(1, "VB");
    grades.put(2, "V0-");
    grades.put(3, "V0");
    grades.put(4, "V0+");
    grades.put(5, "V1-");
    grades.put(6, "V1");
    grades.put(7, "V1+");
    grades.put(8, "V2-");
    grades.put(9, "V2");
    grades.put(10, "V2+");
    grades.put(11, "V3-");
    grades.put(12, "V3");
    grades.put(13, "V3+");
    grades.put(14, "V4-");
    grades.put(15, "V4");
    grades.put(16, "V4+");
    grades.put(17, "V5-");
    grades.put(18, "V5");
    grades.put(19, "V5+");
    grades.put(20, "V6-");
    grades.put(21, "V6");
    grades.put(22, "V6+");
    grades.put(23, "V7-");
    grades.put(24, "V7");
    grades.put(25, "V7+");
    grades.put(26, "V8-");
    grades.put(27, "V8");
    grades.put(28, "V8+");
    grades.put(29, "V9-");
    grades.put(30, "V9");
    grades.put(31, "V9+");
    grades.put(32, "V10-");
    grades.put(33, "V10");
    grades.put(34, "V10+");
    grades.put(35, "V11-");
    grades.put(36, "V11");
    grades.put(37, "V11+");
    grades.put(38, "V12-");
    grades.put(39, "V12");
    grades.put(40, "V12+");
    grades.put(41, "V13-");
    grades.put(42, "V13");
    grades.put(43, "V13+");
    grades.put(44, "V14-");
    grades.put(45, "V14");
    grades.put(46, "V14+");
    grades.put(47, "V15-");
    grades.put(48, "V15");
    grades.put(49, "V15+");
    grades.put(50, "V16-");
    grades.put(51, "V16");
    grades.put(52, "V16+");
    grades.put(53, "V17-");
    grades.put(54, "V17");
    grades.put(55, "V17+");
    return grades;
  }

  private static HashMap<String, Integer> fontGrades = createFontGrades();

  private static HashMap<String, Integer> createFontGrades() {
    HashMap<String, Integer> grades = new HashMap<String, Integer>();
    grades.put("3", 1);
    grades.put("4-", 3);
    grades.put("4", 4);
    grades.put("5-", 5);
    grades.put("5", 6);
    grades.put("5+", 9);
    grades.put("6A", 12);
    grades.put("6A+", 13);
    grades.put("6B-", 14);
    grades.put("6B", 15);
    grades.put("6B+", 16);
    grades.put("6C", 18);
    grades.put("6C+", 19);
    grades.put("7A", 21);
    grades.put("7A+", 24);
    grades.put("7B", 27);
    grades.put("7B+", 28);
    grades.put("7C", 30);
    grades.put("7C+", 33);
    grades.put("8A", 36);
    grades.put("8A+", 39);
    grades.put("8B", 42);
    grades.put("8B+", 45);
    grades.put("8C", 48);
    grades.put("8C+", 51);
    grades.put("9A", 54);
    return grades;
  }

  private static HashMap<Integer, String> fontReverseGrades = createReverseFontGrades();

  private static HashMap<Integer, String> createReverseFontGrades() {
    HashMap<Integer, String> grades = new HashMap<>();
    grades.put(1, "3");
    grades.put(3, "4-");
    grades.put(4, "4");
    grades.put(5, "5-");
    grades.put(6, "5");
    grades.put(9, "5+");
    grades.put(12, "6A");
    grades.put(13, "6A+");
    grades.put(14, "6B-");
    grades.put(15, "6B");
    grades.put(16, "6B+");
    grades.put(18, "6C");
    grades.put(19, "6C+");
    grades.put(21, "7A");
    grades.put(24, "7A+");
    grades.put(27, "7B");
    grades.put(28, "7B+");
    grades.put(30, "7C");
    grades.put(33, "7C+");
    grades.put(36, "8A");
    grades.put(39, "8A+");
    grades.put(42, "8B");
    grades.put(45, "8B+");
    grades.put(48, "8C");
    grades.put(51, "8C+");
    grades.put(54, "9A");
    return grades;
  }

  private static HashMap<String, Integer> waterIceGrades = createWaterIceGrades();

  private static HashMap<String, Integer> createWaterIceGrades() {
    HashMap<String, Integer> grades = new HashMap<String, Integer>();
    grades.put("WI0", 1);
    grades.put("WI1", 2);
    grades.put("WI2", 3);
    grades.put("WI3", 4);
    grades.put("WI4", 5);
    grades.put("WI5", 6);
    grades.put("WI6", 7);
    grades.put("WI7", 8);
    grades.put("WI8", 9);
    return grades;
  }

  private static HashMap<Integer, String> waterIceReverseGrades = createReverseWaterIceGrades();

  private static HashMap<Integer, String> createReverseWaterIceGrades() {
    HashMap<Integer, String> grades = new HashMap<>();
    grades.put(1, "WI0");
    grades.put(2, "WI1");
    grades.put(3, "WI2");
    grades.put(4, "WI3");
    grades.put(5, "WI4");
    grades.put(6, "WI5");
    grades.put(7, "WI6");
    grades.put(8, "WI7");
    grades.put(9, "WI8");
    return grades;
  }

}
