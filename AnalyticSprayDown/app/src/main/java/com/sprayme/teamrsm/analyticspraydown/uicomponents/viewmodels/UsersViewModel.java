package com.sprayme.teamrsm.analyticspraydown.uicomponents.viewmodels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.sprayme.teamrsm.analyticspraydown.models.MPProfileDrawerItem;
import com.sprayme.teamrsm.analyticspraydown.utilities.DataCache;

import java.util.List;

/**
 * Created by climbak on 11/16/17.
 */

public class UsersViewModel extends ViewModel {

  private MutableLiveData<List<MPProfileDrawerItem>> mUsers = new MutableLiveData<>();
  private MutableLiveData<MPProfileDrawerItem> mCurrentUser = new MutableLiveData<>();

  public UsersViewModel(){
    super();
    DataCache dc = DataCache.getInstance();
    dc.getUsersLiveData().observeForever(users -> {
      mUsers.setValue(users);
    });
    dc.getCurrentUserLiveData().observeForever(user -> {
      mCurrentUser.setValue(user);
    });
  }

  public MutableLiveData<List<MPProfileDrawerItem>> getUsers(){
    return mUsers;
  }

  public MutableLiveData<MPProfileDrawerItem> getCurrentUser(){
    return mCurrentUser;
  }
}
