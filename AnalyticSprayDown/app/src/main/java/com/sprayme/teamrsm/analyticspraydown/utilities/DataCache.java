package com.sprayme.teamrsm.analyticspraydown.utilities;

import android.app.Application;

import com.sprayme.teamrsm.analyticspraydown.data_access.BetaSpewDb;
import com.sprayme.teamrsm.analyticspraydown.data_access.InvalidUserException;
import com.sprayme.teamrsm.analyticspraydown.models.Grade;
import com.sprayme.teamrsm.analyticspraydown.models.MPModel;
import com.sprayme.teamrsm.analyticspraydown.models.Pyramid;
import com.sprayme.teamrsm.analyticspraydown.models.PyramidStepType;
import com.sprayme.teamrsm.analyticspraydown.models.Route;
import com.sprayme.teamrsm.analyticspraydown.models.RouteType;
import com.sprayme.teamrsm.analyticspraydown.models.Tick;
import com.sprayme.teamrsm.analyticspraydown.models.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by Said on 10/9/2017.
 * Singleton class to act as middle tier between data and views
 */

public class DataCache extends Application
        implements MPModel.MPModelListener {
    public interface DataCacheTicksHandler {
        void onTicksCached(List<Tick> ticks);
    }
    public interface DataCacheRoutesHandler {
        void onRoutesCached(List<Route> routes);
    }
    public interface DataCacheUserHandler {
        void onUserCached(User user);
    }

    private HashMap<UUID,DataCacheUserHandler> userHandlers = new HashMap<>();
    private HashMap<UUID,DataCacheTicksHandler> ticksHandlers = new HashMap<>();
    private HashMap<UUID,DataCacheRoutesHandler> routeHandlers = new HashMap<>();

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
        m_MpModel.requestUser(emailAddress, apiKey);

        if (m_CurrentUser == null)
            m_CurrentUser = new User();
        else
            clearCurrentUser();

        m_CurrentUser.setEmailAddr(emailAddress);
        m_CurrentUser.setApiKey(apiKey);
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
        else if (isCacheInvalid())
            fetchTicks();
        else {

        }
    }

    private void fetchTicks() {
        if (isCacheInvalid()) {
            m_MpModel.requestTicks(m_CurrentUser.getUserId(), m_CurrentUser.getApiKey());
        }
        else {
            // todo: trigger the finished listener
            m_Ticks = m_Db.getTicks(m_CurrentUser.getUserId());
        }
    }


    /*
    * MPModel Subscription Methods
    * */
    @Override
    public void onRoutesLoaded() {

    }

    @Override
    public void onTicksLoaded() {
        /* persist to database, then retrieve latest set. */
        m_Db.upsertTicks(m_MpModel.getTicks(), m_CurrentUser.getUserId());
        m_Ticks = m_Db.getTicks(m_CurrentUser.getUserId());
    }

    @Override
    public void onUserLoaded() {
        User tmpUser = m_MpModel.getUser();
        m_CurrentUser.setUserName(tmpUser.getUserName());
        m_CurrentUser.setUserId(tmpUser.getUserId());

        m_Db.insertUser(m_CurrentUser);
    }

    @Override
    public void onFinished() {

    }

    /*
    * Publisher / Subscriber Methods
    * */
    public UUID subscribe(DataCacheUserHandler handler){
        UUID uuid = UUID.randomUUID();
        userHandlers.put(uuid, handler);
        return uuid;
    }

    public UUID subscribe(DataCacheTicksHandler handler){
        UUID uuid = UUID.randomUUID();
        ticksHandlers.put(uuid, handler);
        return uuid;
    }

    public UUID subscribe(DataCacheRoutesHandler handler) {
        UUID uuid = UUID.randomUUID();
        routeHandlers.put(uuid, handler);
        return uuid;
    }

    public boolean unsubscribeUserHandler(UUID uuid){
        return userHandlers.remove(uuid) != null;
    }

    public boolean unsubscribeTicksHandler(UUID uuid){
        return ticksHandlers.remove(uuid) != null;
    }

    public boolean unsubscribeRoutesHandler(UUID uuid){
        return routeHandlers.remove(uuid) != null;
    }

    private void broadcastTicksCompleted() {

    }

    /* temporary home for these sprayrific structures */
    public Pyramid buildPyramid(List<Route> routes, RouteType type, int height, int stepChangeSize, PyramidStepType stepModifier) {
        List<Route> filteredRoutes = routes.stream()
                .filter((route) -> Objects.nonNull(route))
                .filter((route) -> route.getType() == type)
                .collect(Collectors.toList());

        return new Pyramid(filteredRoutes, height, stepChangeSize, stepModifier);
    }

    public Pyramid buildPyramid(List<Route> routes, RouteType type, int height, int stepChangeSize, PyramidStepType stepModifier, Grade goal) {
        List<Route> filteredRoutes = routes.stream()
                .filter((route) -> route.getType() == type)
                .collect(Collectors.toList());

        return new Pyramid(filteredRoutes, height, stepChangeSize, stepModifier, goal);
    }
}
