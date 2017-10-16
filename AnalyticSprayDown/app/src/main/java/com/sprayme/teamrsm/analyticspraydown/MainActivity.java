package com.sprayme.teamrsm.analyticspraydown;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.sprayme.teamrsm.analyticspraydown.data_access.BetaSpewDb;
import com.sprayme.teamrsm.analyticspraydown.data_access.InvalidUserException;
import com.sprayme.teamrsm.analyticspraydown.models.Pyramid;
import com.sprayme.teamrsm.analyticspraydown.models.PyramidStepType;
import com.sprayme.teamrsm.analyticspraydown.models.Route;
import com.sprayme.teamrsm.analyticspraydown.models.RouteType;
import com.sprayme.teamrsm.analyticspraydown.models.Tick;
import com.sprayme.teamrsm.analyticspraydown.models.TickType;
import com.sprayme.teamrsm.analyticspraydown.models.User;
import com.sprayme.teamrsm.analyticspraydown.utilities.AndroidDatabaseManager;
import com.sprayme.teamrsm.analyticspraydown.utilities.DataCache;
import com.sprayme.teamrsm.analyticspraydown.utilities.SprayarificStructures;
import com.sprayme.teamrsm.analyticspraydown.uicomponents.RecyclerAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    static final int LOGIN_REQUEST = 1;

    public static SharedPreferences mSharedPref;

    private DataCache dataCache = null;
    private BetaSpewDb db = null;
    private User currentUser = null;

    private ListView mDrawerList;
    private ArrayAdapter<String> mArrayAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private RecyclerView mRecyclerView;
    private RecyclerAdapter mRecyclerAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static ConcurrentHashMap<RouteType, Pyramid> pyramids = new ConcurrentHashMap<>();

    private String mActivityTitle;
    private UUID userCallbackUuid, ticksCallbackUuid, routesCallbackUuid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        this.deleteDatabase("BetaSpew.db");
        db = BetaSpewDb.getInstance(this);

        mDrawerList = (ListView)findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();

        addDrawerItems();
        setupDrawer();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Button button =(Button)findViewById(R.id.viewDbButton);

        //RecyclerView
        mRecyclerView = (RecyclerView) findViewById(R.id.pyramid_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setHorizontalScrollBarEnabled(false);

        mRecyclerAdapter = new RecyclerAdapter(this, getData());
        mRecyclerView.setAdapter(mRecyclerAdapter);

        //Layout manager for the Recycler View
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(mRecyclerView);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        mSharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        /** for debugging the db **/
        Context context = this;
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent dbmanager = new Intent(context,AndroidDatabaseManager.class);
                startActivity(dbmanager);
            }
        });
    }

    private void addDrawerItems() {
        String[] osArray = { /*"Stats", "Training",*/ "Settings" };
        mArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mArrayAdapter);

        Context context = this;
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//                if (id == R.id.action_settings) {
                    Intent intent = new Intent(context, SettingsActivity.class);
                    startActivity(intent);
//                    return true;
//                }
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
        boolean canLaunchLogin = false;

        try {
            dataCache = DataCache.getInstance();
            dataCache.setDb(db);
            currentUser = dataCache.getLastUser();
            ticksCallbackUuid = dataCache.subscribe(new DataCache.DataCacheTicksHandler() {
                @Override
                public void onTicksCached(List<Tick> ticks) {
                    if (dataCache.unsubscribeTicksHandler(ticksCallbackUuid))
                        ticksCallbackUuid = null;

                    onFinished(ticks);
                }
            });
            dataCache.loadUserTicks();
        } catch (InvalidUserException e) {
            /* launch login, we have no known user */
            canLaunchLogin = true;
        }

        if (canLaunchLogin) {
            Intent loginIntent = new Intent(this, UserLoginActivity.class);
            startActivityForResult(loginIntent, LOGIN_REQUEST);
        }

        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == LOGIN_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                currentUser = dataCache.getCurrentUser();

                ticksCallbackUuid = dataCache.subscribe(new DataCache.DataCacheTicksHandler() {
                    @Override
                    public void onTicksCached(List<Tick> ticks) {
                        if (dataCache.unsubscribeTicksHandler(ticksCallbackUuid))
                            ticksCallbackUuid = null;

                        onFinished(ticks);
                    }
                });
                dataCache.loadUserTicks();
            }
        }
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
//        if (itemThatWasClickedId == R.id.build_pyramid) {
//            ticksCallbackUuid = dataCache.subscribe(new DataCache.DataCacheTicksHandler() {
//                @Override
//                public void onTicksCached(List<Tick> ticks) {
//                    if (dataCache.unsubscribeTicksHandler(ticksCallbackUuid))
//                        ticksCallbackUuid = null;
//
//                    onFinished(ticks);
//                }
//            });
//            dataCache.loadUserTicks();
//            return true;
//        }

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onFinished(List<Tick> ticks) {
        Set<Route> routes = new HashSet<Route>();
//        boolean ignoreTopropes = MainActivity.mSharedPref.getBoolean(SettingsActivity.KEY_PREF_USE_ONLY_LEADS, true);
        for (Tick tick : ticks) {
//            if (ignoreTopropes){
//                TickType tickType = tick.getType();
//                boolean skip = tickType == TickType.Fell;
//                skip |= tickType == TickType.Toprope;
//                skip |= tickType == TickType.Unknown;
//                if (skip)
//                    continue;
//            }
            if (tick.getRoute() != null)
            routes.add(tick.getRoute());
        }

        if (MainActivity.mSharedPref.getBoolean(SettingsActivity.KEY_PREF_SHOW_SPORT_PYRAMID, true))
            pyramids.put(RouteType.Sport, SprayarificStructures.buildPyramid(routes.stream().collect(Collectors.toList()), RouteType.Sport, 5, 2, PyramidStepType.Additive));

        if (MainActivity.mSharedPref.getBoolean(SettingsActivity.KEY_PREF_SHOW_TRAD_PYRAMID, true))
            pyramids.put(RouteType.Trad, SprayarificStructures.buildPyramid(routes.stream().collect(Collectors.toList()), RouteType.Trad, 5, 2, PyramidStepType.Additive));

        if (MainActivity.mSharedPref.getBoolean(SettingsActivity.KEY_PREF_SHOW_BOULDER_PYRAMID, true))
            pyramids.put(RouteType.Boulder, SprayarificStructures.buildPyramid(routes.stream().collect(Collectors.toList()), RouteType.Boulder, 5, 2, PyramidStepType.Additive));

        if (MainActivity.mSharedPref.getBoolean(SettingsActivity.KEY_PREF_SHOW_ICE_PYRAMID, true))
            pyramids.put(RouteType.Ice, SprayarificStructures.buildPyramid(routes.stream().collect(Collectors.toList()), RouteType.Ice, 5, 2, PyramidStepType.Additive));

        if (MainActivity.mSharedPref.getBoolean(SettingsActivity.KEY_PREF_SHOW_AID_PYRAMID, true))
            pyramids.put(RouteType.Aid, SprayarificStructures.buildPyramid(routes.stream().collect(Collectors.toList()), RouteType.Aid, 5, 2, PyramidStepType.Additive));

        mRecyclerAdapter.update(getData());
    }

    public static List<Pyramid> getData() {
        List<Pyramid> subActivityData = new ArrayList<>();
        subActivityData.add(pyramids.get(RouteType.Sport));
        subActivityData.add(pyramids.get(RouteType.Trad));
        subActivityData.add(pyramids.get(RouteType.Boulder));

        return subActivityData;
    }
}
