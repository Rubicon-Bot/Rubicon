package fun.rubicon.util;

import net.dv8tion.jda.core.entities.User;

/**
 * Rubicon Discord bot
 *
 * @author Yannick Seeger / ForYaSee
 * @copyright RubiconBot Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.util
 */
public class GlobalBlacklist {

    public static boolean isOnBlacklist(User user) {
        Configuration configuration = new Configuration(FileUtil.createFileIfNotExist("data/global-blacklist.json"));
        if (configuration.has(user.getId())) {
            return true;
        }
        return false;
    }

    public static void addToBlacklist(User user) {
        Configuration configuration = new Configuration(FileUtil.createFileIfNotExist("data/global-blacklist.json"));
        configuration.set(user.getId(), "true");
    }

    public static void removeFromBlacklist(User user) {
        Configuration configuration = new Configuration(FileUtil.createFileIfNotExist("data/global-blacklist.json"));
        configuration.set(user.getId(), null);
    }
}
