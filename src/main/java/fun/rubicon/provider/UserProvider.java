package fun.rubicon.provider;

import fun.rubicon.RubiconBot;
import fun.rubicon.entities.User;
import fun.rubicon.io.Data;
import fun.rubicon.util.PrimitiveLong;
import lombok.Getter;

import java.util.*;

/**
 * @author ForYaSee / Yannick Seeger
 */
public class UserProvider {

    @Getter
    private static final Map<PrimitiveLong, User> cache = new HashMap<>();

    public static User getUserById(long userId) {
        //TODO Replace this line
        assert RubiconBot.getShardManager() != null;
        net.dv8tion.jda.core.entities.User jdaUser = RubiconBot.getShardManager().getUserById(userId);
        if (jdaUser == null)
            return null;
        return cache.containsKey(new PrimitiveLong(userId)) ? cache.get(new PrimitiveLong(userId)) : Data.db().getUser(jdaUser);
    }

    public static List<User> getUsers() {
        return new ArrayList<>(cache.values());
    }

    public static void addUser(User user) {
        if(!cache.containsKey(new PrimitiveLong(user.getIdLong())))
            cache.put(new PrimitiveLong(user.getIdLong()), user);
    }
}
