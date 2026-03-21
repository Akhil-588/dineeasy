package com.dineeasy.dao;

import com.dineeasy.model.User;

/** DAO interface for user-related database operations. */
public interface UserDAO {

    /**
     * Persists a new user record.
     *
     * @return {@code true} if the insert succeeded
     */
    boolean registerUser(User user);

    /**
     * Finds a user by username.
     *
     * @return the matching {@link User}, or {@code null} if not found
     */
    User findByUsername(String username);

    /**
     * Validates credentials.
     *
     * @return the authenticated {@link User}, or {@code null} on failure
     */
    User authenticate(String username, String password);

    /**
     * Checks whether a username is already taken.
     *
     * @return {@code true} if already in use
     */
    boolean usernameExists(String username);
}
