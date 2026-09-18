package com.inventory.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility class providing reusable, beginner-friendly file handling operations
 * for local text-based data persistence.
 */
public class FileUtil {

    // Common delimiters used across the application for data serialization
    public static final String FIELD_SEPARATOR = "|";
    public static final String FIELD_DELIMITER_REGEX = "\\|";
    public static final String ITEM_SEPARATOR = ";";
    public static final String ITEM_DELIMITER_REGEX = ";";
    public static final String SUB_ITEM_SEPARATOR = "~";
    public static final String SUB_ITEM_DELIMITER_REGEX = "~";

    /**
     * Ensures that the specified directory exists. Creates it if missing.
     *
     * @param dirPath the path to the directory
     * @return true if directory exists or was successfully created, false otherwise
     */
    public static boolean ensureDirectoryExists(String dirPath) {
        if (dirPath == null || dirPath.trim().isEmpty()) {
            return false;
        }
        try {
            Path path = Paths.get(dirPath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
            return true;
        } catch (IOException e) {
            System.err.println("[FileUtil Error] Failed to create directory: " + dirPath + " - " + e.getMessage());
            return false;
        }
    }

    /**
     * Ensures that the specified file exists. Creates parent directories and
     * the file itself if they do not exist.
     *
     * @param filePath the path to the file
     * @return true if the file exists or was successfully created, false otherwise
     */
    public static boolean ensureFileExists(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            return false;
        }
        try {
            Path path = Paths.get(filePath);
            Path parent = path.getParent();
            if (parent != null && !Files.exists(parent)) {
                Files.createDirectories(parent);
            }
            if (!Files.exists(path)) {
                Files.createFile(path);
            }
            return true;
        } catch (IOException e) {
            System.err.println("[FileUtil Error] Failed to create file: " + filePath + " - " + e.getMessage());
            return false;
        }
    }

    /**
     * Reads all non-empty lines from the specified file.
     * If the file does not exist, it safely returns an empty list without throwing an exception.
     *
     * @param filePath the path to the file
     * @return a list of lines read from the file, or an empty list if file is missing or empty
     */
    public static List<String> readAllLines(String filePath) {
        List<String> lines = new ArrayList<>();
        if (filePath == null || filePath.trim().isEmpty()) {
            return lines;
        }

        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            return lines;
        }

        try {
            List<String> rawLines = Files.readAllLines(path, StandardCharsets.UTF_8);
            for (String line : rawLines) {
                if (line != null && !line.trim().isEmpty()) {
                    lines.add(line.trim());
                }
            }
        } catch (IOException e) {
            System.err.println("[FileUtil Error] Failed to read from file: " + filePath + " - " + e.getMessage());
        }

        return lines;
    }

    /**
     * Appends a single line to the specified file.
     * Automatically creates parent directories and the file if they do not exist.
     *
     * @param filePath the path to the file
     * @param line     the string line to append
     * @return true if the append was successful, false otherwise
     */
    public static boolean appendLine(String filePath, String line) {
        if (filePath == null || line == null) {
            return false;
        }

        if (!ensureFileExists(filePath)) {
            return false;
        }

        try {
            Path path = Paths.get(filePath);
            Files.write(path, Collections.singletonList(line), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            return true;
        } catch (IOException e) {
            System.err.println("[FileUtil Error] Failed to append to file: " + filePath + " - " + e.getMessage());
            return false;
        }
    }

    /**
     * Overwrites the specified file with the given list of lines.
     * Automatically creates parent directories and the file if they do not exist.
     *
     * @param filePath the path to the file
     * @param lines    the list of lines to write
     * @return true if writing was successful, false otherwise
     */
    public static boolean writeAllLines(String filePath, List<String> lines) {
        if (filePath == null || lines == null) {
            return false;
        }

        if (!ensureFileExists(filePath)) {
            return false;
        }

        try {
            Path path = Paths.get(filePath);
            Files.write(path, lines, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
            return true;
        } catch (IOException e) {
            System.err.println("[FileUtil Error] Failed to write lines to file: " + filePath + " - " + e.getMessage());
            return false;
        }
    }

    /**
     * Checks if the specified file exists.
     *
     * @param filePath the path to the file
     * @return true if the file exists and is a regular file, false otherwise
     */
    public static boolean fileExists(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            return false;
        }
        Path path = Paths.get(filePath);
        return Files.exists(path) && Files.isRegularFile(path);
    }

    /**
     * Deletes the specified file if it exists.
     *
     * @param filePath the path to the file
     * @return true if the file was deleted or does not exist, false if deletion failed
     */
    public static boolean deleteFile(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            return false;
        }
        try {
            Path path = Paths.get(filePath);
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            System.err.println("[FileUtil Error] Failed to delete file: " + filePath + " - " + e.getMessage());
            return false;
        }
    }
}
