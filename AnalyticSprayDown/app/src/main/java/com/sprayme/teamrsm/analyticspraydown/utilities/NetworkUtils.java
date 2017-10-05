package com.sprayme.teamrsm.analyticspraydown.utilities;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Utilities to communicate with the network.
 */

public class NetworkUtils {

    final static String MP_BASE_URL =
            "https://www.mountainproject.com/data/";

    final static String ROUTES = "get-routes";
    final static String TICKS = "get-ticks";

    final static String PARAM_EMAIL = "email";
    final static String PARAM_KEY = "key";
    final static String PARAM_ROUTEIDS = "routeIds";

    private String _key;

    public NetworkUtils(String key) {
        _key = key;
    }

    /*
    * Builds the URL used to query mountain project
    * for users ticks
    * */
    public URL buildTicksUrl(String userEmail) {
        Uri ticksUri = Uri.parse(MP_BASE_URL).buildUpon()
                .appendPath(TICKS)
                .appendQueryParameter(PARAM_EMAIL, userEmail)
                .appendQueryParameter(PARAM_KEY, _key)
                .build();

        URL url = null;
        try {
            url = new URL(ticksUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /*
    * Builds the URL used to query mountain project
    * for routes given an array of routeIds.
    * */
    public URL buildRoutesUrl(String[] routeIds) {
        String idString = null;

        for (String id : routeIds) {
            idString += id + ",";
        }
        if (idString != null && idString.length() > 0) {
            idString = idString.substring(0, idString.length() - 1);
        }

        Uri routesUri = Uri.parse(MP_BASE_URL).buildUpon()
                .appendPath(ROUTES)
                .appendQueryParameter(PARAM_ROUTEIDS, idString)
                .appendQueryParameter(PARAM_KEY, _key)
                .build();

        URL routesUrl = null;
        try {
            routesUrl = new URL(routesUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return routesUrl;
    }

    /*
    * Returns the entire result from the HTTP response.
    * */
    public String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            }
            else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
