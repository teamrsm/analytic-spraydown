package com.sprayme.teamrsm.analyticspraydown.utilities;

import android.app.Application;
import android.content.Context;

import com.sprayme.teamrsm.analyticspraydown.data_access.BetaSpewDb;
import com.sprayme.teamrsm.analyticspraydown.data_access.DbSprAyPI;
import com.sprayme.teamrsm.analyticspraydown.data_access.InvalidUserException;
import com.sprayme.teamrsm.analyticspraydown.models.MPModel;
import com.sprayme.teamrsm.analyticspraydown.models.User;

/**
 * Created by Said on 10/9/2017.
 * Singleton class to act as middle tier between data and views
 */

public class DataCache extends Application
        implements MPModel.MPModelListener {

    private static DataCache instance = new DataCache();
    private BetaSpewDb db = null;
    private MPModel mp = null;

    private DataCache(){}

    public static synchronized DataCache getInstance(){
        if (instance == null)
            instance = new DataCache();

        return instance;
    }

    public void setDb(BetaSpewDb database) {
        db = database;
    }

    // todo: we need a better way of teasing out the MPModel from Main Activity
    public void setMp(MPModel mountainProject) {
        mp = new MPModel(this);
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

    public void createNewUser(String emailAddress, String apiKey) {
        mp.requestUser(emailAddress);

        // todo: think about the case in which _current user already has some context
        if (_currentUser == null)
            _currentUser = new User();

        _currentUser.setEmailAddr(emailAddress);
        _currentUser.setApiKey(apiKey);
    }


    @Override
    public void onRoutesLoaded() {

    }

    @Override
    public void onTicksLoaded() {

    }

    @Override
    public void onUserLoaded() {
        User tmpUser = mp.getUser();
        _currentUser.setUserName(tmpUser.getUserName());
        _currentUser.setUserId(tmpUser.getUserId());

        db.insertUser(_currentUser);
    }

    @Override
    public void onFinished() {

    }
}
