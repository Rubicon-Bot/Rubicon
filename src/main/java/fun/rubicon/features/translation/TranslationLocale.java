/*
 * Copyright (c) 2018 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.features.translation;

import fun.rubicon.util.Logger;

import java.util.Locale;
import java.util.ResourceBundle;

public class TranslationLocale {
    private TranslationManager manager;

    private final Locale locale;
    private final ResourceBundle resourceBundle;
    private final String languageName;

    public TranslationLocale(TranslationManager manager, Locale locale, String languageName) {
        this.manager = manager;
        this.locale = locale;
        this.languageName = languageName;
        this.resourceBundle = ResourceBundle.getBundle("lang.translation_" + locale.getLanguage() + '_' + locale.getCountry(), locale);
    }

    public String getLocaleCode() {
        return locale.getLanguage() + '-' + locale.getCountry();
    }

    public Locale getLocale() {
        return locale;
    }

    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    public String getLanguageName() {
        return languageName;
    }

    public String getTranslationOrDefault(String key) {
        if (resourceBundle.containsKey(key))
            return resourceBundle.getString(key);
        else {
            Logger.warning("TranslationLocale for '" + key + "' missing in locale " + getLocaleCode());
            return manager.getDefaultTranslationLocale().getTranslationOrDefault(key);
        }
    }
}
