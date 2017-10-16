package com.sprayme.teamrsm.analyticspraydown.utilities;

import android.app.Application;

import com.sprayme.teamrsm.analyticspraydown.data_access.BetaSpewDb;
import com.sprayme.teamrsm.analyticspraydown.data_access.InvalidUserException;
import com.sprayme.teamrsm.analyticspraydown.models.MPModel;
import com.sprayme.teamrsm.analyticspraydown.models.Route;
import com.sprayme.teamrsm.analyticspraydown.models.Tick;
import com.sprayme.teamrsm.analyticspraydown.models.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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

    private ConcurrentHashMap<UUID,DataCacheUserHandler> userHandlers = new ConcurrentHashMap<>();
    private ConcurrentHashMap<UUID,DataCacheTicksHandler> ticksHandlers = new ConcurrentHashMap<>();
    private ConcurrentHashMap<UUID,DataCacheRoutesHandler> routeHandlers = new ConcurrentHashMap<>();

    private static final int invalidCacheHours = 24;
    private static final int mountainProjectRoutesRequestSizeLimit = 200;

    /* member variables */
    private static DataCache instance = new DataCache();
    private BetaSpewDb m_Db = null;
    private MPModel m_MpModel = null;
    private User m_CurrentUser;
    private List<Tick> m_Ticks = null;
    private List<Route> m_Routes = null;

    private boolean ticksWaitingOnRoutes = false;
    private boolean routesQueryIsBatched = false;
    private Long[] routesBatchedRunCache = null;

    /* Singleton Constructor */
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
    public List<Tick> getTicks() {
        return m_Ticks;
    }

    public void loadUserTicks() {
        if (m_Ticks == null)
            fetchTicks();
        else if (isCacheInvalid())
            fetchTicks();
        else
            broadcastTicksCompleted();
    }

    private void fetchTicks() {
        if (isCacheInvalid())
            m_MpModel.requestTicks(m_CurrentUser.getUserId(), m_CurrentUser.getApiKey());
        else {
            m_Ticks = m_Db.getTicks(m_CurrentUser.getUserId());

            if (m_Ticks.size() == 0)
                m_MpModel.requestTicks(m_CurrentUser.getUserId(), m_CurrentUser.getApiKey());
            else {
                Long[] routeIds = getRouteIdArray(m_Ticks);
                loadRoutes(routeIds);
                broadcastTicksCompleted();
            }
        }
    }

    /*
    * Routes Methods
    * */
    public void loadRoutes(Long[] routeIds) {
        if (m_Routes == null)
            fetchRoutes(routeIds);
        else if (isCacheInvalid())
            fetchRoutes(routeIds);
        else
            broadcastRoutesCompleted();
    }

    private void fetchRoutes(Long[] routeIds) {
        if (isCacheInvalid() || routesQueryIsBatched) {
            getMpRoutes(routeIds);
        }
        else {
            // todo: trigger the finished listener
            m_Routes = m_Db.getRoutes(routeIds);

            if (m_Routes.size() == 0) {
                getMpRoutes(routeIds);
            }
            else {
                broadcastRoutesCompleted();
                if (ticksWaitingOnRoutes)
                {
                    mapRoutes(m_Ticks, m_Routes);
                    ticksWaitingOnRoutes = false;
                    broadcastTicksCompleted();
                }
            }
        }
    }

    private void getMpRoutes(Long[] routeIds) {
        Long[] routes;
        if (routeIds.length > mountainProjectRoutesRequestSizeLimit)
        {
            routes = new Long[mountainProjectRoutesRequestSizeLimit];
            for (int i=0; i<mountainProjectRoutesRequestSizeLimit; i++) {
                routes[i] = routeIds[i];
            }

            Long[] routesToCache = new Long[routeIds.length - 200];
            for (int i=200; i< routeIds.length; i++){
                routesToCache[i-200] = routeIds[i];
            }
            routesQueryIsBatched = true;
            routesBatchedRunCache = routesToCache;
        }
        else
        {
            routes = routeIds;
            routesQueryIsBatched = false;
            routesBatchedRunCache = null;
        }
        // todo routeIDs need to be sent in batches of 200 at a time
        m_MpModel.requestRoutes(m_CurrentUser.getUserId(), m_CurrentUser.getApiKey(), routes);
    }

    /*
    * MPModel Subscription Methods
    * */
    @Override
    public void onRoutesLoaded(List<Route> routes) {
        m_Db.upsertRoutes(routes);
        if (routesQueryIsBatched){
            fetchRoutes(routesBatchedRunCache);
        }
        else {
            m_Routes = m_Db.getRoutes(null);
            broadcastRoutesCompleted();
            if (ticksWaitingOnRoutes)
            {
                mapRoutes(m_Ticks, m_Routes);
                ticksWaitingOnRoutes = false;
                broadcastTicksCompleted();
            }
        }
    }

    @Override
    public void onTicksLoaded(List<Tick> ticks) {
        /* persist to database, then retrieve latest set. */
        m_Db.upsertTicks(ticks, m_CurrentUser.getUserId());
        m_Db.updateAccessMoment(m_CurrentUser.getUserId());

        m_Ticks = m_Db.getTicks(m_CurrentUser.getUserId());
        ticksWaitingOnRoutes = true;
        Long[] routeIds = getRouteIdArray(m_Ticks);
        loadRoutes(routeIds);
        //broadcastTicksCompleted();
    }

    @Override
    public void onUserLoaded(User user) {
        m_CurrentUser.setUserName(user.getUserName());
        m_CurrentUser.setUserId(user.getUserId());

        m_Db.insertUser(m_CurrentUser);
        getTicks();

        broadcastUserCompleted();
    }

    private Long[] getRouteIdArray(List<Tick> ticks) {
        List<Long> routeIds = new ArrayList<>();
        for (Tick tick : ticks) {
            routeIds.add(tick.getRouteId());
        }
        return routeIds.toArray(new Long[routeIds.size()]);
    }

    private void mapRoutes(List<Tick> ticks, List<Route> routes) {
        HashMap<Long, Route> routeMap = new HashMap<>();
        for (Route route : routes) {
            routeMap.put(route.getId(), route);
        }

        for (Tick tick : ticks) {
            tick.setRoute(routeMap.get(tick.getRouteId()));
        }
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
        if (ticksHandlers != null && !ticksHandlers.isEmpty())
            ticksHandlers.forEach((k,v) -> v.onTicksCached((m_Ticks)));
    }

    private void broadcastRoutesCompleted() {
        routeHandlers.forEach((k,v) -> v.onRoutesCached((m_Routes)));
    }

    private void broadcastUserCompleted() {
        userHandlers.forEach((k,v) -> v.onUserCached((m_CurrentUser)));
    }
}
