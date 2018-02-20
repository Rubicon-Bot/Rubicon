/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.core.translation;

import fun.rubicon.core.entities.RubiconUser;
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
        translationLocales.add(new TranslationLocale(this, new Locale("de", "SA"), "Sächsisch (Deutschland, Sachsen)"));
        translationLocales.add(new TranslationLocale(this, new Locale("de", "AT"), "Österreichisch (Österreich)"));
        translationLocales.add(new TranslationLocale(this, new Locale("es", "ES"), "Español (España)"));
        translationLocales.add(new TranslationLocale(this, new Locale("pt", "PT"), "Português (Portugal)"));
        translationLocales.add(new TranslationLocale(this, new Locale("pt", "BR"), "Português (Brasileiro)"));
        translationLocales.add(new TranslationLocale(this, new Locale("lo", "LO"), "Lolcat (lol)"));
        translationLocales.add(new TranslationLocale(this, new Locale("fi", "FI"), "Suomalainen (Suomi)"));
        translationLocales.add(new TranslationLocale(this, new Locale("fr", "FR"), "Français (France)"));
        translationLocales.add(new TranslationLocale(this, new Locale("it", "IT"), "Italiano (Italia)"));
        translationLocales.add(new TranslationLocale(this, new Locale("no", "NO"), "Norsk (Norge)"));
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
        String languageTag = RubiconUser.fromUser(user).getLanguage();
        Locale locale = Locale.forLanguageTag(languageTag);
        return getTranslationLocaleByLocale(locale);
    }

    public List<TranslationLocale> getLocales() {
        return translationLocaleList;
    }
}
