package com.sprayme.teamrsm.analyticspraydown.models;

import java.util.Locale;

/**
 * Created by climbak on 11/4/17.
 */

public class Statistic {
  private String name;
  private Object value;
  private StatisticType type;

  public Statistic(String name, Object value, StatisticType type){
    this.name = name;
    this.value = value;
    this.type = type;
  }

  public String getName(){
    return name;
  }

  public String getValueString(){
    switch (type){
      case Count: return String.format(Locale.getDefault(), "%d", value);
      case Ratio: return String.format(Locale.getDefault(), "%.3f", value);
      case Percentage: return String.format(Locale.getDefault(), "%.1f%%", value);
      default: return null;
    }
  }
}
