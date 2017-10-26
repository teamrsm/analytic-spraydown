package com.sprayme.teamrsm.analyticspraydown.utilities;

import android.os.AsyncTask;

import com.sprayme.teamrsm.analyticspraydown.models.Tick;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by climbak on 10/17/17.
 */

public class MPQueryTicksTask extends AsyncTask<Void, Void, List<Tick>> {

  public interface AsyncResponse {
    void processFinish(List<Tick> ticks);
  }

  private final int MP_TICK_RETURN_LIMIT = 200;

  private String key;
  private long userId = -1;
  private AsyncResponse delegate = null;

  public MPQueryTicksTask(long userId, String apiKey, MPQueryTicksTask.AsyncResponse delegate) {
    this.userId = userId;
    this.key = apiKey;
    this.delegate = delegate;
  }


  @Override
  protected List<Tick> doInBackground(Void... params) {
    int startIndex = 0;
    String mpQueryResults;
    List<Tick> ticks = new ArrayList<>();
    int ticksReturned = MP_TICK_RETURN_LIMIT;

    try {
      while (ticksReturned == MP_TICK_RETURN_LIMIT) {
        URL executeUrl = MPQueryTaskHelper.buildTicksUrl(userId, key, startIndex);
        mpQueryResults = MPQueryTaskHelper.getResponseFromHttpUrl(executeUrl);
        List<Tick> result = SprayarificParser.parseTicksJson(mpQueryResults);
        ticksReturned = result.size();
        startIndex += 200;
        ticks.addAll(result);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return ticks;
  }

  @Override
  protected void onPostExecute(List<Tick> ticks) {
    delegate.processFinish(ticks);
  }
}
