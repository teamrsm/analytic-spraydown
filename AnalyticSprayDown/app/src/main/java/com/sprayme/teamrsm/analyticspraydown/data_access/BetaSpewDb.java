package com.sprayme.teamrsm.analyticspraydown.data_access;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.sprayme.teamrsm.analyticspraydown.models.Route;
import com.sprayme.teamrsm.analyticspraydown.models.Tick;
import com.sprayme.teamrsm.analyticspraydown.models.TickType;
import com.sprayme.teamrsm.analyticspraydown.models.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.sprayme.teamrsm.analyticspraydown.data_access.SqlGen.API_KEY;
import static com.sprayme.teamrsm.analyticspraydown.data_access.SqlGen.AVATAR_URL;
import static com.sprayme.teamrsm.analyticspraydown.data_access.SqlGen.EMAIL_ADDR;
import static com.sprayme.teamrsm.analyticspraydown.data_access.SqlGen.GRADE_ID;
import static com.sprayme.teamrsm.analyticspraydown.data_access.SqlGen.LAST_ACCESS;
import static com.sprayme.teamrsm.analyticspraydown.data_access.SqlGen.NOTES;
import static com.sprayme.teamrsm.analyticspraydown.data_access.SqlGen.ONSIGHT_PERCENTAGE;
import static com.sprayme.teamrsm.analyticspraydown.data_access.SqlGen.PITCHES;
import static com.sprayme.teamrsm.analyticspraydown.data_access.SqlGen.RATING;
import static com.sprayme.teamrsm.analyticspraydown.data_access.SqlGen.ROUTES_TABLE_NAME;
import static com.sprayme.teamrsm.analyticspraydown.data_access.SqlGen.ROUTE_ID;
import static com.sprayme.teamrsm.analyticspraydown.data_access.SqlGen.ROUTE_NAME;
import static com.sprayme.teamrsm.analyticspraydown.data_access.SqlGen.ROUTE_TYPE;
import static com.sprayme.teamrsm.analyticspraydown.data_access.SqlGen.ROUTE_URL;
import static com.sprayme.teamrsm.analyticspraydown.data_access.SqlGen.ROW_NUM;
import static com.sprayme.teamrsm.analyticspraydown.data_access.SqlGen.STARS;
import static com.sprayme.teamrsm.analyticspraydown.data_access.SqlGen.TICKS_TABLE_NAME;
import static com.sprayme.teamrsm.analyticspraydown.data_access.SqlGen.TICK_DATE;
import static com.sprayme.teamrsm.analyticspraydown.data_access.SqlGen.TICK_TYPE;
import static com.sprayme.teamrsm.analyticspraydown.data_access.SqlGen.USERS_TABLE_NAME;
import static com.sprayme.teamrsm.analyticspraydown.data_access.SqlGen.USER_ID;
import static com.sprayme.teamrsm.analyticspraydown.data_access.SqlGen.USER_NAME;

/****** import for debug helper ******/
import android.database.MatrixCursor;

/**
 * Created by Said on 10/8/2017.
 * Singleton class as a data access layer to the local SQLiteDatabase
 */

public class BetaSpewDb extends SQLiteOpenHelper {
  private static final int DATABASE_VERSION = 5;
  private static final String DATABASE_NAME = "BetaSpew.db";
  private static BetaSpewDb instance;

  public static synchronized BetaSpewDb getInstance(Context context) {
    if (instance == null) {
      instance = new BetaSpewDb(context.getApplicationContext());
    }

    return instance;
  }

  /*
  * Constructor, find a reference to the database with DATABASE_NAME,
  * or creates one if SQLite db does not exist. If existing db does not
  * match the DATABASE_VERSION, Upgrade logic is called.
  * */
  private BetaSpewDb(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  /*
  *
  * */
  @Override
  public void onCreate(SQLiteDatabase db) {
    db.beginTransaction();
    try {
      db.execSQL(SqlGen.makeUsersTableCreate());
      db.execSQL(SqlGen.makeTicksTableCreate());
      db.execSQL(SqlGen.makeRoutesTableCreate());
      db.execSQL(SqlGen.makeGradeMapTableCreate());
      db.setTransactionSuccessful();
    } catch (Exception e) {
      db.execSQL(SqlGen.makeRoutesTableDrop());
      db.execSQL(SqlGen.makeTicksTableDrop());
      db.execSQL(SqlGen.makeUsersTableDrop());
      db.execSQL(SqlGen.makeGradeMapTableDrop());
    } finally {
      db.endTransaction();
    }
  }


  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.beginTransaction();
    try {
      if (oldVersion < 2 && newVersion >= 2) {
                /* Added unique constraint to Ticks Table.
                 * Added NOT NULL to USER_ID, ROUTE_ID, TICK_DATE */

        //todo: get ticks
        db.execSQL(SqlGen.makeTicksTableDrop());
        db.execSQL(SqlGen.makeTicksTableCreate());
        // todo: reload ticks
      }
      if (oldVersion < 3 && newVersion >= 3) {
                /* Added NOT NULL UNIQUE ON CONFLICT TO ROUTE_ID */

        //todo: get routes
        db.execSQL(SqlGen.makeRoutesTableDrop());
        db.execSQL(SqlGen.makeRoutesTableCreate());
        //todo: reload routes
      }
      if (oldVersion < 4 && newVersion >= 4) {
                /* Added TICK_DATE to UNIQUE CONSTRAINT on TICKS Table */

        // todo: get ticks
        db.execSQL(SqlGen.makeTicksTableDrop());
        db.execSQL(SqlGen.makeTicksTableCreate());
        // todo: reload ticks
      }
      if (oldVersion < 5 && newVersion >= 5) {
                /* Added GradeMap. Added url to USERS table.*/

        db.execSQL(SqlGen.makeGradeMapTableCreate());
        db.execSQL(SqlGen.makeInsertGradeMapStatement());

        // todo: get users
        db.execSQL(SqlGen.makeUsersTableDrop());
        db.execSQL(SqlGen.makeUsersTableCreate());
        // todo: reload users
      }

      db.setTransactionSuccessful();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      db.endTransaction();
    }
  }

  /*
  * Retrieves the last known user
  * */
  public User getLastUser() {
    User lastUser = new User();

    SQLiteDatabase db = getReadableDatabase();
    Cursor cursor = db.rawQuery(SqlGen.makeGetLastUser(), null);

    try {
      if (cursor.moveToFirst()) {
        lastUser.setUserName(cursor.getString(cursor.getColumnIndex(USER_NAME)));
        lastUser.setUserId(cursor.getLong(cursor.getColumnIndex(USER_ID)));
        lastUser.setEmailAddr(cursor.getString(cursor.getColumnIndex(EMAIL_ADDR)));
        lastUser.setApiKey(cursor.getString(cursor.getColumnIndex(API_KEY)));
        lastUser.setAvatarUrl(cursor.getString(cursor.getColumnIndex(AVATAR_URL)));
      }

    } catch (Exception e) {
      e.printStackTrace();
    }

    return lastUser;
  }

  public List<User> getUsers() {
    List<User> users = new ArrayList<>();

    SQLiteDatabase db = getReadableDatabase();
    Cursor cursor = db.rawQuery(SqlGen.makeGetUsers(), null);

    try {
      while (cursor.moveToNext()) {
        String userName = cursor.getString(cursor.getColumnIndex(USER_NAME));
        Long userId = cursor.getLong(cursor.getColumnIndex(USER_ID));
        String email = cursor.getString(cursor.getColumnIndex(EMAIL_ADDR));
        String apiKey = cursor.getString(cursor.getColumnIndex(API_KEY));
        String avatarUrl = cursor.getString(cursor.getColumnIndex(AVATAR_URL));

        users.add(new User(userId, userName, email, apiKey, avatarUrl));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return users;
  }

  public Date getLastAccessTime(long userId) {
    long lastAccessLong = 0;

    SQLiteDatabase db = getReadableDatabase();
    Cursor cursor = db.rawQuery(SqlGen.makeLastUserAccess(userId), null);

    try {
      if (cursor.moveToFirst()) {
        lastAccessLong = cursor.getLong(cursor.getColumnIndex(LAST_ACCESS));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return new Date(lastAccessLong);
  }

  public void insertUser(User user) {
    SQLiteDatabase db = getWritableDatabase();

    db.beginTransaction();
    try {
      long unixDate = new Date().getTime();

      ContentValues insertValues = new ContentValues();
      insertValues.put(USER_ID, user.getUserId());
      insertValues.put(USER_NAME, user.getUserName());
      insertValues.put(EMAIL_ADDR, user.getEmailAddr());
      insertValues.put(API_KEY, user.getApiKey());
      insertValues.put(AVATAR_URL, user.getAvatarUrl());
      insertValues.put(LAST_ACCESS, unixDate);

      db.insertOrThrow(USERS_TABLE_NAME, null, insertValues);
      db.setTransactionSuccessful();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      db.endTransaction();
    }
  }

  public void updateAccessMoment(long userId) {
    SQLiteDatabase db = getWritableDatabase();

    db.beginTransaction();
    try {
      long unixDate = new Date().getTime();

      ContentValues updateValues = new ContentValues();
      updateValues.put(LAST_ACCESS, unixDate);

      db.update(USERS_TABLE_NAME, updateValues, SqlGen.makeUserIdWhereClause(userId), null);
      db.setTransactionSuccessful();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      db.endTransaction();
    }
  }

  /*
  * insert new ticks. Replace old ones with new values.
  * The table uses a unique constraint on USER_ID, ROUTE_ID with
  * a ON CONFLICT REPLACE clause replacing any rows which violate this condition.
  * */
  public void upsertTicks(List<Tick> userTicks, long userId) {
    SQLiteDatabase db = getWritableDatabase();

    db.beginTransaction();
    try {
      for (Tick tick : userTicks) {
        ContentValues insertValues = new ContentValues();
        insertValues.put(USER_ID, userId);
        insertValues.put(ROUTE_ID, tick.getRouteId());
        insertValues.put(TICK_DATE, tick.getDate().getTime());
        insertValues.put(NOTES, tick.getNotes());
        insertValues.put(TICK_TYPE, tick.getType().toString());

        db.insert(TICKS_TABLE_NAME, null, insertValues);
      }

      db.setTransactionSuccessful();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      db.endTransaction();
    }
  }

  public List<Tick> getTicks(long userId) {
    List<Tick> userTicks = new ArrayList<>();

    SQLiteDatabase db = getReadableDatabase();
    Cursor cursor = db.rawQuery(SqlGen.makeGetUserTicks(userId), null);

    try {
      while (cursor.moveToNext()) {
        long routeId = cursor.getLong(cursor.getColumnIndex(ROUTE_ID));
        Date tickDate = new Date(cursor.getLong(cursor.getColumnIndex(TICK_DATE)));
        Integer pitches = cursor.getInt(cursor.getColumnIndex(PITCHES));
        String notes = cursor.getString(cursor.getColumnIndex(NOTES));
        TickType tickType = TickType.valueOf(cursor.getString(cursor.getColumnIndex(TICK_TYPE)));
        Integer rowNum = cursor.getInt(cursor.getColumnIndex(ROW_NUM));

        boolean isRepeat = false;
        if (rowNum > 1)
          isRepeat = true;

        userTicks.add(new Tick(routeId, tickDate, pitches, notes, tickType, isRepeat));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return userTicks;
  }

  /* Inserts new values. Updates old values using
   * SQLite's ON CONFLICT REPLACE clause*/
  public void upsertRoutes(List<Route> routes) {
    SQLiteDatabase db = getWritableDatabase();

    db.beginTransaction();
    try {
      for (Route route : routes) {
        ContentValues upsertValues = new ContentValues();
        upsertValues.put(ROUTE_ID, route.getId());
        upsertValues.put(ROUTE_NAME, route.getName());
        upsertValues.put(ROUTE_TYPE, route.getType().toString());
        upsertValues.put(RATING, route.getGrade().toString());
        upsertValues.put(STARS, route.getStars());
        upsertValues.put(PITCHES, route.getPitches());
        upsertValues.put(ROUTE_URL, route.getUrl());

        db.insert(ROUTES_TABLE_NAME, null, upsertValues);
      }

      db.setTransactionSuccessful();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      db.endTransaction();
    }
  }

  public List<Route> getRoutes(Long[] routeIds) {
    List<Route> routes = new ArrayList<>();

    SQLiteDatabase db = getWritableDatabase();
    Cursor cursor = db.rawQuery(SqlGen.makeGetRoutes(routeIds), null);

    try {
      routes = parseRoutes(cursor);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      cursor.close();
    }

    return routes;
  }

  private List<Route> parseRoutes(Cursor cursor) {
    List<Route> routes = new ArrayList();

    while (cursor.moveToNext()) {
      Long routeId = cursor.getLong(cursor.getColumnIndex(ROUTE_ID));
      String routeName = cursor.getString(cursor.getColumnIndex(ROUTE_NAME));
      String routeType = cursor.getString(cursor.getColumnIndex(ROUTE_TYPE));
      String rating = cursor.getString(cursor.getColumnIndex(RATING));
      Float stars = cursor.getFloat(cursor.getColumnIndex(STARS));
      Integer pitches = cursor.getInt(cursor.getColumnIndex(PITCHES));
      String routeUrl = cursor.getString(cursor.getColumnIndex(ROUTE_URL));

      routes.add(new Route(routeId, routeName, routeType, rating, stars, pitches, routeUrl));
    }

    return routes;
  }

  public HashMap<Long, Float> getOnsightPercentages(long userId, String ratingType, String routeType) {
    HashMap<Long, Float> onsightPercentages = new HashMap<>();

    SQLiteDatabase db = getReadableDatabase();
    Cursor cursor = db.rawQuery(SqlGen.makeOnsightPercentage(userId, ratingType, routeType), null);

    try {
      while (cursor.moveToNext()) {
        Long gradeId = cursor.getLong(cursor.getColumnIndex(GRADE_ID));
        Float onsightPercentage = cursor.getFloat(cursor.getColumnIndex(ONSIGHT_PERCENTAGE));

        onsightPercentages.put(gradeId, onsightPercentage);
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      cursor.close();
    }

    return onsightPercentages;
  }

  /******* this function is a helper for AndroidDatabaseManager.java ********/
  public ArrayList<Cursor> getData(String Query) {
    //get writable database
    SQLiteDatabase sqlDB = this.getWritableDatabase();
    String[] columns = new String[]{"message"};
    //an array list of cursor to save two cursors one has results from the query
    //other cursor stores error message if any errors are triggered
    ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
    MatrixCursor Cursor2 = new MatrixCursor(columns);
    alc.add(null);
    alc.add(null);

    try {
      String maxQuery = Query;
      //execute the query results will be save in Cursor c
      Cursor c = sqlDB.rawQuery(maxQuery, null);

      //add value to cursor2
      Cursor2.addRow(new Object[]{"Success"});

      alc.set(1, Cursor2);
      if (null != c && c.getCount() > 0) {

        alc.set(0, c);
        c.moveToFirst();

        return alc;
      }
      return alc;
    } catch (SQLException sqlEx) {
      Log.d("printing exception", sqlEx.getMessage());
      //if any exceptions are triggered save the error message to cursor an return the arraylist
      Cursor2.addRow(new Object[]{"" + sqlEx.getMessage()});
      alc.set(1, Cursor2);
      return alc;
    } catch (Exception ex) {
      Log.d("printing exception", ex.getMessage());

      //if any exceptions are triggered save the error message to cursor an return the arraylist
      Cursor2.addRow(new Object[]{"" + ex.getMessage()});
      alc.set(1, Cursor2);
      return alc;
    }
  }
}

