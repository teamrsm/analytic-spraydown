package com.sprayme.teamrsm.analyticspraydown;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.sprayme.teamrsm.analyticspraydown.models.MPModel;
import com.sprayme.teamrsm.analyticspraydown.models.Pyramid;
import com.sprayme.teamrsm.analyticspraydown.models.PyramidStepType;
import com.sprayme.teamrsm.analyticspraydown.models.Route;
import com.sprayme.teamrsm.analyticspraydown.models.RouteType;
import com.sprayme.teamrsm.analyticspraydown.models.Tick;
import com.sprayme.teamrsm.analyticspraydown.views.SprayamidView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
    implements MPModel.MPModelListener {

    MPModel mpModel = new MPModel(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
            mpModel.requestTicks("thebigrokh@gmail.com");
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
            routes.add(tick.getRoute());
        }
        Pyramid pyramid = mpModel.buildPyramid(routes, RouteType.Sport, 4, 2, PyramidStepType.Additive);
        SprayamidView view = (SprayamidView)findViewById(R.id.pyramidView);
        view.setPyramid(pyramid);
        view.invalidate();
    }
}
