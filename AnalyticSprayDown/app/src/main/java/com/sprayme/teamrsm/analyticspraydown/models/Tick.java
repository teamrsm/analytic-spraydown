package com.sprayme.teamrsm.analyticspraydown.models;

import java.util.Date;

/**
 * Created by cournaydo on 10/4/17.
 */

public class Tick {

    private String routeId;
    private Date date;
    private int pitches;
    private String notes;
    private Route route;
    private TickType type;

    public Tick(String routeId, Date date, int numPitches, String notes){
        this.routeId = routeId;
        this.date = date;
        this.pitches = numPitches;
        this.notes = notes;

        if (notes.startsWith("Lead / Onsight."))
            type = TickType.Onsite;
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

    public String getRouteId() {
        return routeId;
    }

    public Date getDate() {
        return date;
    }

    public int getPitches() {
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
}

