package com.sprayme.teamrsm.analyticspraydown.utilities;

import com.sprayme.teamrsm.analyticspraydown.models.Grade;
import com.sprayme.teamrsm.analyticspraydown.models.RouteType;

import java.util.Dictionary;
import java.util.HashMap;

/**
 * Created by climbak on 10/5/17.
 */

public class GradeManager {


    // todo make resources
    public static String toAmerican(Grade grade) {
        RouteType type = grade.getRouteType();
        switch (type) {
            case Sport:
            case Trad:
                return "5.todo";
            case Boulder:
                return "Vtodo";
            case Ice:
                return "WItodo"; // also handle AI
            case Aid:
                return "Atodo";
            case Mixed:
                return "Mtodo";
            default:
                return "Unknown";
        }
    }

    public static String toEuro(Grade grade){
        RouteType type = grade.getRouteType();
        switch (type) {
            case Sport:
            case Trad:
                return "7todo";
            case Boulder:
                return "8todo";
            case Ice:
                return "WItodo"; // also handle AI
            case Aid:
                return "Atodo";
            case Mixed:
                return "Mtodo";
            default:
                return "Unknown";
        }
    }

    public static int getGradeValue(String gradeStr){
        if (grades.containsKey(gradeStr))
            return grades.get(gradeStr);
        else
            return 0;
    }

    private static HashMap<String, Integer> grades = createMap();
    private static HashMap<String, Integer> createMap(){
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
        grades.put("5.10-",13);
        grades.put("5.10b",14);
        grades.put("5.10",15);
        grades.put("5.10c",16);
        grades.put("5.10+",17);
        grades.put("5.10d",18);
        grades.put("5.11a",19);
        grades.put("5.11-",20);
        grades.put("5.11b",21);
        grades.put("5.11",22);
        grades.put("5.11c",23);
        grades.put("5.11+",24);
        grades.put("5.11d",25);
        grades.put("5.12a",26);
        grades.put("5.12-",27);
        grades.put("5.12b",28);
        grades.put("5.12",29);
        grades.put("5.12c",30);
        grades.put("5.12+",31);
        grades.put("5.12d",32);
        grades.put("5.13a",33);
        grades.put("5.13-",34);
        grades.put("5.13b",35);
        grades.put("5.13",36);
        grades.put("5.13c", 37);
        return grades;
    }
}
