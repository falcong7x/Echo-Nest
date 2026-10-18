package com.econest.model.util;

import com.econest.model.exception.ValidationException;

import java.util.regex.Pattern;

/** Shared input validation helpers. Teammates can reuse these (e.g. for email tasks 8 and 9). */
public final class Validator {

    private static final Pattern EMAIL =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE = Pattern.compile("^\\+?[0-9]{9,15}$");

    private Validator() {
    }

    public static String requireText(String field, String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(field + " is required.");
        }
        return value.trim();
    }

    public static String requireEmail(String value) {
        String email = requireText("Email", value);
        if (!EMAIL.matcher(email).matches()) {
            throw new ValidationException("Email format is invalid (e.g. name@example.com).");
        }
        return email;
    }

    public static String requirePhone(String value) {
        String phone = requireText("Phone", value).replaceAll("[\\s-]", "");
        if (!PHONE.matcher(phone).matches()) {
            throw new ValidationException("Phone must contain 9 to 15 digits (optional leading +).");
        }
        return phone;
    }

    public static boolean isValidEmail(String value) {
        return value != null && EMAIL.matcher(value.trim()).matches();
    }
}
