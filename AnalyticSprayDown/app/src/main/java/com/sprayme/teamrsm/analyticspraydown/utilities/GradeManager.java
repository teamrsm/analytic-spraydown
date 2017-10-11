package com.sprayme.teamrsm.analyticspraydown.utilities;

import com.sprayme.teamrsm.analyticspraydown.models.Grade;
import com.sprayme.teamrsm.analyticspraydown.models.GradeType;
import com.sprayme.teamrsm.analyticspraydown.models.RouteType;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by climbak on 10/5/17.
 */

public class GradeManager {

    // todo make resources

    public static int getGradeValue(String gradeStr, GradeType type){
        switch(type) {
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
                if (huecoGrades.containsKey(gradeStr))
                    return huecoGrades.get(gradeStr);
                else
                    return 0;
            case BoulderFont:
                if (fontGrades.containsKey(gradeStr))
                    return fontGrades.get(gradeStr);
                else
                    return 0;
            default:
                return 0;
        }
    }

    public static String getGradeString(int gradeValue, GradeType type){
        Set<String> keys;
        switch (type) {
            case RouteYosemite:
                 keys = yosemiteGrades.keySet();
                for (String key : keys) {
                    if (yosemiteGrades.get(key) == gradeValue)
                        return key;
                }
                return null;
            case RouteEuropean:
                keys = euroGrades.keySet();
                for (String key : keys) {
                    if (euroGrades.get(key) == gradeValue)
                        return key;
                }
                return null;
            case BoulderHueco:
                keys = huecoGrades.keySet();
                for (String key : keys) {
                    if (huecoGrades.get(key) == gradeValue)
                        return key;
                }
                return null;
            case BoulderFont:
                keys = fontGrades.keySet();
                for (String key : keys) {
                    if (fontGrades.get(key) == gradeValue)
                        return key;
                }
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
                return null; // also handle AI
            case Aid:
                return null;
            case Mixed:
                return null;
            default:
                return null;
        }
    }

    public static Grade toEuro(Grade grade){
        RouteType type = grade.getRouteType();
        switch (type) {
            case Sport:
            case Trad:
                return routeToEuro(grade);
            case Boulder:
                return boulderToFont(grade);
            case Ice:
                return null; // also handle AI
            case Aid:
                return null;
            case Mixed:
                return null;
            default:
                return null;
        }
    }

    // todo implement conversions
    private static Grade routeToEuro(Grade grade){
        if (grade.getRouteType() != RouteType.Sport && grade.getRouteType() != RouteType.Trad)
            return null;
        if (grade.getGradeType() == GradeType.RouteEuropean)
            return grade;

        return null;
    }

    private static Grade routeToYosemite(Grade grade){
        return null;
    }

    private static Grade boulderToFont(Grade grade){
        return null;
    }

    private static Grade boulderToHueco(Grade grade){
        return null;
    }

    private static HashMap<String, Integer> yosemiteGrades = createYosemiteMap();
    private static HashMap<String, Integer> createYosemiteMap(){
        HashMap<String, Integer> grades = new HashMap<String, Integer>();
        grades.put("5.1", 1);
        grades.put("5.2", 2);
        grades.put("5.3", 3);
        grades.put("5.4", 4);
        grades.put("5.5",5);
        grades.put("5.6",6);
        grades.put("5.7",7);
        grades.put("5.8",8);
        grades.put("5.9-", 9);
        grades.put("5.9",10);
        grades.put("5.9+", 11);
        grades.put("5.10a",12);
        grades.put("5.10b",13);
        grades.put("5.10c",14);
        grades.put("5.10d",15);
        grades.put("5.11a",16);
        grades.put("5.11b",17);
        grades.put("5.11c",18);
        grades.put("5.11d",19);
        grades.put("5.12a",20);
        grades.put("5.12b",21);
        grades.put("5.12c",22);
        grades.put("5.12d",23);
        grades.put("5.13a",24);
        grades.put("5.13b",25);
        grades.put("5.13c",26);
        grades.put("5.13d",27);
        grades.put("5.14a",28);
        grades.put("5.14b",29);
        grades.put("5.14c",30);
        grades.put("5.14d",31);
        grades.put("5.15a",32);
        grades.put("5.15b",33);
        grades.put("5.15c",34);
        grades.put("5.15d",35);
        grades.put("5.10-",12);
        grades.put("5.10",14);
        grades.put("5.10+",15);
        grades.put("5.11-",16);
        grades.put("5.11",18);
        grades.put("5.11+",19);
        grades.put("5.12-",20);
        grades.put("5.12",22);
        grades.put("5.12+",23);
        grades.put("5.13-",24);
        grades.put("5.13",26);
        grades.put("5.13+",27);
        grades.put("5.14-",28);
        grades.put("5.14",30);
        grades.put("5.14+",31);
        grades.put("5.15-",32);
        grades.put("5.15",34);
        grades.put("5.15+",35);
        return grades;
    }

    private static HashMap<String, Integer> euroGrades = createEuroMap();
    private static HashMap<String, Integer> createEuroMap(){
        HashMap<String, Integer> grades = new HashMap<String, Integer>();
        grades.put("5.1", 1);
        grades.put("5.2", 2);
        grades.put("5.3", 3);
        grades.put("4a", 4);
        grades.put("4b",5);
        grades.put("4c",6);
        grades.put("5a",7);
        grades.put("5b",8);
        grades.put("5c", 10);
        grades.put("6a",12);
        grades.put("6a+",13);
        grades.put("6b",14);
        grades.put("6b+",15);
        grades.put("6c",16);
//        grades.put("6c+",17);
        grades.put("6c+",18);
        grades.put("7a",19);
        grades.put("7a+",20);
        grades.put("7b",21);
        grades.put("7b+",22);
        grades.put("7c",23);
        grades.put("7c+",24);
        grades.put("8a",25);
        grades.put("8a+",26);
        grades.put("8b",27);
        grades.put("8b+",28);
        grades.put("8c",29);
        grades.put("8c+",30);
        grades.put("9a",31);
        grades.put("9a+",32);
        grades.put("9b",33);
        grades.put("9b+",34);
        grades.put("9c",35);
        return grades;
    }

    private static HashMap<String, Integer> huecoGrades = createHuecoMap();
    private static HashMap<String, Integer> createHuecoMap(){
        HashMap<String, Integer> grades = new HashMap<String, Integer>();
        grades.put("VB",1);
        grades.put("V0-",2);
        grades.put("V0",3);
        grades.put("V0+",4);
        grades.put("V1-",5);
        grades.put("V1",6);
        grades.put("V1+",7);
        grades.put("V2-",8);
        grades.put("V2",9);
        grades.put("V2+",10);
        grades.put("V3-",12);
        grades.put("V3",13);
        grades.put("V3+",14);
        grades.put("V4-",15);
        grades.put("V4",16);
        grades.put("V4+",17);
        grades.put("V5-",18);
        grades.put("V5",19);
        grades.put("V5+",20);
        grades.put("V6-",21);
        grades.put("V6",22);
        grades.put("V6+",23);
        grades.put("V7-",24);
        grades.put("V7",25);
        grades.put("V7+",26);
        grades.put("V8-",27);
        grades.put("V8",28);
        grades.put("V8+",29);
        grades.put("V9-",30);
        grades.put("V9",31);
        grades.put("V9+",32);
        grades.put("V10-",33);
        grades.put("V10",34);
        grades.put("V10+",35);
        grades.put("V11-",36);
        grades.put("V11",37);
        grades.put("V11+",38);
        grades.put("V12-",39);
        grades.put("V12",40);
        grades.put("V12+",41);
        grades.put("V13-",42);
        grades.put("V13",43);
        grades.put("V13+",44);
        grades.put("V14-",45);
        grades.put("V14",46);
        grades.put("V14+",47);
        grades.put("V15-",48);
        grades.put("V15",49);
        grades.put("V15+",50);
        grades.put("V16-",51);
        grades.put("V16",52);
        grades.put("V16+",53);
        grades.put("V17-",54);
        grades.put("V17",55);
        grades.put("V17+",56);
        return grades;
    }

    private static HashMap<String, Integer> fontGrades = createFontGrades();
    private static HashMap<String, Integer> createFontGrades(){
        HashMap<String, Integer> grades = new HashMap<String, Integer>();
        grades.put("3",1);
        grades.put("4-",3);
        grades.put("4",4);
        grades.put("5-",5);
        grades.put("5",6);
        grades.put("5+",9);
        grades.put("6A",13);
        grades.put("6A+",14);
        grades.put("6B-",15);
        grades.put("6B",16);
        grades.put("6B+",17);
        grades.put("6C",19);
        grades.put("6C+",20);
        grades.put("7A",22);
        grades.put("7A+",25);
        grades.put("7B",28);
        grades.put("7B+",29);
        grades.put("7C",31);
        grades.put("7C+",34);
        grades.put("8A",37);
        grades.put("8A+",40);
        grades.put("8B",43);
        grades.put("8B+",46);
        grades.put("8C",49);
        grades.put("8C+",52);
        grades.put("9A",55);
        return grades;
    }

}
