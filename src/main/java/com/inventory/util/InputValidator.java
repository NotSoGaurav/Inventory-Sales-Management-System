package com.inventory.util;

import com.inventory.exception.ValidationException;

import java.util.regex.Pattern;

/**
 * Utility class providing reusable input validation rules and parsing helpers.
 * Supports both boolean validation checks and exception-throwing validation methods.
 */
public class InputValidator {

    // Simple, reliable regular expressions for email and phone numbers
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9]{10,15}$");

    /**
     * Checks if a given text is null or empty after trimming.
     *
     * @param text the text to check
     * @return true if null or whitespace only, false otherwise
     */
    public static boolean isEmpty(String text) {
        return text == null || text.trim().isEmpty();
    }

    /**
     * Validates that a required string is non-null and not empty.
     * Also verifies that it does not contain illegal persistence delimiters.
     *
     * @param text      the input text
     * @param fieldName the user-friendly field name for error reporting
     * @return the trimmed text
     * @throws ValidationException if text is empty or contains delimiters
     */
    public static String validateRequiredText(String text, String fieldName) throws ValidationException {
        if (isEmpty(text)) {
            throw new ValidationException(fieldName + " cannot be empty.");
        }
        validateNoDelimiters(text, fieldName);
        return text.trim();
    }

    /**
     * Checks if the text contains any of the file storage delimiters (| ; ~).
     *
     * @param text the text to check
     * @return true if delimiters are found, false otherwise
     */
    public static boolean containsDelimiters(String text) {
        if (text == null) {
            return false;
        }
        return text.contains(FileUtil.FIELD_SEPARATOR) ||
                text.contains(FileUtil.ITEM_SEPARATOR) ||
                text.contains(FileUtil.SUB_ITEM_SEPARATOR);
    }

    /**
     * Validates that the input does not contain reserved file storage delimiters.
     *
     * @param text      the input text
     * @param fieldName the field name for error reporting
     * @throws ValidationException if the text contains reserved delimiter characters
     */
    public static void validateNoDelimiters(String text, String fieldName) throws ValidationException {
        if (containsDelimiters(text)) {
            throw new ValidationException(fieldName + " cannot contain reserved characters ('|', ';', or '~').");
        }
    }

    /**
     * Validates an identifier string.
     *
     * @param id        the ID string
     * @param fieldName the ID field name (e.g., "Product ID")
     * @return the trimmed ID string
     * @throws ValidationException if ID is empty or contains reserved characters
     */
    public static String validateId(String id, String fieldName) throws ValidationException {
        if (isEmpty(id)) {
            throw new ValidationException(fieldName + " is required.");
        }
        validateNoDelimiters(id, fieldName);
        return id.trim();
    }

    /**
     * Checks if a string represents a valid positive integer (> 0).
     *
     * @param input the string to test
     * @return true if valid integer > 0, false otherwise
     */
    public static boolean isPositiveInteger(String input) {
        if (isEmpty(input)) {
            return false;
        }
        try {
            int val = Integer.parseInt(input.trim());
            return val > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Parses and validates a positive integer (> 0).
     *
     * @param input     the string input
     * @param fieldName field name for error messages
     * @return the parsed positive integer
     * @throws ValidationException if input is not a number or not > 0
     */
    public static int parsePositiveInt(String input, String fieldName) throws ValidationException {
        if (isEmpty(input)) {
            throw new ValidationException(fieldName + " is required.");
        }
        try {
            int val = Integer.parseInt(input.trim());
            if (val <= 0) {
                throw new ValidationException(fieldName + " must be greater than 0.");
            }
            return val;
        } catch (NumberFormatException e) {
            throw new ValidationException(fieldName + " must be a valid integer number.");
        }
    }

    /**
     * Checks if a string represents a non-negative integer (>= 0).
     *
     * @param input the string to test
     * @return true if valid integer >= 0, false otherwise
     */
    public static boolean isNonNegativeInteger(String input) {
        if (isEmpty(input)) {
            return false;
        }
        try {
            int val = Integer.parseInt(input.trim());
            return val >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Parses and validates a non-negative integer (>= 0).
     *
     * @param input     the string input
     * @param fieldName field name for error messages
     * @return the parsed integer
     * @throws ValidationException if input is not a number or < 0
     */
    public static int parseNonNegativeInt(String input, String fieldName) throws ValidationException {
        if (isEmpty(input)) {
            throw new ValidationException(fieldName + " is required.");
        }
        try {
            int val = Integer.parseInt(input.trim());
            if (val < 0) {
                throw new ValidationException(fieldName + " cannot be negative.");
            }
            return val;
        } catch (NumberFormatException e) {
            throw new ValidationException(fieldName + " must be a valid integer number.");
        }
    }

    /**
     * Checks if a string represents a valid positive decimal number (> 0.0).
     *
     * @param input the string to test
     * @return true if valid double > 0.0, false otherwise
     */
    public static boolean isPositiveDouble(String input) {
        if (isEmpty(input)) {
            return false;
        }
        try {
            double val = Double.parseDouble(input.trim());
            return val > 0.0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Parses and validates a positive double value (> 0.0), such as a product price.
     *
     * @param input     the string input
     * @param fieldName field name for error messages
     * @return the parsed positive double
     * @throws ValidationException if input is not a valid number or not > 0.0
     */
    public static double parsePositiveDouble(String input, String fieldName) throws ValidationException {
        if (isEmpty(input)) {
            throw new ValidationException(fieldName + " is required.");
        }
        try {
            double val = Double.parseDouble(input.trim());
            if (val <= 0.0) {
                throw new ValidationException(fieldName + " must be greater than 0.");
            }
            return val;
        } catch (NumberFormatException e) {
            throw new ValidationException(fieldName + " must be a valid numeric value.");
        }
    }

    /**
     * Checks if a string matches basic email format.
     *
     * @param email the email string
     * @return true if valid email format, false otherwise
     */
    public static boolean isValidEmail(String email) {
        if (isEmpty(email)) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches() && !containsDelimiters(email);
    }

    /**
     * Validates an email address.
     *
     * @param email    the email string
     * @param required whether the email field is strictly required
     * @return the trimmed email, or empty string if optional and blank
     * @throws ValidationException if required and blank, or format is invalid
     */
    public static String validateEmail(String email, boolean required) throws ValidationException {
        if (isEmpty(email)) {
            if (required) {
                throw new ValidationException("Email address is required.");
            }
            return "";
        }
        if (!isValidEmail(email)) {
            throw new ValidationException("Invalid email format (e.g., user@example.com).");
        }
        return email.trim();
    }

    /**
     * Checks if a string matches a valid phone number (10 to 15 digits, optional leading +).
     *
     * @param phone the phone string
     * @return true if valid phone format, false otherwise
     */
    public static boolean isValidPhone(String phone) {
        if (isEmpty(phone)) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone.trim()).matches() && !containsDelimiters(phone);
    }

    /**
     * Validates a phone number.
     *
     * @param phone    the phone number string
     * @param required whether the phone field is strictly required
     * @return the trimmed phone number, or empty string if optional and blank
     * @throws ValidationException if required and blank, or format is invalid
     */
    public static String validatePhone(String phone, boolean required) throws ValidationException {
        if (isEmpty(phone)) {
            if (required) {
                throw new ValidationException("Phone number is required.");
            }
            return "";
        }
        if (!isValidPhone(phone)) {
            throw new ValidationException("Invalid phone number. Must contain 10-15 digits.");
        }
        return phone.trim();
    }
}
