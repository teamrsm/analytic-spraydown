package com.sprayme.teamrsm.analyticspraydown;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SettingsActivity extends AppCompatActivity {

  public static final String KEY_PREF_CACHE_TIMEOUT = "cache_invalidate_time_preference";
  public static final String KEY_PREF_GRADE_LOCALE = "grade_system_preference";
  public static final String KEY_PREF_USE_ONLY_LEADS = "only_use_leads_preference";
  public static final String KEY_PREF_ALWAYS_BUILD_OPTIMISTIC = "always_optimistic_preference";
  public static final String KEY_PREF_IGNORE_DUPLICATES = "ignore_duplicates_preference";
  public static final String KEY_PREF_SHOW_ROUTE_PYRAMID = "build_route_pyramid_preference";
  public static final String KEY_PREF_SHOW_SPORT_PYRAMID = "build_sport_pyramid_preference";
  public static final String KEY_PREF_SHOW_TRAD_PYRAMID = "build_trad_pyramid_preference";
  public static final String KEY_PREF_SHOW_BOULDER_PYRAMID = "build_boulder_pyramid_preference";
  public static final String KEY_PREF_SHOW_ICE_PYRAMID = "build_ice_pyramid_preference";
  public static final String KEY_PREF_SHOW_AID_PYRAMID = "build_aid_pyramid_preference";
  public static final String KEY_PREF_PYRAMID_HEIGHT = "pyramid_height_preference";
  public static final String KEY_PREF_PYRAMID_STEP_MODIFIER_SIZE = "pyramid_step_modifier_size_preference";
  public static final String KEY_PREF_PYRAMID_STEP_MODIFIER_TYPE = "pyramid_step_modifier_type_preference";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getSupportFragmentManager().beginTransaction()
            .replace(android.R.id.content, new SettingsFragment())
            .commit();
  }
}
