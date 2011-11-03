package com.icesoft.faces.context;

import java.util.Locale;
import java.util.ResourceBundle;

interface BundleResolver {
    ResourceBundle bundleFor(Locale locale);
}
