package com.sprayme.teamrsm.analyticspraydown.utilities;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.v4.util.ArraySet;

import com.sprayme.teamrsm.analyticspraydown.MainActivity;
import com.sprayme.teamrsm.analyticspraydown.SettingsActivity;
import com.sprayme.teamrsm.analyticspraydown.data_access.BetaSpewDb;
import com.sprayme.teamrsm.analyticspraydown.data_access.InvalidUserException;
import com.sprayme.teamrsm.analyticspraydown.models.Grade;
import com.sprayme.teamrsm.analyticspraydown.models.MPModel;
import com.sprayme.teamrsm.analyticspraydown.models.MPProfileDrawerItem;
import com.sprayme.teamrsm.analyticspraydown.models.Route;
import com.sprayme.teamrsm.analyticspraydown.models.Statistic;
import com.sprayme.teamrsm.analyticspraydown.models.Tick;
import com.sprayme.teamrsm.analyticspraydown.models.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

  public interface DataCacheProfileHandler {
    void onProfileCached(MPProfileDrawerItem profile);
  }

  private ConcurrentHashMap<UUID, DataCacheProfileHandler> profileHandlers = new ConcurrentHashMap<>();
  private ConcurrentHashMap<UUID, DataCacheTicksHandler> ticksHandlers = new ConcurrentHashMap<>();
  private ConcurrentHashMap<UUID, DataCacheRoutesHandler> routeHandlers = new ConcurrentHashMap<>();

  /* member variables */
  private static DataCache instance = new DataCache();
  private BetaSpewDb m_Db = null;
  private MPModel m_MpModel = null;
  private User m_CurrentUser;
  private boolean m_UserChanged;
  private List<User> m_Users = null;
  private List<MPProfileDrawerItem> m_UserProfiles = new ArrayList<>();
  private List<Tick> m_Ticks = null;
  private MutableLiveData<List<Tick>> mTicks = new MutableLiveData<>();
  private List<Route> m_Routes = null;
  private static int m_InvalidCacheHours;

  private boolean ticksWaitingOnRoutes = false;

  /* Singleton Constructor */
  private DataCache() {
    m_MpModel = new MPModel(this);
    m_InvalidCacheHours = Integer.valueOf(MainActivity.mSharedPref.getString(SettingsActivity.KEY_PREF_CACHE_TIMEOUT, "24"));
  }

  public static synchronized DataCache getInstance() {
    if (instance == null) {
      instance = new DataCache();
    }

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

    Date lastAccessDate = m_Db.getLastAccessTime(m_CurrentUser.getUserId());

    Calendar cal = Calendar.getInstance();
    cal.setTime(lastAccessDate);
    cal.add(Calendar.HOUR_OF_DAY, m_InvalidCacheHours);
    Date cacheInvalidationDate = cal.getTime();

    System.out.println(System.currentTimeMillis());
    System.out.println(cacheInvalidationDate.getTime());
    System.out.println(System.currentTimeMillis() > cacheInvalidationDate.getTime());

    return System.currentTimeMillis() > cacheInvalidationDate.getTime();
  }
  /*
   * User Methods
   * */
  public User getCurrentUser() {
    return m_CurrentUser;
  }

  public void setCurrentUser(User user) {
    m_CurrentUser = user;
    m_UserChanged = true;
    //todo: update ticks
  }

  public User getLastUser() throws InvalidUserException {
    User lastUser = m_Db.getLastUser();
    if (lastUser.getUserId() == null) {
      m_UserProfiles = new ArrayList<>();
      throw new InvalidUserException("No Known last user");
    }

    m_CurrentUser = lastUser;

    return lastUser;
  }

  public List<MPProfileDrawerItem> getUserProfiles() throws InvalidUserException {
    List<User> users = m_Db.getUsers();
    if (users == null || users.size() == 0) {
      m_UserProfiles = new ArrayList<>();
      throw new InvalidUserException("No saved users");
    }

    if (m_CurrentUser == null)
      getLastUser();

    m_Users = users;
    List<MPProfileDrawerItem> profiles = new ArrayList<>();
    MPProfileDrawerItem currentProfile = null;
    for (User user : users) {
      MPProfileDrawerItem profile = new MPProfileDrawerItem(user);
      profiles.add(profile);
      if ((long)user.getUserId() == (long)m_CurrentUser.getUserId())
        currentProfile = profile;
    }
    m_UserProfiles = profiles;
    m_CurrentUser = currentProfile.getUser();
//    broadcastUserCompleted(currentProfile);
    return m_UserProfiles;
  }

  public void createNewUser(String emailAddress, String apiKey) {
    m_MpModel.requestUser(emailAddress, apiKey);

//    if (m_CurrentUser == null)
      m_CurrentUser = null; //new User();
//    else
//      clearCurrentUser();

//    m_CurrentUser.setEmailAddr(emailAddress);
//    m_CurrentUser.setApiKey(apiKey);
    m_UserChanged = true;
  }

  /*
  * Ticks Methods
  * */
  public List<Tick> getTicks() {
    return m_Ticks;
  }

  public void loadUserTicks() {
    if (m_Ticks == null || m_UserChanged)
    {
      fetchTicks();
      m_UserChanged = false;
    }
    else if (isCacheInvalid())
      fetchTicks();
    else if (m_Routes == null) {
      Long[] routeIds = getRouteIdArray(m_Ticks);
      ticksWaitingOnRoutes = true;
      loadRoutes(routeIds);
    } else
      broadcastTicksCompleted();
  }

  private void fetchTicks() {
    if (isCacheInvalid()) {
      m_MpModel.requestTicks(m_CurrentUser.getUserId(), m_CurrentUser.getApiKey());
    } else {
      m_Ticks = m_Db.getTicks(m_CurrentUser.getUserId());
      mTicks.setValue(m_Ticks);

      if (m_Ticks.size() == 0) {
        m_MpModel.requestTicks(m_CurrentUser.getUserId(), m_CurrentUser.getApiKey());
      } else {
        Long[] routeIds = getRouteIdArray(m_Ticks);
        ticksWaitingOnRoutes = true;
        loadRoutes(routeIds);
      }
    }
  }

  public void importFromMountainProjectCsv(String csv) {
    List<Tick> ticks = SprayarificParser.parseTicksMountainProjectCsv(csv);

    if (ticks.size() > 0)
      onTicksLoaded(ticks);
  }

  /*
  * Routes Methods
  * */
  public void loadRoutes(Long[] routeIds) {
    if (m_Routes == null)
      fetchRoutes(routeIds);
    else if (isCacheInvalid())
      fetchRoutes(routeIds);
    else {
      HashSet<Long> routesHash = new HashSet<>();
      ArrayList<Long> missingRoutes = new ArrayList<>();
      for (Route route : m_Routes) {
        routesHash.add(route.getId());
      }
      for (Long id : routeIds) {
        if (!routesHash.contains(id))
          missingRoutes.add(id);
      }
      if (missingRoutes.size() > 0) {
        m_MpModel.requestRoutes(m_CurrentUser.getApiKey(),
                missingRoutes.toArray(new Long[missingRoutes.size()]));
      }
      else {
        broadcastRoutesCompleted();
        if (ticksWaitingOnRoutes) {
          mapRoutes(m_Ticks, m_Routes);
          ticksWaitingOnRoutes = false;
          mTicks.setValue(m_Ticks);
          broadcastTicksCompleted();
        }
      }
    }
  }

  private void fetchRoutes(Long[] routeIds) {
    if (isCacheInvalid()) {
      m_MpModel.requestRoutes(m_CurrentUser.getApiKey(), routeIds);
    } else {
      m_Routes = m_Db.getRoutes(routeIds);

      /* we should exercise this branch only in the case of new users with no
        * routes in the db who have not yet invalidated the cache. */
      if (m_Routes.size() == 0) {
        m_MpModel.requestRoutes(m_CurrentUser.getApiKey(), routeIds);
      } else {
        broadcastRoutesCompleted();
        if (ticksWaitingOnRoutes) {
          mapRoutes(m_Ticks, m_Routes);
          ticksWaitingOnRoutes = false;
          mTicks.setValue(m_Ticks);
          broadcastTicksCompleted();
        }
      }
    }
  }

  /*
  * Stats Methods
  * */

  /*
  * Returns the gradeId and percentage value of the grade with the maximum onsight percentage.
  * */
  public Map.Entry<Long, Float> calculateOnsightLevel(String routeType) {
    if (m_CurrentUser == null)
      return null;

    HashMap<Long, Float> osPercentages = m_Db.getOnsightPercentages(m_CurrentUser.getUserId(),
            routeType);

    /* ToDo: This is really stupid logic. */
    Map.Entry<Long, Float> maxEntry = null;
    for (Map.Entry<Long, Float> entry : osPercentages.entrySet()) {
      if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)
        maxEntry = entry;
    }

    return maxEntry;
  }

  /*
  * Returns all onsights
  * */
  public List<Route> getOnsights() {
    if (m_CurrentUser == null)
      return null;

    return m_Db.getOnsights(m_CurrentUser.getUserId());
  }

  public List<Statistic> getStats(){
    List<Statistic> stats = new ArrayList<>();



    return stats;
  }

  /*
  * MPModel Subscription Methods
  * */
  @Override
  public void onRoutesLoaded(List<Route> routes) {
    m_Db.upsertRoutes(routes);
    m_Routes = m_Db.getRoutes(null);
    broadcastRoutesCompleted();
    if (ticksWaitingOnRoutes) {
      mapRoutes(m_Ticks, m_Routes);
      ticksWaitingOnRoutes = false;
      mTicks.setValue(m_Ticks);
      broadcastTicksCompleted();
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
    if (user == null)
      return;

    m_CurrentUser = user;

    m_Db.insertUser(m_CurrentUser);

    MPProfileDrawerItem profile = new MPProfileDrawerItem(user);

    m_UserProfiles.add(profile);

    broadcastUserCompleted(profile);
  }

  /*
  * Helper methods
  * */
  private Long[] getRouteIdArray(List<Tick> ticks) {
    Set<Long> routeIds = new ArraySet<>();
    for (Tick tick : ticks) {
      Long id = tick.getRouteId();
      if (id != null)
        routeIds.add(id);
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
  public UUID subscribe(DataCacheProfileHandler handler) {
    UUID uuid = UUID.randomUUID();
    profileHandlers.put(uuid, handler);
    return uuid;
  }

  public UUID subscribe(DataCacheTicksHandler handler) {
    UUID uuid = UUID.randomUUID();
    ticksHandlers.put(uuid, handler);
    return uuid;
  }

  public UUID subscribe(DataCacheRoutesHandler handler) {
    UUID uuid = UUID.randomUUID();
    routeHandlers.put(uuid, handler);
    return uuid;
  }

  public boolean unsubscribeProfileHandler(UUID uuid) { return profileHandlers.remove(uuid) != null; }

  public boolean unsubscribeTicksHandler(UUID uuid) {
    return ticksHandlers.remove(uuid) != null;
  }

  public boolean unsubscribeRoutesHandler(UUID uuid) {
    return routeHandlers.remove(uuid) != null;
  }

  private void broadcastTicksCompleted() {
    if (ticksHandlers != null && !ticksHandlers.isEmpty())
    for(Map.Entry<UUID, DataCacheTicksHandler> entry : ticksHandlers.entrySet()) {
      entry.getValue().onTicksCached(m_Ticks);
    }
  }

  private void broadcastRoutesCompleted() {
    for(Map.Entry<UUID, DataCacheRoutesHandler> entry : routeHandlers.entrySet()) {
      entry.getValue().onRoutesCached(m_Routes);
    }
  }

  private void broadcastUserCompleted(MPProfileDrawerItem profile) {
    for(Map.Entry<UUID, DataCacheProfileHandler> entry : profileHandlers.entrySet()) {
      entry.getValue().onProfileCached(profile);
    }
  }

  public LiveData<List<Tick>> getTicksLiveData(){
    return mTicks;
  }
}
