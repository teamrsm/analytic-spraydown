package com.sprayme.teamrsm.analyticspraydown.utilities;

import com.sprayme.teamrsm.analyticspraydown.models.Grade;
import com.sprayme.teamrsm.analyticspraydown.models.RouteType;

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
}
