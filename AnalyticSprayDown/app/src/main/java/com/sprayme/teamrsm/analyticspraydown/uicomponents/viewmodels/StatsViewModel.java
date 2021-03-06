package com.sprayme.teamrsm.analyticspraydown.uicomponents.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.sprayme.teamrsm.analyticspraydown.SettingsActivity;
import com.sprayme.teamrsm.analyticspraydown.models.Grade;
import com.sprayme.teamrsm.analyticspraydown.models.GradeType;
import com.sprayme.teamrsm.analyticspraydown.models.Pyramid;
import com.sprayme.teamrsm.analyticspraydown.models.PyramidStepType;
import com.sprayme.teamrsm.analyticspraydown.models.Route;
import com.sprayme.teamrsm.analyticspraydown.models.RouteType;
import com.sprayme.teamrsm.analyticspraydown.models.Statistic;
import com.sprayme.teamrsm.analyticspraydown.models.StatisticType;
import com.sprayme.teamrsm.analyticspraydown.models.Tick;
import com.sprayme.teamrsm.analyticspraydown.models.TickType;
import com.sprayme.teamrsm.analyticspraydown.models.TimeScale;
import com.sprayme.teamrsm.analyticspraydown.utilities.DataCache;
import com.sprayme.teamrsm.analyticspraydown.utilities.SprayarificStructures;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
  private MutableLiveData<Boolean> mIsBusy = new MutableLiveData<>();

  private DataCache mDataCache = null;
  private SharedPreferences mSharedPref;

  public StatsViewModel(Application app){
    super(app);
    mDataCache = DataCache.getInstance();
    mDataCache.getTicksLiveData().observeForever(ticks -> {
      updatePyramids(ticks);

      /* ToDo: For Statistics, we need a notion of the currently active pyramid,
       * so we can update statistics appropriately.
       * Or alternately, load all stats and swizzle them as needed. */

      mDataCache.calculateOnsightLevel(RouteType.Sport, GradeType.RouteYosemite);
      mDataCache.getOnsights(GradeType.RouteYosemite);
      mDataCache.getTickDistribution(RouteType.Sport, GradeType.RouteYosemite, TickType.Redpoint);
    });

    LiveData<Boolean> ld = mDataCache.getIsBusyLiveData();
    mIsBusy.setValue(ld.getValue());
    ld.observeForever(isBusy -> {
      mIsBusy.setValue(isBusy);
    });

  }

  public MutableLiveData<List<Pyramid>> getPyramids() {
    return mPyramids;
  }

  public MutableLiveData<List<Statistic>> getStatistics() {
    return mStatistics;
  }

  public MutableLiveData<Boolean> getIsBusy(){
    return mIsBusy;
  }

  public void setTimeScale(TimeScale timeScale){
    mTimeScale = timeScale;
    updatePyramids(mDataCache.getTicks());
  }

  private void updatePyramids(List<Tick> ticks){
    try {
      if (ticks == null)
        return;
      Set<Route> routes = new HashSet<>();
      List<Tick> includedTicks = new ArrayList<>();
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

        if (routes.add(tick.getRoute()))
          includedTicks.add(tick);
      }

      int height = Integer.valueOf(mSharedPref.getString(SettingsActivity.KEY_PREF_PYRAMID_HEIGHT, "5"));
      int stepSize = Integer.valueOf(mSharedPref.getString(SettingsActivity.KEY_PREF_PYRAMID_STEP_MODIFIER_SIZE, "2"));
      String stepTypeStr = mSharedPref.getString(SettingsActivity.KEY_PREF_PYRAMID_STEP_MODIFIER_TYPE, "Additive");
      PyramidStepType stepType = PyramidStepType.valueOf(stepTypeStr);
      List<Pyramid> pyramids = new ArrayList<>();
      if (mSharedPref.getBoolean(SettingsActivity.KEY_PREF_SHOW_ROUTE_PYRAMID, false))
        pyramids.add(SprayarificStructures.buildPyramid(includedTicks, RouteType.Route, height, stepSize, stepType));

      if (mSharedPref.getBoolean(SettingsActivity.KEY_PREF_SHOW_SPORT_PYRAMID, true))
        pyramids.add(SprayarificStructures.buildPyramid(includedTicks, RouteType.Sport, height, stepSize, stepType));

      if (mSharedPref.getBoolean(SettingsActivity.KEY_PREF_SHOW_TRAD_PYRAMID, true))
        pyramids.add(SprayarificStructures.buildPyramid(includedTicks, RouteType.Trad, height, stepSize, stepType));

      if (mSharedPref.getBoolean(SettingsActivity.KEY_PREF_SHOW_BOULDER_PYRAMID, true))
        pyramids.add(SprayarificStructures.buildPyramid(includedTicks, RouteType.Boulder, height, stepSize, stepType));

      if (mSharedPref.getBoolean(SettingsActivity.KEY_PREF_SHOW_ICE_PYRAMID, false))
        pyramids.add(SprayarificStructures.buildPyramid(includedTicks, RouteType.Ice, height, stepSize, stepType));

      if (mSharedPref.getBoolean(SettingsActivity.KEY_PREF_SHOW_AID_PYRAMID, false))
        pyramids.add(SprayarificStructures.buildPyramid(includedTicks, RouteType.Aid, height, stepSize, stepType));

      mPyramids.setValue(pyramids);
      updateStats(ticks);
    }
    finally {
    }
  }

  private void updateStats(List<Tick> ticks){
    try {
      if (ticks == null)
        return;
      Set<Route> routes = new HashSet<>();
      List<Tick> includedTicks = new ArrayList<>();
      if (mSharedPref == null)
        mSharedPref = PreferenceManager.getDefaultSharedPreferences(getApplication().getApplicationContext());
      boolean ignoreTopropes = mSharedPref.getBoolean(SettingsActivity.KEY_PREF_USE_ONLY_LEADS, true);
      int sendCount = 0;
      HashMap<Grade, Integer> sends = new HashMap<>();
      HashMap<Grade, Integer> onsights = new HashMap<>();
      for (Tick tick : ticks) {
        if (tick.getRoute() == null)
          continue;

        if (tick.getRoute().getType() != RouteType.Sport)
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

        sendCount++;
        if (routes.add(tick.getRoute()))
          includedTicks.add(tick);

        Grade grade = tick.getRoute().getGrade();
        if (!sends.containsKey(grade))
          sends.put(grade, 0);

        Integer currentSendCount = sends.get(grade);
        currentSendCount++;
        sends.put(grade, currentSendCount);

        if (!onsights.containsKey(grade))
        onsights.put(grade, 0);

        if (tick.getType() == TickType.Onsight) {
          Integer onsightCount = onsights.get(grade);
          onsightCount++;
          onsights.put(grade, onsightCount);
        }
      }

      if (includedTicks.isEmpty())
       return;

      Grade maxOnsight = null, maxSend = null;
      int distinctSendCount = includedTicks.size();
      int onsightCount = 0, onsightsTotalValue = 0, sendsTotalValue = 0;
      for(Grade grade : onsights.keySet()){
        if (onsights.get(grade) == 0)
          continue;
        if (maxOnsight == null)
          maxOnsight = grade;
        else if (grade.compareTo(maxOnsight) > 0)
          maxOnsight = grade;

        onsightCount += onsights.get(grade);
        onsightsTotalValue += onsights.get(grade) * grade.getGradeValue();
      }

      for(Grade grade : sends.keySet()){
        if (maxSend == null)
          maxSend = grade;
        else if (grade.compareTo(maxSend) > 0)
          maxSend = grade;

        sendsTotalValue += sends.get(grade) * grade.getGradeValue();
      }

      int avgOnsight = onsightCount > 0 ? onsightsTotalValue / onsightCount : -1;
      int avgSend = sendsTotalValue / sendCount;

      List<Statistic> stats = new ArrayList<>();

      Statistic bestOnsight = new Statistic("Best Onsight", maxOnsight, StatisticType.String);
      Statistic numOnsights = new Statistic("Number of Onsights", onsightCount, StatisticType.Count);
      Statistic averageOnsight = new Statistic("Average Onsight", new Grade(avgOnsight, GradeType.RouteYosemite), StatisticType.String);

      Statistic bestSend = new Statistic("Best Send", maxSend, StatisticType.String);
      Statistic numSends = new Statistic("Number of Sends", sendCount, StatisticType.Count);
      Statistic numDistinct = new Statistic("Number of Distinct Sends", distinctSendCount, StatisticType.Count);
      Statistic averageSend = new Statistic("Average Send", new Grade(avgSend, GradeType.RouteYosemite), StatisticType.String);

      stats.add(bestOnsight);
      stats.add(numOnsights);
      stats.add(averageOnsight);
      stats.add(bestSend);
      stats.add(numSends);
      stats.add(numDistinct);
      stats.add(averageSend);

      mStatistics.setValue(stats);

    } finally {
    }
  }
}
