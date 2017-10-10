package com.sprayme.teamrsm.analyticspraydown.data_access;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Said on 10/8/2017.
 */

public class BetaSpewDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "BetaSpew.db";

    /*-********************* TICKS TABLE SCHEMA ***************************/
    static final String TICKS_TABLE_NAME =  "TICKS";
    static final String USER_ID = "USER_ID";
    static final String ROUTE_ID = "ROUTE_ID";
    static final String TICK_DATE = "TICK_DATE";
    static final String NOTES = "NOTES";
    static final String TICK_TYPE = "TICK_TYPE";

    private static final String TICKS_TABLE_CREATE =
            new StringBuilder()
                .append("CREATE TABLE ")
                .append(TICKS_TABLE_NAME).append(" (")
                .append(USER_ID).append(" INT, ")
                .append(ROUTE_ID).append(" INT, ")
                .append(TICK_DATE).append(" TEXT, ")
                .append(NOTES).append(" TEXT, ")
                .append(TICK_TYPE).append(" TEXT")
                .append(")").toString();

    private static final String TICKS_TABLE_DROP =
            new StringBuilder()
                .append("DROP TABLE ").append(TICKS_TABLE_NAME).toString();
    /*-*********************************************************************/

    /*-********************* ROUTES TABLE SCHEMA ***************************/
    static final String ROUTES_TABLE_NAME = "ROUTES";
    static final String ROUTE_NAME = "ROUTE_NAME";
    static final String ROUTE_TYPE = "ROUTE_TYPE";
    static final String RATING = "RATING";
    static final String STARS = "STARS";
    static final String PITCHES = "PITCHES";
    static final String ROUTE_URL = "ROUTE_URL";

    // todo: stars is float. add unique constraint to users table
    private static final String ROUTES_TABLE_CREATE =
            new StringBuilder()
                .append("CREATE TABLE ")
                .append(ROUTES_TABLE_NAME).append(" (")
                .append(ROUTE_ID).append(" INT, ")
                .append(ROUTE_NAME).append(" TEXT, ")
                .append(ROUTE_TYPE).append(" TEXT, ")
                .append(RATING).append(" TEXT, ")
                .append(STARS).append(" REAL, ")
                .append(PITCHES).append(" INT, ")
                .append(ROUTE_URL).append(" TEXT ")
                .append(")").toString();

    private static final String ROUTES_TABLE_DROP =
            new StringBuilder()
                .append("DROP TABLE ").append(ROUTES_TABLE_NAME).toString();
    /*-********************************************************************/

    /*-********************* USERS TABLE SCHEMA ***************************/
    static final String USERS_TABLE_NAME = "USERS";
    static final String USER_NAME = "USER_NAME";
    static final String EMAIL_ADDR = "EMAIL_ADDRESS";
    static final String API_KEY = "DATA_KEY";

    private static final String USERS_TABLE_CREATE =
            new StringBuilder()
                .append("CREATE TABLE ")
                .append(USERS_TABLE_NAME).append(" (")
                .append(USER_ID).append(" INT, ")
                .append(USER_NAME).append(" TEXT, ")
                .append(EMAIL_ADDR).append(" TEXT, ")
                .append(API_KEY).append(" TEXT ")
                .append(")").toString();

    private static final String USERS_TABLE_DROP =
            new StringBuilder()
                .append("DROP TABLE ").append(USERS_TABLE_NAME).toString();
    /*-********************************************************************/

    /*
    * Constructor, find a reference to the database with DATABASE_NAME,
    * or creates one if SQLite db does not exist. If existing db does not
    * match the DATABSAE_VERSION, Upgrade logic is called.
    * */
    public BetaSpewDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /*
    *
    * */
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.beginTransaction();
            db.execSQL(USERS_TABLE_CREATE);
            db.execSQL(TICKS_TABLE_CREATE);
            db.execSQL(ROUTES_TABLE_CREATE);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            db.execSQL(ROUTES_TABLE_DROP);
            db.execSQL(TICKS_TABLE_DROP);
            db.execSQL(USERS_TABLE_DROP);
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
