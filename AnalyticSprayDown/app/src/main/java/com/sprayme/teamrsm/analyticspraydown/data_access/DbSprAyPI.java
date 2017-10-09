package com.sprayme.teamrsm.analyticspraydown.data_access;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;

import static com.sprayme.teamrsm.analyticspraydown.data_access.BetaSpewDbHelper.USERS_TABLE_NAME;
import static com.sprayme.teamrsm.analyticspraydown.data_access.BetaSpewDbHelper.USER_ID;

/**
 * Created by Said on 10/8/2017.
 */

public class DbSprAyPI extends Application {
    /**
     * DbSprAyPI(userId)
     * GetUser
     * GetTicks(fromMoment, toMoment)
     * GetRoutes(List<int> routeIds)
     * GetPyramid(RouteType, TickType, fromMoment, toMoment)
     * GetPyramid(RouteType, TickType, fromMoment, toMoment, NumTiers, stepSize, stepType)
     * GetOnsightLevel(minOnsightPercentage)
     * GetMaxRedpoint()
     */

    private static DbSprAyPI _instance;
    private BetaSpewDbHelper _dbHelper = null;
    private int _userId;

    /*
    * Instantiate a class to gather information from the SQLite db within the scope
    * of a user. If no user exists in the db, we will create a new entry.
    * */
    public DbSprAyPI(int userId) {
        _instance = this;
        _dbHelper = new BetaSpewDbHelper(_instance);

        if(!doesUserExist(userId)) {
            createNewuser(userId);
        }
        _userId = userId;
    }

    /*
    * Queries the database and checks if the user exists
    * */
    private boolean doesUserExist(int userId) {
        /* Build the sql statement. */
        String sqlStmt = new StringBuilder()
                .append("SELECT ").append(USER_ID)
                .append(" FROM ").append(USERS_TABLE_NAME)
                .append(" WHERE ")
                .append(USER_ID).append(" = ").append(userId).toString();

        Cursor cursor = null;
        int userCount = -1;

        try {
            SQLiteDatabase sqlDb = _dbHelper.getReadableDatabase();

            cursor = sqlDb.rawQuery(sqlStmt, new String[]{USER_ID});
            userCount = cursor.getCount();
        } catch (SQLiteException sexc) {
            sexc.printStackTrace();
        }
        finally {
            cursor.close();
        }

        if (userCount > 0) return true;
        else return false;
    }

    private void createNewuser(int userId) {

    }

}
