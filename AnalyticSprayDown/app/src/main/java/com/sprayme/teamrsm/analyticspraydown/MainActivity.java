package com.sprayme.teamrsm.analyticspraydown;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.sprayme.teamrsm.analyticspraydown.data_access.BetaSpewDb;
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

    private MPModel mpModel = null;
    private DataCache dataCache = null;
    private BetaSpewDb db = null;
    private User currentUser = null;

    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private String mActivityTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mpModel = new MPModel(this);
        dataCache = DataCache.getInstance();
        //this.deleteDatabase("BetaSpew.db");
        db = BetaSpewDb.getInstance(this);

        mDrawerList = (ListView)findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();

        addDrawerItems();
        setupDrawer();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void addDrawerItems() {
        String[] osArray = { /*"Stats", "Training",*/ "Settings" };
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "Time for an upgrade!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Navigation!");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        try {
            dataCache.setDb(db);
            dataCache.setMp(mpModel);
            currentUser = dataCache.getLastUser();
        } catch (InvalidUserException e) {
            // todo: launch login, we have no known user
            Intent intent = new Intent(this, UserLoginActivity.class);
            startActivity(intent);
        }
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
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

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
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
    public void onUserLoaded() {

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
