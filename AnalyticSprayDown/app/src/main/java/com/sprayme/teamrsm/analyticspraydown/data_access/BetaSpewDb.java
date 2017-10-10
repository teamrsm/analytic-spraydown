package com.sprayme.teamrsm.analyticspraydown.data_access;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sprayme.teamrsm.analyticspraydown.models.User;

import static com.sprayme.teamrsm.analyticspraydown.data_access.SqlGen.API_KEY;
import static com.sprayme.teamrsm.analyticspraydown.data_access.SqlGen.EMAIL_ADDR;
import static com.sprayme.teamrsm.analyticspraydown.data_access.SqlGen.USER_ID;
import static com.sprayme.teamrsm.analyticspraydown.data_access.SqlGen.USER_NAME;

/**
 * Created by Said on 10/8/2017.
 */

public class BetaSpewDb extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
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
            db.setTransactionSuccessful();
        } catch (Exception e) {
            db.execSQL(SqlGen.makeRoutesTableDrop());
            db.execSQL(SqlGen.makeTicksTableDrop());
            db.execSQL(SqlGen.makeUsersTableDrop());
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // todo: make this smarter
        db.beginTransaction();
        try {
            db.execSQL(SqlGen.makeRoutesTableDrop());
            db.execSQL(SqlGen.makeTicksTableDrop());
            db.execSQL(SqlGen.makeUsersTableDrop());
            db.execSQL(SqlGen.makeUsersTableCreate());
            db.execSQL(SqlGen.makeTicksTableCreate());
            db.execSQL(SqlGen.makeRoutesTableCreate());
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

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
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lastUser;
    }
}
