package fun.rubicon.core.translation;

import fun.rubicon.RubiconBot;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.entities.User;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author ForYaSee / Yannick Seeger
 */
public class TranslationUtil {

    public static String translate(User user, String key) {
        assert RubiconBot.sGetTranslations() != null;
        ResourceBundle defaultResourceBundle = RubiconBot.sGetTranslations().getDefaultTranslationLocale().getResourceBundle();
        String entry;
        try {
            entry = RubiconBot.sGetTranslations().getUserLocale(user).getResourceBundle().getString(key);
        } catch (NullPointerException | MissingResourceException e) {
            try {
                entry = defaultResourceBundle.getString(key);
            } catch (MissingResourceException e2) {
                entry = "Unable to find language string for \"" + key + "\"";
                Logger.error(e2);
            }
        }
        return entry;
    }
}
