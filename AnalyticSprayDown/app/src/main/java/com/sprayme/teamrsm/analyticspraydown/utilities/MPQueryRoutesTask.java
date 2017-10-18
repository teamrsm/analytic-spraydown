package com.sprayme.teamrsm.analyticspraydown.utilities;

import android.os.AsyncTask;

import com.sprayme.teamrsm.analyticspraydown.models.Route;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by climbak on 10/17/17.
 */

public class MPQueryRoutesTask extends AsyncTask<Long[], Void, List<Route>> {

    public interface AsyncResponse {
        void processFinish(List<Route> ticks);
    }

    private final int MP_ROUTE_RETURN_LIMIT = 200;

    private String key;
    private AsyncResponse delegate = null;

    public MPQueryRoutesTask(String apiKey, AsyncResponse delegate) {
        this.key = apiKey;
        this.delegate = delegate;
    }


    @Override
    protected List<Route> doInBackground(Long[]... params) {
        String mpQueryResults;
        List<Route> returnRoutes = new ArrayList<>();
        Long[] routesLeft = params[0];

        try {
            while (routesLeft.length > 0) {
                int count = routesLeft.length < MP_ROUTE_RETURN_LIMIT ? routesLeft.length : MP_ROUTE_RETURN_LIMIT;
                Long[] routes = new Long[count];
                for (int i=0; i<count; i++) {
                    routes[i] = routesLeft[i];
                }

                if (count > MP_ROUTE_RETURN_LIMIT) {
                    Long[] routesToCache = new Long[routesLeft.length - 200];
                    for (int i = 200; i < routesLeft.length; i++) {
                        routesToCache[i - 200] = routesLeft[i];
                    }
                    routesLeft = routesToCache;
                }
                else
                    routesLeft = new Long[0];

                URL executeUrl = MPQueryTaskHelper.buildRoutesUrl(key, routes);
                mpQueryResults = MPQueryTaskHelper.getResponseFromHttpUrl(executeUrl);
                List<Route> result = SprayarificParser.parseRoutesJson(mpQueryResults);
                returnRoutes.addAll(result);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return returnRoutes;
    }

    @Override
    protected void onPostExecute(List<Route> routes) {
        delegate.processFinish(routes);
    }
}
