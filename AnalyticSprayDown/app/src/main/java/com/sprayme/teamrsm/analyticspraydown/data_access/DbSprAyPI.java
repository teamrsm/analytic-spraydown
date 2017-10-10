package com.sprayme.teamrsm.analyticspraydown.data_access;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDoneException;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteStatement;

import com.sprayme.teamrsm.analyticspraydown.models.Route;
import com.sprayme.teamrsm.analyticspraydown.models.Tick;
import com.sprayme.teamrsm.analyticspraydown.models.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.sprayme.teamrsm.analyticspraydown.data_access.BetaSpewDbHelper.API_KEY;
import static com.sprayme.teamrsm.analyticspraydown.data_access.BetaSpewDbHelper.EMAIL_ADDR;
import static com.sprayme.teamrsm.analyticspraydown.data_access.BetaSpewDbHelper.PITCHES;
import static com.sprayme.teamrsm.analyticspraydown.data_access.BetaSpewDbHelper.RATING;
import static com.sprayme.teamrsm.analyticspraydown.data_access.BetaSpewDbHelper.ROUTES_TABLE_NAME;
import static com.sprayme.teamrsm.analyticspraydown.data_access.BetaSpewDbHelper.ROUTE_ID;
import static com.sprayme.teamrsm.analyticspraydown.data_access.BetaSpewDbHelper.ROUTE_NAME;
import static com.sprayme.teamrsm.analyticspraydown.data_access.BetaSpewDbHelper.ROUTE_TYPE;
import static com.sprayme.teamrsm.analyticspraydown.data_access.BetaSpewDbHelper.ROUTE_URL;
import static com.sprayme.teamrsm.analyticspraydown.data_access.BetaSpewDbHelper.STARS;
import static com.sprayme.teamrsm.analyticspraydown.data_access.BetaSpewDbHelper.USERS_TABLE_NAME;
import static com.sprayme.teamrsm.analyticspraydown.data_access.BetaSpewDbHelper.USER_ID;
import static com.sprayme.teamrsm.analyticspraydown.data_access.BetaSpewDbHelper.USER_NAME;

/**
 * Created by Said on 10/8/2017.
 */

public class DbSprAyPI extends Application {
    /**
     * DbSprAyPI(userId) x
     * GetUser x
     * GetTicks(fromMoment, toMoment)
     * InsertTicks(List<Ticks> ticks)
     * GetRoutes(List<int> routeIds)
     * InsertRoutes(List<Routes> Routes)
     * GetPyramid(RouteType, TickType, fromMoment, toMoment)
     * GetPyramid(RouteType, TickType, fromMoment, toMoment, NumTiers, stepSize, stepType)
     * GetOnsightLevel(minOnsightPercentage)
     * GetMaxRedpoint()
     */

    private static DbSprAyPI _instance;
    private BetaSpewDbHelper _dbHelper = null;
    private User _user;

    /*
    * Instantiate a class to gather information from the SQLite db within the scope
    * of a user. If no user exists in the db, we will create a new entry
    * */
    public DbSprAyPI(User user) throws InvalidUserException {
        _instance = this;
        _dbHelper = new BetaSpewDbHelper(_instance);

        if(!doesUserExist(user)) {
            createNewUser(user);
        }
    }

    /*
    * Queries the database and checks if the user exists. Updates internal user object
    * if we do in fact find the user from the local db.
    * */
    private boolean doesUserExist(User user) throws InvalidUserException {

        /* Build the sql statement. */
        StringBuilder sql = new StringBuilder()
                .append("SELECT *")
                .append(" FROM ").append(USERS_TABLE_NAME)
                .append(" WHERE ");

        if (user.getUserId() != null)
            sql.append(USER_ID).append(" = ").append(user.getUserId());
        else if (user.getEmailAddr() != null)
            sql.append(EMAIL_ADDR).append(" = '").append(user.getEmailAddr()).append("'");
        else
            throw new InvalidUserException("Not enough user information to query db!");

        String sqlString = sql.toString();
        long userCount = -1;
        Cursor cursor = null;

        try {
            SQLiteDatabase sqlDb = _dbHelper.getReadableDatabase();
            cursor = sqlDb.rawQuery(sqlString,
                    new String[]{USER_ID, USER_NAME, EMAIL_ADDR, API_KEY});

            userCount = cursor.getCount();
            if (userCount == 0)
                return false;

            _user = ParseUser(cursor);
        } catch (SQLiteException sExc) {
            sExc.printStackTrace();
        }
        finally {
            cursor.close();
        }

        if (userCount == 1)
            return true;
        else
            return false;
    }

    /* Creates a new user. */
    private void createNewUser(User user) throws InvalidUserException {
        if (user.getApiKey() == null)
            throw new InvalidUserException("No API key provided for new user.");

        String sql = new StringBuilder()
                .append("INSERT INTO ").append(USERS_TABLE_NAME)
                .append(" ( ").append(USER_ID).append(", ")
                .append(USER_NAME).append(", ")
                .append(EMAIL_ADDR).append(", ")
                .append(API_KEY).append(") ")
                .append(" VALUES (")
                .append(user.getUserId()).append(", ")
                .append(user.getUserName()).append(", ")
                .append(user.getEmailAddr()).append(", ")
                .append(user.getApiKey()).append(")").toString();

        SQLiteStatement stmt = null;
        try {
            SQLiteDatabase sqlDb = _dbHelper.getWritableDatabase();
            stmt = sqlDb.compileStatement(sql);

            stmt.executeInsert();
        } catch (SQLiteException sExc) {
            sExc.printStackTrace();
        }
    }

    /*
    * Parse the cursor object returned from the sql query and return a User object.
    * */
    private User ParseUser(Cursor cursor) throws InvalidUserException {
        User dbSourcedUser = new User();

        cursor.moveToFirst();

        long userId = cursor.getLong(cursor.getColumnIndex(USER_ID));
        String userName = cursor.getString(cursor.getColumnIndex(USER_NAME));
        String emailAddr = cursor.getString(cursor.getColumnIndex(EMAIL_ADDR));
        String apiKey = cursor.getString(cursor.getColumnIndex(API_KEY));

        dbSourcedUser.setUserId(userId);
        dbSourcedUser.setUserName(userName);
        dbSourcedUser.setEmailAddr(emailAddr);
        dbSourcedUser.setApiKey(apiKey);

        return dbSourcedUser;
    }

    /* Returns the contextual user. */
    public User GetUser() {
        return _user;
    }

    public List<Tick> getTicks(Date fromMoment, Date toMoment) {
        List<Tick> ticks = new ArrayList();

        // todo: get ticks from db where userid
        // todo: get routes from db where routeid
        // todo: match objects and return

        return ticks;
    }

    public List<Route> getRoutes(List<Integer> routeIds) {

        List<Route> routes = new ArrayList();

        String sql = new StringBuilder()
                .append("SELECT *")
                .append(" FROM ").append(ROUTES_TABLE_NAME)
                .append(" WHERE ")
                .append(ROUTE_ID).append(" IN (")
                .append(buildInList(routeIds))
                .append(")").toString();

        Cursor cursor = null;

        try {
            SQLiteDatabase sqlDb = _dbHelper.getReadableDatabase();
            cursor = sqlDb.rawQuery(sql, null);

            routes = ParseRoutes(cursor);
        } catch (SQLiteException sExc) {
            sExc.printStackTrace();
        } finally {
            cursor.close();
        }

        return routes;
    }

    private List<Route> ParseRoutes(Cursor cursor) {
        List<Route> routes = new ArrayList();

        while (cursor.moveToNext()) {
            Long routeId = cursor.getLong(cursor.getColumnIndex(ROUTE_ID));
            String routeName = cursor.getString(cursor.getColumnIndex(ROUTE_NAME));
            String routeType = cursor.getString(cursor.getColumnIndex(ROUTE_TYPE));
            String rating = cursor.getString(cursor.getColumnIndex(RATING));
            Integer stars = cursor.getInt(cursor.getColumnIndex(STARS));
            Integer pitches = cursor.getInt(cursor.getColumnIndex(PITCHES));
            String routeUrl = cursor.getString(cursor.getColumnIndex(ROUTE_URL));

            // todo: pull changes and merge with route
            //routes.add(new Route(routeId, routeName, routeType, rating, stars, pitches, routeUrl));
        }

        return routes;
    }

    private String buildInList(List<Integer> inInts) {
        StringBuilder inList = new StringBuilder();

        for (Integer i : inInts) {
            inList.append(i).append(",");
        }

        int lastIndex = inList.lastIndexOf(",");
        return inList.substring(0, lastIndex - 1).toString();
    }

class InvalidUserException extends Exception
{
    public InvalidUserException() {}

    // Constructor that accepts a message
    public InvalidUserException(String message)
    {
        super(message);
    }
}