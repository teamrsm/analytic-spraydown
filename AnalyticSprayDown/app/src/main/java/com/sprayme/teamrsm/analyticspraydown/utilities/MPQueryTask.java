package com.sprayme.teamrsm.analyticspraydown.utilities;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.sprayme.teamrsm.analyticspraydown.models.Route;
import com.sprayme.teamrsm.analyticspraydown.models.Tick;

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
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * Created by Said on 10/4/2017.
 */
//
@RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
public class MPQueryTask extends AsyncTask<URL, Void, String> {

    public interface AsyncResponse {
        void processFinish(String output);
    }

    final String MP_BASE_URL = "https://www.mountainproject.com/data/";

    final String ROUTES = "get-routes";
    final String TICKS = "get-ticks";
    final String USER = "get-user";

    final String PARAM_EMAIL = "email";
    final String PARAM_USERID = "userId";
    final String PARAM_KEY = "key";
    final String PARAM_ROUTEIDS = "routeIds";

    private String email;
    private String key;
    private long userId = -1;


    public AsyncResponse delegate = null;

    public MPQueryTask(String emailAddress, String apiKey, AsyncResponse delegate) {
        this.email = emailAddress;
        this.key = apiKey;
        this.delegate = delegate;
    }

    public MPQueryTask(long userId, String apiKey, AsyncResponse delegate) {
        this.userId = userId;
        this.key = apiKey;
        this.delegate = delegate;
    }

    @Override
    protected String doInBackground(URL... urls) {
        URL executeUrl = urls[0];
        String mpQueryResults = null;

        try {
            mpQueryResults = getResponseFromHttpUrl(executeUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return mpQueryResults;
    }

    @Override
    protected void onPostExecute(String result) {
        delegate.processFinish(result);
    }
    /*
    * Builds the URL used to query mountain project
    * for users ticks
    * */
    public URL buildTicksUrl() {
        String userParam;
        String userIdentifier;
        if (userId > 0){
            userParam = PARAM_USERID;
            userIdentifier = userId+"";
        }
        else{
            userParam = PARAM_EMAIL;
            userIdentifier = email;
        }

        Uri ticksUri = Uri.parse(MP_BASE_URL).buildUpon()
                .appendPath(TICKS)
                .appendQueryParameter(userParam, userIdentifier)
                .appendQueryParameter(PARAM_KEY, key)
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
    public URL buildRoutesUrl(Long[] routeIds) {
        String idString = null;

        for (Long id : routeIds) {
            idString += id + ",";
        }
        if (idString != null && idString.length() > 0) {
            idString = idString.substring(0, idString.length() - 1);
        }

        Uri routesUri = Uri.parse(MP_BASE_URL).buildUpon()
                .appendPath(ROUTES)
                .appendQueryParameter(PARAM_ROUTEIDS, idString)
                .appendQueryParameter(PARAM_KEY, key)
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
    * Build URL to query mountain project data api to query
    * for the user with the given email address
    * */
    public URL buildUserUrl() {
        Uri userUri = Uri.parse(MP_BASE_URL).buildUpon()
                .appendPath(USER)
                .appendQueryParameter(PARAM_EMAIL, email)
                .appendQueryParameter(PARAM_KEY, key)
                .build();

        URL userUrl = null;
        try {
            userUrl = new URL(userUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return userUrl;
    }

    /*
    * Returns the entire result from the HTTP response.
    * */
    private String getResponseFromHttpUrl(URL url) throws IOException {
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
