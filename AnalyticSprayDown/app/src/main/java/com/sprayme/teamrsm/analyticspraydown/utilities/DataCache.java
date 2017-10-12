package com.sprayme.teamrsm.analyticspraydown.utilities;

import android.app.Application;

import com.sprayme.teamrsm.analyticspraydown.data_access.BetaSpewDb;
import com.sprayme.teamrsm.analyticspraydown.data_access.InvalidUserException;
import com.sprayme.teamrsm.analyticspraydown.models.MPModel;
import com.sprayme.teamrsm.analyticspraydown.models.Tick;
import com.sprayme.teamrsm.analyticspraydown.models.User;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Said on 10/9/2017.
 * Singleton class to act as middle tier between data and views
 */

public class DataCache extends Application
        implements MPModel.MPModelListener {

    private static final int invalidCacheHours = 24;

    /* member variables */
    private static DataCache instance = new DataCache();
    private BetaSpewDb m_Db = null;
    private MPModel m_MpModel = null;
    private User m_CurrentUser;
    private List<Tick> m_Ticks = null;

    private DataCache(){ m_MpModel = new MPModel(this); }

    public static synchronized DataCache getInstance(){
        if (instance == null)
            instance = new DataCache();

        return instance;
    }

    /* Database Methods */
    public void setDb(BetaSpewDb database) {
        m_Db = database;
    }

    /*
    * Cache Methods
    * */
    private boolean isCacheInvalid() {

        /* instantiating java.util.date defaults to current time. */

        Date lastAccessDate = m_Db.getLastAccessTime(m_CurrentUser.getUserId());

        Date cacheInvalidationDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(cacheInvalidationDate);
        cal.add(Calendar.HOUR_OF_DAY, -1 * invalidCacheHours);
        cacheInvalidationDate = cal.getTime();

        if (cacheInvalidationDate.getTime() <= lastAccessDate.getTime())
            return false;
        else
            return true;
    }

    /*
     * User Methods
     * */
    public User getCurrentUser() { return m_CurrentUser; }
    public void setCurrentUser(User user) {
        m_CurrentUser = user;
        //todo: update ticks
    }

    public User getLastUser() throws InvalidUserException {
        User lastUser = m_Db.getLastUser();
        if (lastUser.getUserId() == null)
            throw new InvalidUserException("No Known last user");

        m_CurrentUser = lastUser;
        return lastUser;
    }

    public void createNewUser(String emailAddress, String apiKey) {
        //m_MpModel.requestUser(emailAddress);

        if (m_CurrentUser == null)
            m_CurrentUser = new User();
        else
            clearCurrentUser();

        m_CurrentUser.setEmailAddr(emailAddress);
        m_CurrentUser.setApiKey(apiKey);
//
//        m_CurrentUser.setUserId((long)106308715);
//        m_CurrentUser.setUserName("Said Parirokh");
//        onUserLoaded();
    }

    private void clearCurrentUser() {
        m_CurrentUser.setEmailAddr("");
        m_CurrentUser.setApiKey("");
        m_CurrentUser.setUserName("");
        m_CurrentUser.setUserId(null);
    }

    /*
    * Ticks Methods
    * */
    public void getUserTicks() {
        if (m_Ticks == null)
            fetchTicks();


    }

    private void fetchTicks() {
        if (isCacheInvalid()) {
            m_MpModel.requestTicks(m_CurrentUser.getEmailAddr());
        }

        // get data from the db and then trigger the finished listener
    }


    /*
    * MPModel Subscription Methods
    * */
    @Override
    public void onRoutesLoaded() {

    }

    @Override
    public void onTicksLoaded() {
        // once ticks are loaded persist new ones to the db. db layer will take care of this.
    }

    @Override
    public void onUserLoaded() {
//        User tmpUser = m_MpModel.getUser();
//        m_CurrentUser.setUserName(tmpUser.getUserName());
//        m_CurrentUser.setUserId(tmpUser.getUserId());

        m_Db.insertUser(m_CurrentUser);
    }

    @Override
    public void onFinished() {

    }
}
