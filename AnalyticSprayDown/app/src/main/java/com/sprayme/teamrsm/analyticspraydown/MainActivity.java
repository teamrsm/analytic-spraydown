package com.sprayme.teamrsm.analyticspraydown;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.sprayme.teamrsm.analyticspraydown.data_access.BetaSpewDb;
import com.sprayme.teamrsm.analyticspraydown.data_access.DbSprAyPI;
import com.sprayme.teamrsm.analyticspraydown.data_access.InvalidUserException;
import com.sprayme.teamrsm.analyticspraydown.models.MPModel;
import com.sprayme.teamrsm.analyticspraydown.models.Pyramid;
import com.sprayme.teamrsm.analyticspraydown.models.PyramidStepType;
import com.sprayme.teamrsm.analyticspraydown.models.Route;
import com.sprayme.teamrsm.analyticspraydown.models.RouteType;
import com.sprayme.teamrsm.analyticspraydown.models.Tick;
import com.sprayme.teamrsm.analyticspraydown.models.User;
import com.sprayme.teamrsm.analyticspraydown.utilities.DataCache;
import com.sprayme.teamrsm.analyticspraydown.utilities.MPQueryTask;
import com.sprayme.teamrsm.analyticspraydown.views.SprayamidView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
    implements MPModel.MPModelListener {

    MPModel mpModel = null;
    DataCache dataCache = null;
    BetaSpewDb db = null;
    User currentUser = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mpModel = new MPModel(this);
        dataCache = DataCache.getInstance();
        //this.deleteDatabase("BetaSpew.db");
        db = BetaSpewDb.getInstance(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        try {
            dataCache.setDb(db);
            currentUser = dataCache.getLastUser();
        } catch (InvalidUserException e) {
            // todo: launch login, we have no known user
        }
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
            EditText email = (EditText)findViewById(R.id.emailEntry);
            EditText apiKey = (EditText)findViewById(R.id.apiKeyEntry);
            if (apiKey.getText().toString() != null)
                MPQueryTask.KEY = apiKey.getText().toString();
            mpModel.requestTicks(email.getText().toString() != null ? email.getText().toString() : "thebigrokh@gmail.com");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onSomeAction(){
        mpModel.requestTicks("someUser");
    }

    public void onSomeActionComplete(){
        //do stuff
    }

    @Override
    public void onRoutesLoaded() {

    }

    @Override
    public void onTicksLoaded() {

    }

    @Override
    public void onFinished() {
        List<Route> routes = new ArrayList<Route>();
        for (Tick tick : mpModel.getTicks()) {
            if (tick.getRoute() != null)
            routes.add(tick.getRoute());
        }
        Route hardestRoute = routes.stream().max(
                (route1, route2) -> route1.getGrade().compareTo(route2.getGrade())).orElse(null);
        long hardestCount = routes.stream().filter((route) -> route.getGrade().compareTo(hardestRoute.getGrade()) == 0).count();

        Pyramid pyramid;
        if (hardestCount > 1){
            pyramid =  mpModel.buildPyramid(routes, RouteType.Sport, 5, 2, PyramidStepType.Additive, hardestRoute.getGrade().nextHardest());
        }
        else
            pyramid = mpModel.buildPyramid(routes, RouteType.Sport, 5, 2, PyramidStepType.Additive);
        SprayamidView view = (SprayamidView)findViewById(R.id.pyramidView);
        view.setPyramid(pyramid);
        view.invalidate();
    }
}
