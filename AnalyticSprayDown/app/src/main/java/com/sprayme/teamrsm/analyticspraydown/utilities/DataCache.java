package com.sprayme.teamrsm.analyticspraydown.utilities;

import android.app.Application;
import android.content.Context;

import com.sprayme.teamrsm.analyticspraydown.data_access.BetaSpewDb;
import com.sprayme.teamrsm.analyticspraydown.data_access.DbSprAyPI;
import com.sprayme.teamrsm.analyticspraydown.data_access.InvalidUserException;
import com.sprayme.teamrsm.analyticspraydown.models.User;

/**
 * Created by Said on 10/9/2017.
 * Singleton class to act as middle tier between data and views
 */

public class DataCache extends Application {

    private static DataCache instance = new DataCache();
    private BetaSpewDb db = null;

    private DataCache(){}

    public static synchronized DataCache getInstance(){
        if (instance == null)
            instance = new DataCache();

        return instance;
    }


    private User _currentUser;
    public User getCurrentUser() { return _currentUser; }
    public void setCurrentUser(User user) {
        _currentUser = user;
        //todo: update ticks
    }

    public User getLastUser() throws InvalidUserException {
        User lastUser = db.getLastUser();
        if (lastUser.getUserId() == null)
            throw new InvalidUserException("No Known last user");

        _currentUser = lastUser;
        return lastUser;
    }

    public void setDb(BetaSpewDb database) {
        db = database;
    }




}
