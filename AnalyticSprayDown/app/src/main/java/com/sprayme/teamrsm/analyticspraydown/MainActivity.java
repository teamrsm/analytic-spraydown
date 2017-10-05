package com.sprayme.teamrsm.analyticspraydown;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();
        if (itemThatWasClickedId == R.id.build_pyramid) {

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
