package org.intellij.idea.plugin.genprop.util;

import org.apache.commons.lang.StringUtils;

/**
 * String utility methods.
 *
 * @author Claus Ibsen
 */
public class StringUtil {

    /**
     * Private constructor, to prevent instances of this class, since it only has static members.
     */
    private StringUtil() {
    }

    /**
     * Is the string empty (null, or contains just whitespace)
     *
     * @param s string to test.
     * @return true if it's an empty string.
     */
    public static boolean isEmpty(String s) {
        if (s == null) {
            return true;
        }

        if (s.trim().length() == 0) {
            return true;
        }

        return false;
    }

    /**
     * Does the string contain some chars (whitespace is consideres as empty)
     *
     * @param s string to test.
     * @return true if it's NOT an empty string.
     */
    public static boolean isNotEmpty(String s) {
        return !isEmpty(s);
    }

    /**
     * Is the string in lowercase only?
     *
     * @param s string to test.
     * @return true if string is in lowercase only, false if not.
     */
    public static boolean isLowerCase(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isLowerCase(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Is the string in uppercase only?
     *
     * @param s string to test.
     * @return true if string is in uppercase only, false if not.
     */
    public static boolean isUpperCase(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isUpperCase(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Does the string have an uppercase character?
     *
     * @param s the string to test.
     * @return true if the string has an uppercase character, false if not.
     */
    public static boolean hasUpperCaseChar(String s) {
        char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (Character.isUpperCase(c)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Does the string have a lowercase character?
     *
     * @param s the string to test.
     * @return true if the string has a lowercase character, false if not.
     */
    public static boolean hasLowerCaseChar(String s) {
        char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (Character.isLowerCase(c)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the part of s after the token. <p/> <br/>Example:   after("helloWorldThisIsMe", "World") will return
     * "ThisIsMe". <br/>Example:   after("helloWorldThisIsMe", "Dog") will return null. <p/> If the token is not in the
     * string, null is returned.
     *
     * @param s     the string to test.
     * @param token the token.
     * @return the part of s that is after the token.
     * @since 2.15
     */
    public static String after(String s, String token) {
        int i = s.indexOf(token);
        if (i == -1) {
            return null;
        }

        return s.substring(i + token.length());
    }

    /**
     * Converts the first letter to lowercase <p/> <br/>Example: FirstName => firstName <br/>Example: name => name
     * <br/>Example: S => s
     *
     * @param s the string
     * @return the string with the first letter in lowercase.
     * @since 2.15
     */
    public static String firstLetterToLowerCase(String s) {
        if (s.length() > 1) {
            return Character.toLowerCase(s.charAt(0)) + s.substring(1);
        } else if (s.length() == 1) {
            return "" + Character.toLowerCase(s.charAt(0));
        } else {
            return s;
        }
    }

    public static String generatePropertyConstantName(String name) {
        StringBuilder buffer = new StringBuilder("PROPERTYNAME_".length() + name.length() + 5);
        buffer.append("PROPERTYNAME_");
        char[] chars = name.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (Character.isUpperCase(c)) {
                buffer.append('_');
                buffer.append(c);
            } else {
                buffer.append(Character.toUpperCase(c));
            }
        }
        return buffer.toString();
    }

    public static String generateSetterName(String name) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("set");
        buffer.append(StringUtils.capitalize(name));
        return buffer.toString();
    }

    public static String generateGetterNameGeneral(String name) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("get");
        buffer.append(StringUtils.capitalize(name));
        return buffer.toString();
    }

    public static String generateGetterNameBoolean(String name) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("is");
        buffer.append(StringUtils.capitalize(name));
        return buffer.toString();
    }
}
