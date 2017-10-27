package com.sprayme.teamrsm.analyticspraydown.models;

/**
 * Created by Said on 10/8/2017.
 */

public class User {

  public User() {

  }

  public User(Long userId, String userName, String email, String apiKey, String avatarUrl) {
    this._userId = userId;
    this._userName = userName;
    this._emailAddr = email;
    this._apiKey = apiKey;
    this._avatarUrl = avatarUrl;
  }

  private Long _userId;

  public Long getUserId() {
    return _userId;
  }

  public void setUserId(Long userId) {
    _userId = userId;
  }

  private String _userName;

  public String getUserName() {
    return _userName;
  }

  public void setUserName(String userName) {
    _userName = userName;
  }

  private String _emailAddr;

  public String getEmailAddr() {
    return _emailAddr;
  }

  public void setEmailAddr(String emailAddr) {
    _emailAddr = emailAddr;
  }

  private String _apiKey;

  public String getApiKey() {
    return _apiKey;
  }

  public void setApiKey(String apiKey) {
    _apiKey = apiKey;
  }

  private String _avatarUrl;

  public String getAvatarUrl() {
    return _avatarUrl;
  }

  public void setAvatarUrl(String avatarUrl) {
    _avatarUrl = avatarUrl;
  }
}
