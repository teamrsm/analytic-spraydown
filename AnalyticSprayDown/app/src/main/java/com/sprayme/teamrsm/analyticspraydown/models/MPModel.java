package com.sprayme.teamrsm.analyticspraydown.models;

import com.sprayme.teamrsm.analyticspraydown.utilities.MPQueryTask;
import com.sprayme.teamrsm.analyticspraydown.utilities.MPResponseParser;
import com.sprayme.teamrsm.analyticspraydown.utilities.NetworkUtils;

import org.json.JSONException;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by climbak on 10/4/17.
 */

public class MPModel{
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

    public MPModel(MPModelListener listener){
        this.listener = listener;
    }

    public void requestTicks(String user){
        NetworkUtils util = new NetworkUtils();
        URL url = util.buildTicksUrl(user);
        new MPQueryTask(new MPQueryTask.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                ticks = parseTicks(output);
                listener.onTicksLoaded();
                String[] routeIds = getRouteIdArray(ticks);
                String[] sub = new String[99];
                for (int i=0; i<99; i++)
                    sub[i] = routeIds[i];
                requestRoutes(sub);
            }
        }).execute(url);
    }

    public void requestRoutes(String[] routeIds){
        NetworkUtils util = new NetworkUtils();
        //todo make this send in 100 count batches
        URL url = util.buildRoutesUrl(routeIds);
        new MPQueryTask(new MPQueryTask.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                routes = parseRoutes(output);
                listener.onRoutesLoaded();
                mapRoutes();
                listener.onFinished();
            }
        }).execute(url);
    }

    private void mapRoutes(){
        HashMap<String, Route> routeMap = new HashMap<>();
        for (Route route : routes) {
            routeMap.put(route.getId(), route);
        }

        for (Tick tick : ticks) {
            tick.setRoute(routeMap.get(tick.getRouteId()));
        }
    }

    private String[] getRouteIdArray(List<Tick> ticks){
        List<String> routeIds = new ArrayList<>();
        for (Tick tick : ticks) {
            routeIds.add(tick.getRouteId());
        }
        return routeIds.toArray(new String[routeIds.size()]);
    }

    private List<Tick> parseTicks(String json){
        List <Tick> ticks = new ArrayList<>();
        try {
            ticks = MPResponseParser.readTickJson(json);
        } catch (JSONException e) {
            // todo handle error wisely
        }
        return ticks;
    }

    public List<Route> parseRoutes(String json){
        List<Route> routes = null;
        try {
            routes = MPResponseParser.readRouteJson(json);
        } catch (JSONException e) {
            // todo handle error wisely
        }

        return routes;
    }

    public Pyramid buildPyramid(List<Route> routes, RouteType type, int height, int stepChangeSize, PyramidStepType stepModifier){
        List<Route> filteredRoutes = routes.stream()
                .filter((route) -> route.getType() == type)
                .collect(Collectors.toList());

        return new Pyramid(routes, height, stepChangeSize, stepModifier);
    }

    public Pyramid buildPyramid(List<Route> routes, RouteType type, int height, int stepChangeSize, PyramidStepType stepModifier, Grade goal){
        List<Route> filteredRoutes = routes.stream()
                .filter((route) -> route.getType() == type)
                .collect(Collectors.toList());

        return new Pyramid(routes, height, stepChangeSize, stepModifier, goal);
    }
}
