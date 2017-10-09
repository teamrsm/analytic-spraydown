package com.sprayme.teamrsm.analyticspraydown.models;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.sprayme.teamrsm.analyticspraydown.utilities.MPQueryTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Created by climbak on 10/4/17.
 */

public class MPModel {
    public interface MPModelListener {
        // These methods are the different events and
        // need to pass relevant arguments related to the event triggered
        public void onRoutesLoaded();

        public void onTicksLoaded();

        public void onFinished();
    }

    private MPModelListener listener;

    private List<Tick> ticks;
    private List<Route> routes;

    public MPModel(MPModelListener listener) {
        this.listener = listener;
    }

    public void requestTicks(String user) {
        MPQueryTask mpQuery = new MPQueryTask(output -> {
            ticks = parseTicks(output);
            listener.onTicksLoaded();
            String[] routeIds = getRouteIdArray(ticks);
            String[] sub = new String[99];
            for (int i = 0; i < 99; i++)
                sub[i] = routeIds[i];
            requestRoutes(sub);
        });
        URL url = mpQuery.buildTicksUrl(user);
        mpQuery.execute(url);
    }

    public void requestRoutes(String[] routeIds) {
        //todo make this send in 100 count batches
        MPQueryTask mpQuery = new MPQueryTask(output -> {
            routes = parseRoutes(output);
            listener.onRoutesLoaded();
            mapRoutes();
            listener.onFinished();
        });
        URL url = mpQuery.buildRoutesUrl(routeIds);
        mpQuery.execute(url);
    }

    private void mapRoutes() {
        HashMap<String, Route> routeMap = new HashMap<>();
        for (Route route : routes) {
            routeMap.put(route.getId(), route);
        }

        for (Tick tick : ticks) {
            tick.setRoute(routeMap.get(tick.getRouteId()));
        }
    }

    private String[] getRouteIdArray(List<Tick> ticks) {
        List<String> routeIds = new ArrayList<>();
        for (Tick tick : ticks) {
            routeIds.add(tick.getRouteId());
        }
        return routeIds.toArray(new String[routeIds.size()]);
    }

    private List<Tick> parseTicks(String json) {
        List<Tick> ticks = new ArrayList<>();
        try {
            ticks = readTickJson(json);
        } catch (JSONException e) {
            // todo handle error wisely
        }
        return ticks;
    }

    public List<Route> parseRoutes(String json) {
        List<Route> routes = null;
        try {
            routes = readRouteJson(json);
        } catch (JSONException e) {
            // todo handle error wisely
        }

        return routes.stream().filter((route) -> Objects.nonNull(route)).collect(Collectors.toList());
    }

    public List<Tick> readTickJson(String jsonText) throws JSONException {
        List<Tick> ticks = new ArrayList<>();
        JSONObject obj = new JSONObject(jsonText);
        JSONArray arr = obj.getJSONArray("ticks");
        for (int i = 0; i < arr.length(); i++) {
            String routeId = arr.getJSONObject(i).getString("routeId");
            String dateStr = arr.getJSONObject(i).getString("date");
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            Date date;
            try {
                date = df.parse(dateStr);
            } catch (ParseException e) {
                date = null;
            }
            String pitches = arr.getJSONObject(i).getString("pitches");
            String notes = arr.getJSONObject(i).getString("notes");

            Tick tick = new Tick(routeId, date, pitches, notes);
            ticks.add(tick);
        }

        return ticks;
    }

    public List<Route> readRouteJson(String jsonText) throws JSONException {
        List<Route> routes = new ArrayList<>();
        JSONObject obj = new JSONObject(jsonText);
        JSONArray arr = obj.getJSONArray("routes");
        for (int i = 0; i < arr.length(); i++) {
            String routeId = arr.getJSONObject(i).getString("id");
            String name = arr.getJSONObject(i).getString("name");
            String type = arr.getJSONObject(i).getString("type");
            String difficulty = arr.getJSONObject(i).getString("rating");
            String stars = arr.getJSONObject(i).getString("stars");
            String pitches = arr.getJSONObject(i).getString("pitches");
            String url = arr.getJSONObject(i).getString("url");

            Route route = new Route(routeId, name, type, difficulty, stars, pitches, url);
            routes.add(route);
        }

        return routes;
    }

    public List<Tick> getTicks(){
        return ticks;
    }

    public Pyramid buildPyramid(List<Route> routes, RouteType type, int height, int stepChangeSize, PyramidStepType stepModifier) {
        List<Route> filteredRoutes = routes.stream()
                .filter((route) -> Objects.nonNull(route))
                .filter((route) -> route.getType() == type)
                .collect(Collectors.toList());

        return new Pyramid(filteredRoutes, height, stepChangeSize, stepModifier);
    }

    public Pyramid buildPyramid(List<Route> routes, RouteType type, int height, int stepChangeSize, PyramidStepType stepModifier, Grade goal) {
        List<Route> filteredRoutes = routes.stream()
                .filter((route) -> route.getType() == type)
                .collect(Collectors.toList());

        return new Pyramid(routes, height, stepChangeSize, stepModifier, goal);
    }
}
