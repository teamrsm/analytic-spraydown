package com.sprayme.teamrsm.analyticspraydown;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.sprayme.teamrsm.analyticspraydown.utilities.MPQueryTask;

public class MainActivity extends AppCompatActivity
    implements MPQueryTask.AsyncResponse {

    MPQueryTask mpQueryTask = new MPQueryTask(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void processFinish(String output) {
        /* Here we will receive the result fired from the mpQueryTask
        * onPostExecute(result) method. */
    }
}
