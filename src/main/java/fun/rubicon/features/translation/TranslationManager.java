/*
 * Copyright (c) 2018 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.features.translation;

import fun.rubicon.RubiconBot;
import fun.rubicon.sql.MySQL;
import fun.rubicon.sql.UserSQL;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.entities.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Manages translation bundles.
 * @author tr808axm
 */
public class TranslationManager {
    private final List<TranslationLocale> translationLocaleList;
    private final TranslationLocale defaultTranslationLocale;

    public TranslationManager() {
        defaultTranslationLocale = new TranslationLocale(this, new Locale("en", "US"), "English (United States)") {
            @Override
            public String getTranslationOrDefault(String key) {
                if (getResourceBundle().containsKey(key))
                    return getResourceBundle().getString(key);
                else {
                    Logger.error("TranslationLocale for '" + key + "' missing in default locale " + getLocaleCode());
                    return "Missing translation.";
                }
            }
        };
        List<TranslationLocale> translationLocales = new ArrayList<>();
        translationLocales.add(defaultTranslationLocale);
        translationLocales.add(new TranslationLocale(this, new Locale("de", "DE"), "Deutsch (Deutschland)"));
        translationLocaleList = Collections.unmodifiableList(translationLocales);

        // ensure column existence
        try {
            MySQL.getConnection().prepareStatement("ALTER TABLE `users` ADD `language` CHAR(5);").execute();
        } catch (SQLException e) {
            if(e.getErrorCode() != 1060) { // duplicate column name may happen
                Logger.error("Could not create language column! Language settings won't load or save!");
                Logger.error(e);
            }
        }
        RubiconBot.getCommandManager().registerCommandHandler(new LanguageCommandHandler(this));
    }

    public TranslationLocale getDefaultTranslationLocale() {
        return defaultTranslationLocale;
    }

    public TranslationLocale getTranslationLocaleByLocale(Locale locale) {
        for(TranslationLocale translationLocale : translationLocaleList)
            if(translationLocale.getLocale().equals(locale))
                return translationLocale;
        return null;
    }

    public TranslationLocale getTranslationLocaleByLocaleOrDefault(Locale locale) {
        TranslationLocale found = getTranslationLocaleByLocale(locale);
        return found == null ? defaultTranslationLocale : found;
    }

    public TranslationLocale getUserLocale(User user) {
        String languageCode = UserSQL.fromUser(user).get("language");
        return languageCode == null
                ? getDefaultTranslationLocale()
                : getTranslationLocaleByLocaleOrDefault(Locale.forLanguageTag(languageCode));
    }

    public void setUserLocale(User user, TranslationLocale translationLocale) {
        UserSQL.fromUser(user).set("language", translationLocale == null ? "" : translationLocale.getLocaleCode());
    }

    public List<TranslationLocale> getLocales() {
        return translationLocaleList;
    }
}
