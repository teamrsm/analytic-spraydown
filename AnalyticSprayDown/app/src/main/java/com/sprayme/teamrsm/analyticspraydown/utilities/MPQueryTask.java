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

    private NetworkUtils networkUtils = null;

    public interface AsyncResponse {
        void processFinish(String output);
    }

    public AsyncResponse delegate = null;

    public MPQueryTask(AsyncResponse delegate) {
        this.delegate = delegate;
        networkUtils = new NetworkUtils();
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
