package com.sprayme.teamrsm.analyticspraydown.data_access;

public class InvalidUserException extends Exception {
  public InvalidUserException() {
  }

  // Constructor that accepts a message
  public InvalidUserException(String message) {
    super(message);
  }
}
