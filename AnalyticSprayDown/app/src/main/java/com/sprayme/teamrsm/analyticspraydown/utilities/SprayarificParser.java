package com.sprayme.teamrsm.analyticspraydown.utilities;

import com.sprayme.teamrsm.analyticspraydown.models.Route;
import com.sprayme.teamrsm.analyticspraydown.models.Tick;
import com.sprayme.teamrsm.analyticspraydown.models.TickType;
import com.sprayme.teamrsm.analyticspraydown.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by climbak on 10/12/17.
 */

public class SprayarificParser {

    public static User parseUserJson(String jsonText) {
        if (jsonText == null)
            return null;
        try {
            User mpUser = new User();
            JSONObject obj = new JSONObject(jsonText);

            mpUser.setUserId(obj.getLong("id"));
            mpUser.setUserName(obj.getString("name"));
            mpUser.setAvatarUrl(obj.getString("avatar"));

            return mpUser;
        }
        catch (JSONException e){
            // todo handle error wisely
        }
        return null;
    }

    public static List<Tick> parseTicksJson(String jsonText){
        try {
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
                String style = arr.getJSONObject(i).getString("style");
                String leadStyle = arr.getJSONObject(i).getString("leadStyle");
                TickType type = TickType.Unknown;
                if (style.equalsIgnoreCase("tr"))
                    type = TickType.Toprope;
                else if (style.equalsIgnoreCase("lead")){
                    if (leadStyle.equalsIgnoreCase("onsight"))
                        type = TickType.Onsight;
                    else if (leadStyle.equalsIgnoreCase("flash"))
                        type = TickType.Flash;
                    else if (leadStyle.equalsIgnoreCase("redpoint"))
                        type = TickType.Redpoint;
                    else if (leadStyle.equalsIgnoreCase("pinkpoint"))
                        type = TickType.Pinkpoint;
                }

                // todo: support concept of isRepeat
                Tick tick = (new Tick(routeId, date, pitches, notes, type, false));
                ticks.add(tick);
            }

            return ticks;
        }
        catch (JSONException e){
            //todo handle error wisely
        }
        return null;
    }

    public static List<Tick> parseTicksMountainProjectCsv(String csv){
        final int dateIndex = 0;
        final int notesIndex = 3;
        final int urlIndex = 4;
        final int pitchesIndex = 5;
        final int styleIndex = 9;
        final int leadStyleIndex = 10;
        BufferedReader reader = new BufferedReader(new StringReader(csv));
        List<Tick> ticks = new ArrayList<>();
        try {
            String line = reader.readLine();
            String countStr = line.split("\\s.")[0];
            int totalCount = Integer.valueOf(countStr);
            reader.readLine(); // next line is the column headers

            int count = 0;
            while ((line = reader.readLine()) != null){
                String[] tickCsv = line.split("\\s*\\|\\s*");
                Date date;
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                try {
                    date = df.parse(tickCsv[dateIndex]);
                } catch (ParseException e) {
                    date = null;
                }
                String notes = tickCsv[notesIndex];
                int pitches = 0;
                try {
                    pitches = Integer.valueOf(tickCsv[pitchesIndex]);
                }
                catch (NumberFormatException e) {}
                String url = tickCsv[urlIndex];
                int startIndex = url.lastIndexOf('/');
                Long routeId = Long.valueOf((url.substring(startIndex + 1)));
                String style = tickCsv[styleIndex];
                String leadStyle = tickCsv[leadStyleIndex];
                TickType type = TickType.Unknown;
                if (style.equalsIgnoreCase("tr"))
                    type = TickType.Toprope;
                else if (style.equalsIgnoreCase("lead")){
                    if (leadStyle.equalsIgnoreCase("onsight"))
                        type = TickType.Onsight;
                    else if (leadStyle.equalsIgnoreCase("flash"))
                        type = TickType.Flash;
                    else if (leadStyle.equalsIgnoreCase("redpoint"))
                        type = TickType.Redpoint;
                    else if (leadStyle.equalsIgnoreCase("pinkpoint"))
                        type = TickType.Pinkpoint;
                }

                //todo: support concept of isRepeat
                ticks.add(new Tick(routeId, date, pitches, notes, type, false));

                count++;
            }

            if (count != totalCount){
                //todo we missed a tick somehow
            }
        }
        catch (IOException e){
            // todo handle this
        }

        return ticks;
    }

    public static List<Route> parseRoutesJson(String jsonText) {
        try {
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
                Float stars = (float) arr.getJSONObject(i).optDouble("stars", 0);
                Integer pitches = (int) arr.getJSONObject(i).optLong("pitches", 0);
                String url = arr.getJSONObject(i).getString("url");

                Route route = new Route(routeId, name, type, difficulty, stars, pitches, url);
                routes.add(route);
            }

            return routes;
        }
        catch (JSONException e) {
            //todo handle error wisely
        }
        return null;
    }
}
