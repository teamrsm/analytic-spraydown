package com.sprayme.teamrsm.analyticspraydown;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerUIUtils;
import com.sprayme.teamrsm.analyticspraydown.data_access.BetaSpewDb;
import com.sprayme.teamrsm.analyticspraydown.data_access.InvalidUserException;
import com.sprayme.teamrsm.analyticspraydown.models.MPProfileDrawerItem;
import com.sprayme.teamrsm.analyticspraydown.models.Pyramid;
import com.sprayme.teamrsm.analyticspraydown.models.PyramidStepType;
import com.sprayme.teamrsm.analyticspraydown.models.Route;
import com.sprayme.teamrsm.analyticspraydown.models.RouteType;
import com.sprayme.teamrsm.analyticspraydown.models.Tick;
import com.sprayme.teamrsm.analyticspraydown.models.TickType;
import com.sprayme.teamrsm.analyticspraydown.models.TimeScale;
import com.sprayme.teamrsm.analyticspraydown.models.User;
import com.sprayme.teamrsm.analyticspraydown.uicomponents.RecyclerAdapter;
import com.sprayme.teamrsm.analyticspraydown.uicomponents.SpinnerFragment;
import com.sprayme.teamrsm.analyticspraydown.utilities.AndroidDatabaseManager;
import com.sprayme.teamrsm.analyticspraydown.utilities.DataCache;
import com.sprayme.teamrsm.analyticspraydown.utilities.SprayarificStructures;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

  static final int LOGIN_FIRST_USER_REQUEST = 1;
  static final int LOGIN_NEW_USER_REQUEST = 2;
  static final int SETTINGS_REQUEST = 2;
  static final int IMPORT_REQUEST = 3;
  private static final int PROFILE_ADD = 100000;

  public static SharedPreferences mSharedPref;
  private TimeScale mTimeScale = TimeScale.Year;
  private int mTimeScaleSpinnerPos = 3;

  private DataCache dataCache = null;
  private BetaSpewDb db = null;
  private User currentUser = null;
  private DrawerLayout mDrawerLayout;
  private Drawer mDrawer;
  private AccountHeader mAccountHeader;
  private RecyclerView mRecyclerView;
  private RecyclerAdapter mRecyclerAdapter;
  private Fragment mSpinnerFragment;
  private static ConcurrentHashMap<RouteType, Pyramid> pyramids = new ConcurrentHashMap<>();

  private boolean m_InitializingUsers = false;
  private boolean showProgressOnResume = false;
  private boolean requestingNewUser = false;
  private String mActivityTitle;
  private UUID profileCallbackUuid, ticksCallbackUuid;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main_page);

//        this.deleteDatabase("BetaSpew.db");
    db = BetaSpewDb.getInstance(this);

    mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
    mActivityTitle = getTitle().toString();

    mSpinnerFragment = new SpinnerFragment();

    setupDrawer();

    Button button = (Button) findViewById(R.id.viewDbButton);

    //RecyclerView
    mRecyclerView = (RecyclerView) findViewById(R.id.pyramid_recycler_view);
    mRecyclerView.setHasFixedSize(true);
    mRecyclerView.setHorizontalScrollBarEnabled(false);

    mRecyclerAdapter = new RecyclerAdapter(this, getData());
    mRecyclerView.setAdapter(mRecyclerAdapter);

    //Layout manager for the Recycler View
    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
    mRecyclerView.setLayoutManager(mLayoutManager);

    SnapHelper snapHelper = new PagerSnapHelper();
    snapHelper.attachToRecyclerView(mRecyclerView);

    PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    mSharedPref = PreferenceManager.getDefaultSharedPreferences(this);

    // time scale selection spinner
    Spinner spinner = (Spinner) findViewById(R.id.time_scale_spinner);
    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
            R.array.time_scale_spinner_entries, android.R.layout.simple_spinner_item);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spinner.setAdapter(adapter);
    spinner.setSelection(3);
    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
          case 0:
            mTimeScale = TimeScale.Month;
            break;
          case 1:
            mTimeScale = TimeScale.ThreeMonth;
            break;
          case 2:
            mTimeScale = TimeScale.SixMonth;
            break;
          case 3:
            mTimeScale = TimeScale.Year;
            break;
          case 4:
            mTimeScale = TimeScale.Lifetime;
            break;
        }

        triggerCacheUpdate();
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {
      }
    });

    /** for debugging the db **/
    Context context = this;
    button.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {

        Intent dbmanager = new Intent(context, AndroidDatabaseManager.class);
        startActivity(dbmanager);
      }
    });
  }

  private void setupDrawer() {

    Context context = this;

    // Create the AccountHeader
    mAccountHeader = new AccountHeaderBuilder()
            .withActivity(this)
            .withHeaderBackground(R.drawable.background_material)
            .addProfiles(
                    new ProfileSettingDrawerItem().withName(R.string.add_user).withIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_add).actionBar().paddingDp(5).colorRes(R.color.material_drawer_primary_text)).withIdentifier(PROFILE_ADD)
//                        new ProfileSettingDrawerItem().withName("Manage Account").withIcon(GoogleMaterial.Icon.gmd_settings).withIdentifier(100001)
            ).withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
              @Override
              public boolean onProfileChanged(View view, IProfile profile, boolean current) {
                if (current)
                  return false;

                return onSelectedProfileChanged(profile);
              }
            })
            .withCloseDrawerOnProfileListClick(true)
            .withOnlyMainProfileImageVisible(true)
            .build();

    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setTitle(mActivityTitle);

    mDrawer = new DrawerBuilder()
            .withActivity(this)
            .withRootView(R.id.drawer_container)
            .withAccountHeader(mAccountHeader)
            .withToolbar(toolbar)
            .inflateMenu(R.menu.drawer_menu)
            .withDisplayBelowStatusBar(false)
            .withActionBarDrawerToggleAnimated(true)
            .withSelectedItem(-1)
            .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
              @Override
              public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                switch ((int) drawerItem.getIdentifier()) {

                  case R.id.settings:
                    Intent intent = new Intent(context, SettingsActivity.class);
                    startActivityForResult(intent, SETTINGS_REQUEST);
                    return true;
                  //                    case R.id.import_csv:
                  //                        Intent intent = new Intent(context, ImportCsvActivity.class);
                  //                        startActivityForResult(intent, IMPORT_REQUEST);
                  //                        return true;
                  default:
                    Toast.makeText(getApplicationContext(), "Something's wrong", Toast.LENGTH_SHORT).show();
                    return false;

                }
              }
            }).build();

    //initialize and create the profile image loader logic
    DrawerImageLoader.init(new AbstractDrawerImageLoader() {
      @Override
      public void set(ImageView imageView, Uri uri, Drawable placeholder, String tag) {
        Glide.with(imageView.getContext()).load(uri).placeholder(placeholder).into(imageView);
      }

      @Override
      public void cancel(ImageView imageView) {
        Glide.clear(imageView);
      }

      @Override
      public Drawable placeholder(Context ctx, String tag) {
        //define different placeholders for different imageView targets
        //default tags are accessible via the DrawerImageLoader.Tags
        //custom ones can be checked via string. see the CustomUrlBasePrimaryDrawerItem LINE 111
        if (DrawerImageLoader.Tags.PROFILE.name().equals(tag)) {
          return DrawerUIUtils.getPlaceHolder(ctx);
        } else if (DrawerImageLoader.Tags.ACCOUNT_HEADER.name().equals(tag)) {
          return new IconicsDrawable(ctx).iconText(" ").backgroundColorRes(com.mikepenz.materialdrawer.R.color.primary).sizeDp(56);
        } else if ("customUrlItem".equals(tag)) {
          return new IconicsDrawable(ctx).iconText(" ").backgroundColorRes(R.color.md_red_500).sizeDp(56);
        }

        //we use the default one for
        //DrawerImageLoader.Tags.PROFILE_DRAWER_ITEM.name()

        return super.placeholder(ctx, tag);
      }
    });
  }

  @Override
  protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);

    Spinner spinner = (Spinner) findViewById(R.id.time_scale_spinner);
    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position == mTimeScaleSpinnerPos)
          return;
        else
          mTimeScaleSpinnerPos = position;

        switch (position) {
          case 0:
            mTimeScale = TimeScale.Month;
            break;
          case 1:
            mTimeScale = TimeScale.ThreeMonth;
            break;
          case 2:
            mTimeScale = TimeScale.SixMonth;
            break;
          case 3:
            mTimeScale = TimeScale.Year;
            break;
          case 4:
            mTimeScale = TimeScale.Lifetime;
            break;
        }

        triggerCacheUpdate();
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {
      }
    });

    boolean canLaunchLogin = false;

    try {
      dataCache = DataCache.getInstance();
      dataCache.setDb(db);
      ticksCallbackUuid = dataCache.subscribe(new DataCache.DataCacheTicksHandler() {
        @Override
        public void onTicksCached(List<Tick> ticks) {
          onFinished(ticks);
        }
      });
      initUsers();
      triggerCacheUpdate();
    } catch (InvalidUserException e) {
            /* launch login, we have no known user */
      canLaunchLogin = true;
    }

    dataCache.calculateOnsightLevel("YOSEMITE", "Sport");

    if (canLaunchLogin) {
      Intent loginIntent = new Intent(this, UserLoginActivity.class);
      startActivityForResult(loginIntent, LOGIN_FIRST_USER_REQUEST);
    }
  }

  private void initUsers() throws InvalidUserException {
    m_InitializingUsers = true;
    // todo figure out timing of this
    if (profileCallbackUuid == null)
      profileCallbackUuid = dataCache.subscribe(new DataCache.DataCacheProfileHandler() {
        @Override
        public void onProfileCached(MPProfileDrawerItem profile) {
          if (profile == null || m_InitializingUsers)
            return;

          mAccountHeader.addProfile(profile, 0);
          mAccountHeader.setActiveProfile(profile, true);
        }
      });

    List<MPProfileDrawerItem> profiles = dataCache.getUserProfiles();
    currentUser = dataCache.getCurrentUser() != null ? dataCache.getCurrentUser() : dataCache.getLastUser();
    MPProfileDrawerItem currentProfile = null;
    int count = 0;
    for (MPProfileDrawerItem profile : profiles) {
      int position = profile.getUser() == currentUser || count++ == 0 ? 0 : 1;
      if (position == 0)
        currentProfile = profile;
      mAccountHeader.addProfile(profile, position);
    }

    mAccountHeader.setActiveProfile(currentProfile);
    m_InitializingUsers = false;
  }

  private boolean onSelectedProfileChanged(IProfile profile){
    if (profile instanceof IDrawerItem && profile.getIdentifier() == PROFILE_ADD) {
      Intent loginIntent = new Intent(this, UserLoginActivity.class);
      startActivityForResult(loginIntent, LOGIN_NEW_USER_REQUEST);
      requestingNewUser = true;
      return true;
    }

    if (profile instanceof MPProfileDrawerItem){
      onActiveUserChanged(((MPProfileDrawerItem)profile).getUser());
      return true;
    }
    return false;
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    // Check which request we're responding to
    if (requestCode == LOGIN_FIRST_USER_REQUEST) {
      // Make sure the request was successful
      if (resultCode == RESULT_OK) {
        try {
          initUsers();
          triggerCacheUpdate();
        }
        catch (InvalidUserException e) {
          Toast.makeText(this, "Init users failed after successfully adding a user", Toast.LENGTH_LONG).show();}
      }
    }
    if (requestCode == LOGIN_NEW_USER_REQUEST) {
      // Make sure the request was successful
      if (resultCode == RESULT_OK) {
        requestingNewUser = false;
        triggerCacheUpdate(true);
      }
    }

    if (requestCode == SETTINGS_REQUEST) {
      // todo probably force a refresh
    }

    if (requestCode == IMPORT_REQUEST) {
      // Make sure the request was successful
      if (resultCode == RESULT_OK) {
        triggerCacheUpdate(true);
      }
    }
  }

  @Override
  public void onSaveInstanceState(Bundle savedInstanceState) {
    // Always call the superclass so it can save the view hierarchy state
    super.onSaveInstanceState(savedInstanceState);
  }
  @Override
  public void onRestoreInstanceState(Bundle savedInstanceState) {
    // Always call the superclass so it can restore the view hierarchy
    super.onRestoreInstanceState(savedInstanceState);
  }

  @Override
  public void onStart(){
    super.onStart();
    if (showProgressOnResume){
      showProgressOnResume = false;
      showProgress();
    }
  }

  @Override
  public void onStop(){

    super.onStop();
  }

  @Override
  public void onPause(){
    super.onPause();
  }

  @Override
  public void onResume(){
    super.onResume();
    if (showProgressOnResume){
      showProgressOnResume = false;
      showProgress();
    }
  }

  public void onFinished(List<Tick> ticks) {
    Set<Route> routes = new HashSet<Route>();
    boolean ignoreTopropes = MainActivity.mSharedPref.getBoolean(SettingsActivity.KEY_PREF_USE_ONLY_LEADS, true);
    for (Tick tick : ticks) {
      if (tick.getRoute() == null)
        continue;

      // filter for ignore toprope setting
      if (ignoreTopropes && tick.getRoute().getType() != RouteType.Boulder) {
        TickType tickType = tick.getType();
        boolean skip = tickType == TickType.Fell;
        skip |= tickType == TickType.Toprope;
        skip |= tickType == TickType.Unknown;
        if (skip)
          continue;
      }

      // filter for the selected time scale
      if (mTimeScale != TimeScale.Lifetime) {
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        switch (mTimeScale) {
          case Month:
            cal.add(Calendar.MONTH, -1);
            break;
          case ThreeMonth:
            cal.add(Calendar.MONTH, -3);
            break;
          case SixMonth:
            cal.add(Calendar.MONTH, -6);
            break;
          case Year:
            cal.add(Calendar.YEAR, -1);
            break;
          default:
            break;
        }
        date = cal.getTime();
        if (date.getTime() > tick.getDate().getTime())
          continue;
      }

      routes.add(tick.getRoute());
    }
    if (routes.size() == 0)
      return;

    // todo refactor these Collectors.toList() calls to comply with api level 22
    int height = Integer.valueOf(mSharedPref.getString(SettingsActivity.KEY_PREF_PYRAMID_HEIGHT, "5"));
    int stepSize = Integer.valueOf(mSharedPref.getString(SettingsActivity.KEY_PREF_PYRAMID_STEP_MODIFIER_SIZE, "2"));
    String stepTypeStr = mSharedPref.getString(SettingsActivity.KEY_PREF_PYRAMID_STEP_MODIFIER_TYPE, "Additive");
    PyramidStepType stepType = PyramidStepType.valueOf(stepTypeStr);
    pyramids.clear();
    if (mSharedPref.getBoolean(SettingsActivity.KEY_PREF_SHOW_ROUTE_PYRAMID, false))
      pyramids.put(RouteType.Route, SprayarificStructures.buildPyramid(routes.stream().collect(Collectors.toList()), RouteType.Route, height, stepSize, stepType));

    if (mSharedPref.getBoolean(SettingsActivity.KEY_PREF_SHOW_SPORT_PYRAMID, true))
      pyramids.put(RouteType.Sport, SprayarificStructures.buildPyramid(routes.stream().collect(Collectors.toList()), RouteType.Sport, height, stepSize, stepType));

    if (mSharedPref.getBoolean(SettingsActivity.KEY_PREF_SHOW_TRAD_PYRAMID, true))
      pyramids.put(RouteType.Trad, SprayarificStructures.buildPyramid(routes.stream().collect(Collectors.toList()), RouteType.Trad, height, stepSize, stepType));

    if (mSharedPref.getBoolean(SettingsActivity.KEY_PREF_SHOW_BOULDER_PYRAMID, true))
      pyramids.put(RouteType.Boulder, SprayarificStructures.buildPyramid(routes.stream().collect(Collectors.toList()), RouteType.Boulder, height, stepSize, stepType));

    if (mSharedPref.getBoolean(SettingsActivity.KEY_PREF_SHOW_ICE_PYRAMID, false))
      pyramids.put(RouteType.Ice, SprayarificStructures.buildPyramid(routes.stream().collect(Collectors.toList()), RouteType.Ice, height, stepSize, stepType));

    if (mSharedPref.getBoolean(SettingsActivity.KEY_PREF_SHOW_AID_PYRAMID, false))
      pyramids.put(RouteType.Aid, SprayarificStructures.buildPyramid(routes.stream().collect(Collectors.toList()), RouteType.Aid, height, stepSize, stepType));

    mRecyclerAdapter.update(getData());

    hideProgress();
  }

  public static List<Pyramid> getData() {
    List<Pyramid> subActivityData = new ArrayList<>();

    if (pyramids.containsKey(RouteType.Route))
      subActivityData.add(pyramids.get(RouteType.Route));
    if (pyramids.containsKey(RouteType.Sport))
      subActivityData.add(pyramids.get(RouteType.Sport));
    if (pyramids.containsKey(RouteType.Trad))
      subActivityData.add(pyramids.get(RouteType.Trad));
    if (pyramids.containsKey(RouteType.Boulder))
      subActivityData.add(pyramids.get(RouteType.Boulder));
    if (pyramids.containsKey(RouteType.Ice))
      subActivityData.add(pyramids.get(RouteType.Ice));
    if (pyramids.containsKey(RouteType.Aid))
      subActivityData.add(pyramids.get(RouteType.Aid));

    return subActivityData;
  }

  private void onActiveUserChanged(User user){
    dataCache.setCurrentUser(user);
    if (!requestingNewUser)
      triggerCacheUpdate();
  }

  private void triggerCacheUpdate(){
    triggerCacheUpdate(false);
  }
  private void triggerCacheUpdate(boolean delayProgress){
    if (!delayProgress)
      showProgress();
    else
      showProgressOnResume = true;
    dataCache.loadUserTicks();
  }

  private void showProgress(){
    mSpinnerFragment = new SpinnerFragment();
    getFragmentManager().beginTransaction().add(R.id.drawer_container, mSpinnerFragment).commit();
  }

  private void hideProgress(){
    if (mSpinnerFragment == null)
      return;
    getFragmentManager().beginTransaction().remove(mSpinnerFragment).commit();
    mSpinnerFragment = null;
  }
}
