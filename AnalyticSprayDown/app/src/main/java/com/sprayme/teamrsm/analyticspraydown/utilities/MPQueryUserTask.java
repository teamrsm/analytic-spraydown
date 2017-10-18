package com.sprayme.teamrsm.analyticspraydown.utilities;

import android.os.AsyncTask;

import com.sprayme.teamrsm.analyticspraydown.models.User;

import java.io.IOException;
import java.net.URL;

/**
 * Created by climbak on 10/17/17.
 */

public class MPQueryUserTask extends AsyncTask<Void, Void, User> {

    public interface AsyncResponse {
        void processFinish(User user);
    }

    private String key;
    private String email;
    private AsyncResponse delegate = null;

    public MPQueryUserTask(String email, String apiKey, AsyncResponse delegate) {
        this.email = email;
        this.key = apiKey;
        this.delegate = delegate;
    }


    @Override
    protected User doInBackground(Void... params) {
        String mpQueryResults;
        URL url = MPQueryTaskHelper.buildUserUrl(email, key);
        User user = null;

        try {
            mpQueryResults = MPQueryTaskHelper.getResponseFromHttpUrl(url);
            user = SprayarificParser.parseUserJson(mpQueryResults);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return user;
    }

    @Override
    protected void onPostExecute(User user) {
        delegate.processFinish(user);
    }
}
