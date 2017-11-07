package com.sprayme.teamrsm.analyticspraydown.utilities;

import com.sprayme.teamrsm.analyticspraydown.MainActivity;
import com.sprayme.teamrsm.analyticspraydown.SettingsActivity;
import com.sprayme.teamrsm.analyticspraydown.models.Grade;
import com.sprayme.teamrsm.analyticspraydown.models.GradeType;
import com.sprayme.teamrsm.analyticspraydown.models.Pyramid;
import com.sprayme.teamrsm.analyticspraydown.models.PyramidStepType;
import com.sprayme.teamrsm.analyticspraydown.models.Route;
import com.sprayme.teamrsm.analyticspraydown.models.RouteType;

import java.util.List;

import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

/**
 * Created by climbak on 10/12/17.
 */

public class SprayarificStructures {

  public static Pyramid buildPyramid(List<Route> routes, RouteType type, int height, int stepChangeSize, PyramidStepType stepModifier) {
    List<Route> filteredRoutes = routes;
    if (MainActivity.mSharedPref.getBoolean(SettingsActivity.KEY_PREF_IGNORE_DUPLICATES, true)) {
      filteredRoutes = StreamSupport.stream(routes)
              .filter((route) -> route != null)
              .filter((route) -> {
                if (type == RouteType.Route)
                  return route.getType() == RouteType.Sport || route.getType() == RouteType.Trad;
                return route.getType() == type;
              })
              .collect(Collectors.toList());
    }

    Route hardestRoute = StreamSupport.stream(filteredRoutes).max(
            (route1, route2) -> route1.getGrade().compareTo(route2.getGrade())).orElse(null);
    long hardestCount = StreamSupport.stream(filteredRoutes).filter((route) -> route.getGrade().compareTo(hardestRoute.getGrade()) == 0).count();

    Grade hardestGrade = hardestRoute != null ? hardestRoute.getGrade() : null;
    if (hardestGrade == null)
      switch (type) {
        case Sport:
        case Trad:
          hardestGrade = new Grade("5.10a", GradeType.RouteYosemite);
          break;
        case Boulder:
          hardestGrade = new Grade("V4", GradeType.BoulderHueco);
          break;
        case Ice:
          hardestGrade = new Grade("WI3", GradeType.Ice);
        default:
          break;
      }
    if (hardestCount > 1 || MainActivity.mSharedPref.getBoolean(SettingsActivity.KEY_PREF_ALWAYS_BUILD_OPTIMISTIC, true))
      return new Pyramid(filteredRoutes, height, stepChangeSize, stepModifier, type, hardestGrade.nextHardest());
    else
      return new Pyramid(filteredRoutes, height, stepChangeSize, stepModifier, type);


  }

  public static Pyramid buildPyramid(List<Route> routes, RouteType type, int height, int stepChangeSize, PyramidStepType stepModifier, Grade goal) {
    List<Route> filteredRoutes = StreamSupport.stream(routes)
            .filter((route) -> route.getType() == type)
            .collect(Collectors.toList());

    return new Pyramid(filteredRoutes, height, stepChangeSize, stepModifier, type, goal);
  }
}
