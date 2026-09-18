package com.inventory.service;

import com.inventory.enums.UserRole;
import com.inventory.exception.ValidationException;
import com.inventory.model.User;
import com.inventory.repository.UserRepository;
import com.inventory.util.InputValidator;

import java.util.Optional;

/**
 * Service class handling authentication, credential verification,
 * and current session management.
 */
public class AuthenticationService {

    private final UserRepository userRepository;
    private User currentUser;

    public AuthenticationService() {
        this(new UserRepository());
    }

    public AuthenticationService(UserRepository userRepository) {
        this.userRepository = userRepository;
        ensureDefaultAdmin();
    }

    /**
     * Seeds a default administrator account if the users file is empty,
     * ensuring immediate out-of-the-box accessibility.
     */
    public final void ensureDefaultAdmin() {
        if (userRepository.findAll().isEmpty()) {
            User defaultAdmin = new User("U1", "admin", "admin123", "System Administrator", UserRole.ADMIN);
            userRepository.save(defaultAdmin);
        }
    }

    /**
     * Authenticates a user with username and password.
     *
     * @param username the username
     * @param password the password
     * @return the authenticated User object
     * @throws ValidationException if credentials are empty or invalid
     */
    public User login(String username, String password) throws ValidationException {
        if (InputValidator.isEmpty(username)) {
            throw new ValidationException("Username cannot be empty.");
        }
        if (InputValidator.isEmpty(password)) {
            throw new ValidationException("Password cannot be empty.");
        }

        Optional<User> optUser = userRepository.findByUsername(username.trim());
        if (!optUser.isPresent()) {
            com.inventory.util.LoggerUtil.logLoginAttempt(username, false);
            throw new ValidationException("Invalid username or password.");
        }

        User user = optUser.get();
        if (!user.getPassword().equals(password)) {
            com.inventory.util.LoggerUtil.logLoginAttempt(username, false);
            throw new ValidationException("Invalid username or password.");
        }

        this.currentUser = user;
        com.inventory.util.LoggerUtil.logLoginAttempt(username, true);
        return this.currentUser;
    }

    /**
     * Ends the current session and logs out the user.
     */
    public void logout() {
        if (this.currentUser != null) {
            com.inventory.util.LoggerUtil.info("[AUTH] User '" + this.currentUser.getUsername() + "' logged out.");
        }
        this.currentUser = null;
    }

    /**
     * Checks if a user is currently logged in.
     *
     * @return true if a session is active, false otherwise
     */
    public boolean isLoggedIn() {
        return this.currentUser != null;
    }

    /**
     * Retrieves the currently authenticated user in this session.
     *
     * @return the logged-in User, or null if no active session
     */
    public User getCurrentUser() {
        return this.currentUser;
    }

    /**
     * Checks if the currently authenticated user possesses the specified role.
     *
     * @param role the required role
     * @return true if current user matches role, false otherwise
     */
    public boolean hasRole(UserRole role) {
        return this.currentUser != null && this.currentUser.getRole() == role;
    }

    /**
     * Registers a new user account.
     *
     * @param user the new user
     * @return the registered user
     * @throws ValidationException if validation rules fail
     */
    public User registerUser(User user) throws ValidationException {
        if (user == null) {
            throw new ValidationException("User cannot be null.");
        }

        InputValidator.validateId(user.getUserId(), "User ID");
        InputValidator.validateRequiredText(user.getUsername(), "Username");
        InputValidator.validateRequiredText(user.getPassword(), "Password");
        InputValidator.validateRequiredText(user.getFullName(), "Full name");

        if (user.getRole() == null) {
            user.setRole(UserRole.CASHIER);
        }

        if (userRepository.existsById(user.getUserId())) {
            throw new ValidationException("User with ID '" + user.getUserId() + "' already exists.");
        }

        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new ValidationException("Username '" + user.getUsername() + "' is already taken.");
        }

        boolean saved = userRepository.save(user);
        if (!saved) {
            throw new ValidationException("Failed to save user record to storage.");
        }

        return user;
    }
}
