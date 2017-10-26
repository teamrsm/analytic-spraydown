package com.sprayme.teamrsm.analyticspraydown.utilities;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Said on 10/4/2017.
 */

public class MPQueryTaskHelper {

  public static final String MP_BASE_URL = "https://www.mountainproject.com/data/";

  private static final String ROUTES = "get-routes";
  private static final String TICKS = "get-ticks";
  private static final String USER = "get-user";

  private static final String PARAM_EMAIL = "email";
  private static final String PARAM_USERID = "userId";
  private static final String PARAM_KEY = "key";
  private static final String START_POS_KEY = "startPos";
  private static final String PARAM_ROUTEIDS = "routeIds";

  /*
  * Builds the URL used to query mountain project
  * for users ticks
  * */
  public static URL buildTicksUrl(long userId, String key, int startIndex) {
    String userParam = PARAM_USERID;
    String userIdentifier = userId + "";

    Uri ticksUri = Uri.parse(MP_BASE_URL).buildUpon()
            .appendPath(TICKS)
            .appendQueryParameter(userParam, userIdentifier)
            .appendQueryParameter(PARAM_KEY, key)
            .appendQueryParameter(START_POS_KEY, startIndex + "")
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
  public static URL buildRoutesUrl(String key, Long[] routeIds) {
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
  public static URL buildUserUrl(String email, String key) {
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
  public static String getResponseFromHttpUrl(URL url) throws IOException {
    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

    try {
      InputStream in = urlConnection.getInputStream();

      Scanner scanner = new Scanner(in);
      scanner.useDelimiter("\\A");
      String returnVal = null;
      boolean hasInput = scanner.hasNext();
      if (hasInput) {
        returnVal = scanner.next();
      }
      scanner.close();
      return returnVal;
    } finally {
      urlConnection.disconnect();
    }
  }
}
