package com.sprayme.teamrsm.analyticspraydown.models;

/**
 * Created by Said on 10/8/2017.
 */

public class User {

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
}
