package service;

import exceptions.UnauthorizedAccessException;
import exceptions.UserNotFoundException;
import models.User;
import java.util.Map;

public class AuthService {
    private Map<String, User> users;
    private User loggedInUser;

    public AuthService(Map<String, User> users) {
        this.users = users;
        this.loggedInUser = null;
    }

    public void login(String name) {
        if (loggedInUser != null) {
            throw new UnauthorizedAccessException("User " + loggedInUser.getName() + " is already logged in. Please logout first.");
        }
        if (!users.containsKey(name)) {
            throw new UserNotFoundException("User " + name + " not found.");
        }
        loggedInUser = users.get(name);
        System.out.println("[AUTH] " + name + " logged in.");
    }

    public void logout() {
        if (loggedInUser == null) {
            throw new UnauthorizedAccessException("No user is currently logged in.");
        }
        System.out.println("[AUTH] " + loggedInUser.getName() + " logged out.");
        loggedInUser = null;
    }

    public User getLoggedInUser() {
        if (loggedInUser == null) {
            throw new UnauthorizedAccessException("No user logged in.");
        }
        return loggedInUser;
    }
}