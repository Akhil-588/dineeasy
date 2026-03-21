package com.dineeasy.service;

import com.dineeasy.dao.UserDAO;
import com.dineeasy.dao.UserDAOImpl;
import com.dineeasy.model.User;

/** Service layer for user registration and authentication. */
public class UserService {

    private final UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAOImpl();
    }

    /**
     * Registers a new regular user after validating inputs.
     *
     * @return result message prefixed with "OK:" or "FAIL:"
     */
    public String register(String username, String password) {
        if (username == null || username.trim().isEmpty()) return "FAIL:Username cannot be empty.";
        if (password == null || password.trim().isEmpty()) return "FAIL:Password cannot be empty.";
        if (username.length() < 3) return "FAIL:Username must be at least 3 characters.";
        if (password.length() < 6) return "FAIL:Password must be at least 6 characters.";
        if (userDAO.usernameExists(username))
            return "FAIL:Username '" + username + "' is already taken. Choose another.";

        User newUser = new User(username.trim(), password.trim(), "USER");
        return userDAO.registerUser(newUser)
                ? "OK:Registration successful! Welcome, " + username + "!"
                : "FAIL:Registration failed. Please try again.";
    }

    /**
     * Authenticates a user with the provided credentials.
     *
     * @return authenticated {@link User}, or {@code null} on failure
     */
    public User login(String username, String password) {
        if (username == null || username.isEmpty() ||
            password == null || password.isEmpty()) {
            return null;
        }
        return userDAO.authenticate(username.trim(), password.trim());
    }
}
