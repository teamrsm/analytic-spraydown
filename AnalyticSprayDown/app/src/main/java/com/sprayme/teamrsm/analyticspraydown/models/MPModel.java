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

        public void onUserLoaded();

        public void onFinished();
    }

    private MPModelListener listener;

    private List<Tick> ticks;
    private List<Route> routes;
    private User user;

    public MPModel(MPModelListener listener) {
        this.listener = listener;
    }

    public void requestUser(String emailAddress, String apiKey) {
        MPQueryTask mpQuery = new MPQueryTask(emailAddress, apiKey,
                output -> {
            user = parseUser(output);
            listener.onUserLoaded();
        });
        URL url = mpQuery.buildUserUrl();
        mpQuery.execute(url);
    }

    public void requestTicks(long userId, String apiKey) {
        MPQueryTask mpQuery = new MPQueryTask(userId, apiKey, output -> {
            ticks = parseTicks(output);
            listener.onTicksLoaded();
            Long[] routeIds = getRouteIdArray(ticks);
            Long[] sub = new Long[99];
            for (int i = 0; i < 99; i++)
                sub[i] = routeIds[i];
            requestRoutes(userId, apiKey, sub);
        });
        URL url = mpQuery.buildTicksUrl();
        mpQuery.execute(url);
    }

    public void requestRoutes(long userId, String apiKey, Long[] routeIds) {
        //todo make this send in 100 count batches
        MPQueryTask mpQuery = new MPQueryTask(userId, apiKey, output -> {
            routes = parseRoutes(output);
            listener.onRoutesLoaded();
            mapRoutes();
            listener.onFinished();
        });
        URL url = mpQuery.buildRoutesUrl(routeIds);
        mpQuery.execute(url);
    }

    private void mapRoutes() {
        HashMap<Long, Route> routeMap = new HashMap<>();
        for (Route route : routes) {
            routeMap.put(route.getId(), route);
        }

        for (Tick tick : ticks) {
            tick.setRoute(routeMap.get(tick.getRouteId()));
        }
    }

    private Long[] getRouteIdArray(List<Tick> ticks) {
        List<Long> routeIds = new ArrayList<>();
        for (Tick tick : ticks) {
            routeIds.add(tick.getRouteId());
        }
        return routeIds.toArray(new Long[routeIds.size()]);
    }

    private User parseUser(String json) {
        try {
            user = readUserJson(json);
        } catch (JSONException e) {
            // todo handle error wisely
        }

        return user;
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

    public User readUserJson(String jsonText) throws JSONException {
        User mpUser = new User();
        JSONObject obj = new JSONObject(jsonText);

        mpUser.setUserId(obj.getLong("id"));
        mpUser.setUserName(obj.getString("name"));

        return mpUser;
    }

    public List<Tick> readTickJson(String jsonText) throws JSONException {
        List<Tick> ticks = new ArrayList<>();
        JSONObject obj = new JSONObject(jsonText);
        JSONArray arr = obj.getJSONArray("ticks");
        for (int i = 0; i < arr.length(); i++) {
            Long routeId = arr.getJSONObject(i).optLong("routeId", 0);
            if (routeId == 0)
                continue;
            String dateStr = arr.getJSONObject(i).getString("date");
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            Date date;
            try {
                date = df.parse(dateStr);
            } catch (ParseException e) {
                date = null;
            }
            Integer pitches = (int) arr.getJSONObject(i).optLong("pitches", 0);
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
            Long routeId = arr.getJSONObject(i).optLong("id", 0);
            if (routeId == 0)
                continue;
            String name = arr.getJSONObject(i).getString("name");
            String type = arr.getJSONObject(i).getString("type");
            String difficulty = arr.getJSONObject(i).getString("rating");
            Float stars = (float)arr.getJSONObject(i).optDouble("stars", 0);
            Integer pitches = (int) arr.getJSONObject(i).optLong("pitches", 0);
            String url = arr.getJSONObject(i).getString("url");

            Route route = new Route(routeId, name, type, difficulty, stars, pitches, url);
            routes.add(route);
        }

        return routes;
    }

    public User getUser() { return user; }

    public List<Tick> getTicks(){
        return ticks;
    }
}
