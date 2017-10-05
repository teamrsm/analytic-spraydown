package com.sprayme.teamrsm.analyticspraydown.utilities;

import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.io.IOException;
import java.net.URL;

/**
 * Created by Said on 10/4/2017.
 */

@RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
public class MPQueryTask extends AsyncTask<URL, Void, String> {

    /* Hardcoded for now. Later we will create a static class to scrape
    * mp for the users' key. */
    final static String KEY = "106308715-6decf82832c803ba56c7bd6058316b47";
    private NetworkUtils networkUtils = null;

    public interface AsyncResponse {
        void processFinish(String output);
    }

    public AsyncResponse delegate = null;

    public MPQueryTask(AsyncResponse delegate) {
        this.delegate = delegate;
        networkUtils = new NetworkUtils(KEY);
    }

    @Override
    protected String doInBackground(URL... urls) {
        URL executeUrl = urls[0];
        String mpQueryResults = null;

        try {
            mpQueryResults = networkUtils.getResponseFromHttpUrl(executeUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return mpQueryResults;
    }

    @Override
    protected void onPostExecute(String result) {
        delegate.processFinish(result);
    }
}
