package com.sprayme.teamrsm.analyticspraydown.models;

import java.util.Locale;

/**
 * Created by climbak on 11/4/17.
 */

public class Statistic implements Comparable<Statistic> {
  private String name;
  private Object value;
  private StatisticType type;

  /* Constructor */
  public Statistic(String name, Object value, StatisticType type){
    this.name = name;
    this.value = value;
    this.type = type;
  }

  /* Property Getters */
  public String getName(){
    return name;
  }
  public Object getValue() { return this.value; }

  public String getValueString(){
    switch (type){
      case Count: return String.format(Locale.getDefault(), "%d", value);
      case Ratio: return String.format(Locale.getDefault(), "%.3f", value);
      case Percentage: return String.format(Locale.getDefault(), "%.1f%%", value);
      default: return null;
    }
  }

  /* Comparable Interface Methods */
  @Override
  public int compareTo(Statistic thatStat) {
    final int NOT_COMPARABLE = -99;
    final int BEFORE = -1;
    final int EQUAL = 0;
    final int AFTER = 1;

    if (this == thatStat) return EQUAL;

    if (this.value.getClass() != thatStat.getValue().getClass())
      return NOT_COMPARABLE;

    if (this.value instanceof String) {
      String thisValue = (String)this.value;
      int comparison =  thisValue.compareTo((String)thatStat.getValue());
      return comparison;
    }

    if (this.value instanceof Integer) {
      if ((Integer)this.value < (Integer)thatStat.getValue())
        return BEFORE;
      if ((Integer)this.value > (Integer)thatStat.getValue())
        return AFTER;
      else
        return EQUAL;
    }

    if (this.value instanceof Double) {
      if ((Double)this.value < (Double)thatStat.getValue())
        return BEFORE;
      if ((Double)this.value > (Double)thatStat.getValue())
        return AFTER;
      else
        return EQUAL;
    }

    return NOT_COMPARABLE;
  }
}
