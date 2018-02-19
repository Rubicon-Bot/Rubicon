/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.core.translation;

import fun.rubicon.RubiconBot;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.entities.User;

import java.util.*;

/**
 * Manages translation bundles.
 * @author tr808axm
 */
public class TranslationManager {
    private final List<TranslationLocale> translationLocaleList;
    private final TranslationLocale defaultTranslationLocale;

    //TODO replace with database source
    private final Map<User, TranslationLocale> userLocales = new HashMap<>();

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
        return userLocales.getOrDefault(user, defaultTranslationLocale);
    }

    public void setUserLocale(User user, TranslationLocale translationLocale) {
        if(translationLocale == null)
            userLocales.remove(user);
        else
            userLocales.put(user, translationLocale);
    }

    public List<TranslationLocale> getLocales() {
        return translationLocaleList;
    }
}
