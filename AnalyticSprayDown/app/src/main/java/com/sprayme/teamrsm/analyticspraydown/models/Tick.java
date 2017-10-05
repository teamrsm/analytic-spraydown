package com.sprayme.teamrsm.analyticspraydown.models;

import com.sprayme.teamrsm.analyticspraydown.utilities.MPQueryTask;
import com.sprayme.teamrsm.analyticspraydown.utilities.MPResponseParser;
import com.sprayme.teamrsm.analyticspraydown.utilities.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.sprayme.teamrsm.analyticspraydown.utilities.MPResponseParser.readRouteJson;

/**
 * Created by cournaydo on 10/4/17.
 */

public class Tick {

    private String routeId;
    private Date date;
    private String pitches;
    private String notes;
    private Route route;
    private TickType type;

    public Tick(String routeId, Date date, String numPitches, String notes){
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

    public String getPitches() {
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

