package com.mirth.connect.client.ui.i18n;

import java.util.Locale;
import java.util.MissingResourceException;
import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * Minimal i18n helper for the Administrator client UI.
 *
 * Scope: only the keys we explicitly migrate. Everything else remains hard-coded.
 */
public final class I18n {

    private static final String BUNDLE_BASE = "com.mirth.connect.client.ui.i18n.messages";

    private I18n() {
    }

    public static String t(String key) {
        return t(key, null);
    }

    public static String t(String key, String fallback) {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_BASE, Locale.getDefault());
            if (bundle != null && bundle.containsKey(key)) {
                return bundle.getString(key);
            }
        } catch (MissingResourceException ignored) {
            // fall through to fallback
        }
        return fallback != null ? fallback : key;
    }

    public static String tf(String key, String fallback, Object... args) {
        String pattern = t(key, fallback);
        try {
            return MessageFormat.format(pattern, args);
        } catch (IllegalArgumentException ignored) {
            return pattern;
        }
    }
}

