package com.icesoft.faces.context;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class FailoverBundleResolver implements BundleResolver {
    private BundleResolver selectedBundleResolver;

    public FailoverBundleResolver(final String bundleName, final ResourceBundle defaultResourceBundle) {
        try {
            ResourceBundle.getBundle(bundleName);
            selectedBundleResolver = new BundleResolver() {
                public ResourceBundle bundleFor(Locale locale) {
                    return ResourceBundle.getBundle(bundleName, locale);
                }
            };
        } catch (MissingResourceException e) {
            selectedBundleResolver = new BundleResolver() {
                public ResourceBundle bundleFor(Locale locale) {
                    return defaultResourceBundle;
                }
            };
        }
    }

    public ResourceBundle bundleFor(Locale locale) {
        return selectedBundleResolver.bundleFor(locale);
    }
}
