package com.sprayme.teamrsm.analyticspraydown.uicomponents.viewmodels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.sprayme.teamrsm.analyticspraydown.utilities.DataCache;

/**
 * Created by climbak on 11/17/17.
 */

public class ProgressViewModel extends ViewModel {
  private MutableLiveData<Boolean> mIsBusy = new MutableLiveData<>();

  public ProgressViewModel(){
    super();
    DataCache dc = DataCache.getInstance();
    mIsBusy.setValue(false);
    dc.getIsBusyLiveData().observeForever(isBusy -> {
      mIsBusy.setValue(isBusy);
    });
  }

  public MutableLiveData<Boolean> getIsBusy(){
    return mIsBusy;
  }
}
