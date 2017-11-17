package com.sprayme.teamrsm.analyticspraydown.uicomponents.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.sprayme.teamrsm.analyticspraydown.SettingsActivity;
import com.sprayme.teamrsm.analyticspraydown.models.Pyramid;
import com.sprayme.teamrsm.analyticspraydown.models.PyramidStepType;
import com.sprayme.teamrsm.analyticspraydown.models.Route;
import com.sprayme.teamrsm.analyticspraydown.models.RouteType;
import com.sprayme.teamrsm.analyticspraydown.models.Statistic;
import com.sprayme.teamrsm.analyticspraydown.models.Tick;
import com.sprayme.teamrsm.analyticspraydown.models.TickType;
import com.sprayme.teamrsm.analyticspraydown.models.TimeScale;
import com.sprayme.teamrsm.analyticspraydown.utilities.DataCache;
import com.sprayme.teamrsm.analyticspraydown.utilities.SprayarificStructures;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by climbak on 11/7/17.
 */

public class StatsViewModel extends AndroidViewModel {

  private MutableLiveData<List<Pyramid>> mPyramids = new MutableLiveData<>();
  private TimeScale mTimeScale = TimeScale.Year;
  private MutableLiveData<List<Statistic>> mStatistics = new MutableLiveData<>();

  private DataCache mDataCache = null;
  private SharedPreferences mSharedPref;

  public StatsViewModel(Application app){
    super(app);
    mDataCache = DataCache.getInstance();
    mDataCache.getTicksLiveData().observeForever(ticks -> {
      updatePyramids(ticks);
    });
  }

  public MutableLiveData<List<Pyramid>> getPyramids() {
    return mPyramids;
  }

  public MutableLiveData<List<Statistic>> getStatistics() {
    return mStatistics;
  }

  public void setTimeScale(TimeScale timeScale){
    mTimeScale = timeScale;
    updatePyramids(mDataCache.getTicks());
  }

  private void updatePyramids(List<Tick> ticks){
    try {
      if (ticks == null)
        return;
      Set<Route> routes = new HashSet<Route>();
      if (mSharedPref == null)
        mSharedPref = PreferenceManager.getDefaultSharedPreferences(getApplication().getApplicationContext());
      boolean ignoreTopropes = mSharedPref.getBoolean(SettingsActivity.KEY_PREF_USE_ONLY_LEADS, true);
      for (Tick tick : ticks) {
        if (tick.getRoute() == null)
          continue;

        // filter for ignore toprope setting
        if (ignoreTopropes && tick.getRoute().getType() != RouteType.Boulder) {
          TickType tickType = tick.getType();
          boolean skip = tickType == TickType.Fell;
          skip |= tickType == TickType.Toprope;
          skip |= tickType == TickType.Unknown;
          if (skip)
            continue;
        }

        // filter for the selected time scale
        if (mTimeScale != TimeScale.Lifetime) {
          Date date = new Date();
          Calendar cal = Calendar.getInstance();
          cal.setTime(date);
          switch (mTimeScale) {
            case Month:
              cal.add(Calendar.MONTH, -1);
              break;
            case ThreeMonth:
              cal.add(Calendar.MONTH, -3);
              break;
            case SixMonth:
              cal.add(Calendar.MONTH, -6);
              break;
            case Year:
              cal.add(Calendar.YEAR, -1);
              break;
            default:
              break;
          }
          date = cal.getTime();
          if (date.getTime() > tick.getDate().getTime())
            continue;
        }

        routes.add(tick.getRoute());
      }

      int height = Integer.valueOf(mSharedPref.getString(SettingsActivity.KEY_PREF_PYRAMID_HEIGHT, "5"));
      int stepSize = Integer.valueOf(mSharedPref.getString(SettingsActivity.KEY_PREF_PYRAMID_STEP_MODIFIER_SIZE, "2"));
      String stepTypeStr = mSharedPref.getString(SettingsActivity.KEY_PREF_PYRAMID_STEP_MODIFIER_TYPE, "Additive");
      PyramidStepType stepType = PyramidStepType.valueOf(stepTypeStr);
      List<Pyramid> pyramids = new ArrayList<>();
      ArrayList<Route> routeList = new ArrayList<>(routes);
      if (mSharedPref.getBoolean(SettingsActivity.KEY_PREF_SHOW_ROUTE_PYRAMID, false))
        pyramids.add(SprayarificStructures.buildPyramid(routeList, RouteType.Route, height, stepSize, stepType));

      if (mSharedPref.getBoolean(SettingsActivity.KEY_PREF_SHOW_SPORT_PYRAMID, true))
        pyramids.add(SprayarificStructures.buildPyramid(routeList, RouteType.Sport, height, stepSize, stepType));

      if (mSharedPref.getBoolean(SettingsActivity.KEY_PREF_SHOW_TRAD_PYRAMID, true))
        pyramids.add(SprayarificStructures.buildPyramid(routeList, RouteType.Trad, height, stepSize, stepType));

      if (mSharedPref.getBoolean(SettingsActivity.KEY_PREF_SHOW_BOULDER_PYRAMID, true))
        pyramids.add(SprayarificStructures.buildPyramid(routeList, RouteType.Boulder, height, stepSize, stepType));

      if (mSharedPref.getBoolean(SettingsActivity.KEY_PREF_SHOW_ICE_PYRAMID, false))
        pyramids.add(SprayarificStructures.buildPyramid(routeList, RouteType.Ice, height, stepSize, stepType));

      if (mSharedPref.getBoolean(SettingsActivity.KEY_PREF_SHOW_AID_PYRAMID, false))
        pyramids.add(SprayarificStructures.buildPyramid(routeList, RouteType.Aid, height, stepSize, stepType));

      mPyramids.setValue(pyramids);
    }
    finally {
    }
  }
}
