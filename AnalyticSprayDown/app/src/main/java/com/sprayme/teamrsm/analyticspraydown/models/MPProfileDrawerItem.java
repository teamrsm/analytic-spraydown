package com.sprayme.teamrsm.analyticspraydown.models;

import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;

/**
 * Created by climbak on 10/25/17.
 */

public class MPProfileDrawerItem extends ProfileDrawerItem {

  private User user;

  public MPProfileDrawerItem (User user) {
    this.user = user;
    if (user.getAvatarUrl() != null)
      this.withIcon(user.getAvatarUrl());
  }

  public MPProfileDrawerItem withUser(User user){
    this.user = user;
    if (user.getAvatarUrl() != null)
      this.withIcon(user.getAvatarUrl());
    return this;
  }

  public User getUser(){ return this.user; }

  @Override
  public MPProfileDrawerItem withName(String name) {
    user.setUserName(name);
    return this;
  }

  @Override
  public MPProfileDrawerItem withEmail(String email) {
    user.setEmailAddr(email);
    return this;
  }

  @Override
  public StringHolder getName() {
    return new StringHolder(user.getUserName());
  }

  public StringHolder getEmail() {
    return new StringHolder(user.getEmailAddr());
  }
}
