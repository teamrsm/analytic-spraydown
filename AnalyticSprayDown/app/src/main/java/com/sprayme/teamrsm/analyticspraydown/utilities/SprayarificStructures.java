package com.sprayme.teamrsm.analyticspraydown.utilities;

import com.sprayme.teamrsm.analyticspraydown.MainActivity;
import com.sprayme.teamrsm.analyticspraydown.SettingsActivity;
import com.sprayme.teamrsm.analyticspraydown.models.Grade;
import com.sprayme.teamrsm.analyticspraydown.models.GradeType;
import com.sprayme.teamrsm.analyticspraydown.models.Pyramid;
import com.sprayme.teamrsm.analyticspraydown.models.PyramidStepType;
import com.sprayme.teamrsm.analyticspraydown.models.Route;
import com.sprayme.teamrsm.analyticspraydown.models.RouteType;
import com.sprayme.teamrsm.analyticspraydown.models.Tick;

import java.util.List;

import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

/**
 * Created by climbak on 10/12/17.
 */

public class SprayarificStructures {

  public static Pyramid buildPyramid(List<Tick> ticks, RouteType type, int height, int stepChangeSize, PyramidStepType stepModifier) {
    List<Tick> filteredTicks = ticks;
    // todo fix this and make it actually filter duplicates
//    if (MainActivity.mSharedPref.getBoolean(SettingsActivity.KEY_PREF_IGNORE_DUPLICATES, true)) {
      filteredTicks = StreamSupport.stream(ticks)
              .filter((tick) -> tick != null && tick.getRoute() != null)
              .filter((tick) -> {
                if (type == RouteType.Route)
                  return tick.getRoute().getType() == RouteType.Sport || tick.getRoute().getType() == RouteType.Trad;
                return tick.getRoute().getType() == type;
              })
              .collect(Collectors.toList());
//    }

    Tick hardestTick = StreamSupport.stream(filteredTicks).max(
            (tick1, tick2) -> tick1.getRoute().getGrade().compareTo(tick2.getRoute().getGrade())).orElse(null);
    long hardestCount = StreamSupport.stream(filteredTicks).filter((tick) -> tick.getRoute().getGrade().compareTo(hardestTick.getRoute().getGrade()) == 0).count();

    Grade hardestGrade = hardestTick != null && hardestTick.getRoute() != null ? hardestTick.getRoute().getGrade() : null;
    if (hardestGrade == null)
      switch (type) {
        case Route:
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
      return new Pyramid(filteredTicks, height, stepChangeSize, stepModifier, type, hardestGrade.nextHardest());
    else
      return new Pyramid(filteredTicks, height, stepChangeSize, stepModifier, type);


  }

  public static Pyramid buildPyramid(List<Tick> ticks, RouteType type, int height, int stepChangeSize, PyramidStepType stepModifier, Grade goal) {
    List<Tick> filteredTicks = ticks;
    filteredTicks = StreamSupport.stream(ticks)
            .filter((tick) -> tick != null && tick.getRoute() != null)
            .filter((tick) -> {
              if (type == RouteType.Route)
                return tick.getRoute().getType() == RouteType.Sport || tick.getRoute().getType() == RouteType.Trad;
              return tick.getRoute().getType() == type;
            })
            .collect(Collectors.toList());

    return new Pyramid(filteredTicks, height, stepChangeSize, stepModifier, type, goal);
  }
}
