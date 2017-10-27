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
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by climbak on 10/12/17.
 */

public class SprayarificStructures {

  public static Pyramid buildPyramid(List<Route> routes, RouteType type, int height, int stepChangeSize, PyramidStepType stepModifier) {
    List<Route> filteredRoutes = routes;
    if (MainActivity.mSharedPref.getBoolean(SettingsActivity.KEY_PREF_IGNORE_DUPLICATES, true)) {
      filteredRoutes = routes.stream()
              .filter((route) -> Objects.nonNull(route))
              .filter((route) -> route.getType() == type)
              .collect(Collectors.toList());
    }

    Route hardestRoute = filteredRoutes.stream().max(
            (route1, route2) -> route1.getGrade().compareTo(route2.getGrade())).orElse(null);
    long hardestCount = filteredRoutes.stream().filter((route) -> route.getGrade().compareTo(hardestRoute.getGrade()) == 0).count();

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
        default:
          break;
      }
    if (hardestCount > 1 || MainActivity.mSharedPref.getBoolean(SettingsActivity.KEY_PREF_ALWAYS_BUILD_OPTIMISTIC, true))
      return new Pyramid(filteredRoutes, height, stepChangeSize, stepModifier, type, hardestGrade.nextHardest());
    else
      return new Pyramid(filteredRoutes, height, stepChangeSize, stepModifier, type);


  }

  public static Pyramid buildPyramid(List<Route> routes, RouteType type, int height, int stepChangeSize, PyramidStepType stepModifier, Grade goal) {
    List<Route> filteredRoutes = routes.stream()
            .filter((route) -> route.getType() == type)
            .collect(Collectors.toList());

    return new Pyramid(filteredRoutes, height, stepChangeSize, stepModifier, type, goal);
  }
}
