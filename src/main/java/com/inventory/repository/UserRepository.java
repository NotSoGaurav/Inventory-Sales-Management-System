package com.inventory.repository;

import com.inventory.enums.UserRole;
import com.inventory.model.User;
import com.inventory.util.FileUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * File-based repository for persisting and retrieving User entities.
 */
public class UserRepository {

    private final String filePath;

    public UserRepository() {
        this("data/users.txt");
    }

    public UserRepository(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Saves a new user to file.
     *
     * @param user the user to save
     * @return true if saved successfully, false if user is null or ID already exists
     */
    public boolean save(User user) {
        if (user == null || user.getUserId() == null || user.getUserId().trim().isEmpty()) {
            return false;
        }

        if (existsById(user.getUserId())) {
            System.err.println("[UserRepository Warning] User with ID '" + user.getUserId() + "' already exists.");
            return false;
        }

        String serialized = serialize(user);
        return FileUtil.appendLine(filePath, serialized);
    }

    /**
     * Finds a user by their user ID.
     *
     * @param userId the user ID to look for
     * @return an Optional containing the found user, or empty if not found
     */
    public Optional<User> findById(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return Optional.empty();
        }

        return findAll().stream()
                .filter(u -> u.getUserId().equalsIgnoreCase(userId.trim()))
                .findFirst();
    }

    /**
     * Finds a user by their username (case-insensitive).
     *
     * @param username the username to look for
     * @return an Optional containing the found user, or empty if not found
     */
    public Optional<User> findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return Optional.empty();
        }

        return findAll().stream()
                .filter(u -> u.getUsername() != null && u.getUsername().equalsIgnoreCase(username.trim()))
                .findFirst();
    }

    /**
     * Retrieves all users from the file.
     *
     * @return list of all valid users
     */
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        List<String> lines = FileUtil.readAllLines(filePath);

        for (String line : lines) {
            User user = deserialize(line);
            if (user != null) {
                users.add(user);
            }
        }

        return users;
    }

    /**
     * Updates an existing user record.
     *
     * @param updatedUser the user with updated data
     * @return true if updated, false if user is null or not found
     */
    public boolean update(User updatedUser) {
        if (updatedUser == null || updatedUser.getUserId() == null) {
            return false;
        }

        List<User> users = findAll();
        boolean found = false;

        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUserId().equalsIgnoreCase(updatedUser.getUserId().trim())) {
                users.set(i, updatedUser);
                found = true;
                break;
            }
        }

        if (!found) {
            return false;
        }

        List<String> serializedLines = new ArrayList<>();
        for (User u : users) {
            serializedLines.add(serialize(u));
        }

        return FileUtil.writeAllLines(filePath, serializedLines);
    }

    /**
     * Deletes a user by their user ID.
     *
     * @param userId the user ID to delete
     * @return true if removed, false if not found
     */
    public boolean deleteById(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return false;
        }

        List<User> users = findAll();
        boolean removed = users.removeIf(u -> u.getUserId().equalsIgnoreCase(userId.trim()));

        if (!removed) {
            return false;
        }

        List<String> serializedLines = new ArrayList<>();
        for (User u : users) {
            serializedLines.add(serialize(u));
        }

        return FileUtil.writeAllLines(filePath, serializedLines);
    }

    /**
     * Checks if a user exists with the given ID.
     *
     * @param userId the ID to check
     * @return true if found, false otherwise
     */
    public boolean existsById(String userId) {
        return findById(userId).isPresent();
    }

    /**
     * Serializes a User object into a pipe-separated string line.
     * Format: userId|username|password|fullName|role
     */
    private String serialize(User user) {
        return user.getUserId() + FileUtil.FIELD_SEPARATOR
                + user.getUsername() + FileUtil.FIELD_SEPARATOR
                + user.getPassword() + FileUtil.FIELD_SEPARATOR
                + user.getFullName() + FileUtil.FIELD_SEPARATOR
                + (user.getRole() != null ? user.getRole().name() : UserRole.CASHIER.name());
    }

    /**
     * Deserializes a pipe-separated string line into a User object.
     * Handles malformed records gracefully by logging and returning null.
     */
    private User deserialize(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }

        try {
            String[] parts = line.split(FileUtil.FIELD_DELIMITER_REGEX, -1);
            if (parts.length < 5) {
                System.err.println("[UserRepository Warning] Malformed user line skipped (insufficient fields): " + line);
                return null;
            }

            String userId = parts[0].trim();
            String username = parts[1].trim();
            String password = parts[2].trim();
            String fullName = parts[3].trim();
            UserRole role;
            try {
                role = UserRole.valueOf(parts[4].trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                role = UserRole.CASHIER;
            }

            return new User(userId, username, password, fullName, role);
        } catch (Exception e) {
            System.err.println("[UserRepository Warning] Failed to parse user record: " + line + " - " + e.getMessage());
            return null;
        }
    }
}
