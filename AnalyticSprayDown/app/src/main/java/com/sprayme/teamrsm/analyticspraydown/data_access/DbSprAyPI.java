package com.sprayme.teamrsm.analyticspraydown.data_access;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDoneException;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteStatement;

import com.sprayme.teamrsm.analyticspraydown.models.User;

import static com.sprayme.teamrsm.analyticspraydown.data_access.BetaSpewDbHelper.API_KEY;
import static com.sprayme.teamrsm.analyticspraydown.data_access.BetaSpewDbHelper.DATA_KEY;
import static com.sprayme.teamrsm.analyticspraydown.data_access.BetaSpewDbHelper.EMAIL_ADDR;
import static com.sprayme.teamrsm.analyticspraydown.data_access.BetaSpewDbHelper.USERS_TABLE_NAME;
import static com.sprayme.teamrsm.analyticspraydown.data_access.BetaSpewDbHelper.USER_ID;
import static com.sprayme.teamrsm.analyticspraydown.data_access.BetaSpewDbHelper.USER_NAME;

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
    private User _user;

    /*
    * Instantiate a class to gather information from the SQLite db within the scope
    * of a user. If no user exists in the db, we will create a new entry
    * */
    public DbSprAyPI(User user) {
        _instance = this;
        _dbHelper = new BetaSpewDbHelper(_instance);

        if(!doesUserExist(user.getUserId())) {
            createNewUser(user);
        }
        _user = user;
    }

    /*
    * Queries the database and checks if the user exists
    * */
    private boolean doesUserExist(long userId) {
        /* Build the sql statement. */
        String sql = new StringBuilder()
                .append("SELECT COUNT(*)")
                .append(" FROM ").append(USERS_TABLE_NAME)
                .append(" WHERE ")
                .append(USER_ID).append(" = ").append(userId).toString();

        long userCount = -1;
        SQLiteStatement stmt = null;

        try {
            SQLiteDatabase sqlDb = _dbHelper.getReadableDatabase();
            stmt = sqlDb.compileStatement(sql);

            userCount = stmt.simpleQueryForLong();
        } catch (SQLiteDoneException sdExc) {
            /* Exception occurs when no rows are returned. */
            return false;
        } catch (SQLiteException sExc) {
            sExc.printStackTrace();
        }

        if (userCount == 1)
            return true;
        else
            return false;
    }

    /* Creates a new user. */
    private void createNewUser(User user) {

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
}
