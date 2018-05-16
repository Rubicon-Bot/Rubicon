package fun.rubicon.provider;

import fun.rubicon.RubiconBot;
import fun.rubicon.core.Cache;
import fun.rubicon.entities.User;
import fun.rubicon.io.Data;
import lombok.Getter;

/**
 * @author ForYaSee / Yannick Seeger
 */
public class UserProvider {

    @Getter
    private static final Cache<Long, User> cache = new Cache<>();

    public static User getUserById(long userId) {
        //TODO Replace this line
        net.dv8tion.jda.core.entities.User jdaUser = RubiconBot.getShardManager().getUserById(userId);
        if (jdaUser == null)
            return null;
        return cache.containsKey(userId) ? cache.get(userId) : Data.db().getUser(jdaUser);
    }
}
