package com.sprayme.teamrsm.analyticspraydown.models;

import com.sprayme.teamrsm.analyticspraydown.utilities.MPQueryTask;
import com.sprayme.teamrsm.analyticspraydown.utilities.SprayarificParser;

import java.net.URL;
import java.util.List;

/**
 * Created by climbak on 10/4/17.
 */

public class MPModel {
    public interface MPModelListener {
        // These methods are the different events and
        // need to pass relevant arguments related to the event triggered
        void onRoutesLoaded(List<Route> routes);

        void onTicksLoaded(List<Tick> ticks);

        void onUserLoaded(User user);
    }

    private MPModelListener listener;

    public MPModel(MPModelListener listener) {
        this.listener = listener;
    }

    public void requestUser(String emailAddress, String apiKey) {
        MPQueryTask mpQuery = new MPQueryTask(emailAddress, apiKey,
                output -> {
            User user = SprayarificParser.parseUserJson(output);
            listener.onUserLoaded(user);
        });
        URL url = mpQuery.buildUserUrl();
        mpQuery.execute(url);
    }

    public void requestTicks(long userId, String apiKey, int startIndex) {
        MPQueryTask mpQuery = new MPQueryTask(userId, apiKey, output -> {
            List<Tick> ticks = SprayarificParser.parseTicksJson(output);
            listener.onTicksLoaded(ticks);
//            Long[] routeIds = getRouteIdArray(ticks);
//            requestRoutes(userId, apiKey, routeIds);
        });
        URL url = mpQuery.buildTicksUrl(startIndex);
        mpQuery.execute(url);
    }

    public void requestRoutes(long userId, String apiKey, Long[] routeIds)
            throws IllegalArgumentException {
        if (routeIds.length > 200)
            throw new IllegalArgumentException("requestRoutes can only handle up to 200 route IDs" +
                    " at a time. This is a restriction in the MP api.");

        MPQueryTask mpQuery = new MPQueryTask(userId, apiKey, output -> {
            List<Route> routes = SprayarificParser.parseRoutesJson(output);
            listener.onRoutesLoaded(routes);
        });
        URL url = mpQuery.buildRoutesUrl(routeIds);
        mpQuery.execute(url);
    }
}
