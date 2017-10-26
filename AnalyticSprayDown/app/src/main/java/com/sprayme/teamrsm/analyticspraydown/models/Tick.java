package com.sprayme.teamrsm.analyticspraydown.models;

import java.util.Date;

/**
 * Created by climbak on 10/4/17.
 */

public class Tick {

  private Long routeId;
  private Date date;
  private Integer pitches;
  private String notes;
  private Route route;
  private TickType type;
  private boolean isRepeat;

  public Tick(Long routeId, Date date, Integer numPitches, String notes) {
    this.routeId = routeId;
    this.date = date;
    this.pitches = numPitches;
    this.notes = notes;

    if (notes.startsWith("Lead / Onsight."))
      type = TickType.Onsight;
    else if (notes.startsWith("Lead / Redpoint."))
      type = TickType.Redpoint;
    else if (notes.startsWith("Lead / Pinkpoint."))
      type = TickType.Pinkpoint;
    else if (notes.startsWith("Lead / Fell/Hung."))
      type = TickType.Fell;
    else if (notes.startsWith("Lead / Flash."))
      type = TickType.Flash;
    else if (notes.startsWith("TR."))
      type = TickType.Toprope;
    else
      type = TickType.Unknown;
  }

  public Tick(Long routeId, Date date, Integer numPitches, String notes, TickType type, boolean isRepeat) {
    this.routeId = routeId;
    this.date = date;
    this.pitches = numPitches;
    this.notes = notes;
    this.type = type;
  }

  public Long getRouteId() {
    return routeId;
  }

  public Date getDate() {
    return date;
  }

  public Integer getPitches() {
    return pitches;
  }

  public String getNotes() {
    return notes;
  }

  public Route getRoute() {
    return route;
  }

  public void setRoute(Route route) {
    this.route = route;
  }

  public TickType getType() {
    return type;
  }

  public boolean isRepeat() {
    return isRepeat;
  }
}

