package com.sprayme.teamrsm.analyticspraydown.utilities;

import com.sprayme.teamrsm.analyticspraydown.MainActivity;
import com.sprayme.teamrsm.analyticspraydown.models.Route;
import com.sprayme.teamrsm.analyticspraydown.models.Tick;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by cournaydo on 10/4/17.
 */

public class MPResponseParser {

    public static List<Tick> readTickJson(String jsonText) throws JSONException {
        List<Tick> ticks = new ArrayList<>();
        JSONObject obj = new JSONObject(jsonText);
        JSONArray arr = obj.getJSONArray("ticks");
        for (int i = 0; i < arr.length(); i++)
        {
            String routeId = arr.getJSONObject(i).getString("routeId");
            String dateStr = arr.getJSONObject(i).getString("date");
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            Date date;
            try {
                date =  df.parse(dateStr);
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

    public static List<Route> readRouteJson(String jsonText) throws JSONException {
        List<Route> routes = new ArrayList<>();
        JSONObject obj = new JSONObject(jsonText);
        JSONArray arr = obj.getJSONArray("routes");
        for (int i = 0; i < arr.length(); i++)
        {
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

}
