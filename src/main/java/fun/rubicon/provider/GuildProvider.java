package fun.rubicon.provider;

import fun.rubicon.RubiconBot;
import fun.rubicon.entities.Guild;
import fun.rubicon.io.Data;
import fun.rubicon.util.PrimitiveLong;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ForYaSee / Yannick Seeger
 */
public class GuildProvider {

    @Getter
    private static final Map<PrimitiveLong, Guild> cache = new HashMap<>();

    public static Guild getGuildById(long guildId) {
        //TODO Replace this line
        assert RubiconBot.getShardManager() != null;
        net.dv8tion.jda.core.entities.Guild jdaGuild = RubiconBot.getShardManager().getGuildById(guildId);
        if (jdaGuild == null)
            return null;
        return cache.containsKey(new PrimitiveLong(guildId)) ? cache.get(new PrimitiveLong(guildId)) : Data.db().getGuild(jdaGuild);
    }

    public static List<Guild> getUsers() {
        return new ArrayList<>(cache.values());
    }

    public static void addGuild(Guild guild) {
        if (!cache.containsKey(new PrimitiveLong(guild.getIdLong())))
            cache.put(new PrimitiveLong(guild.getIdLong()), guild);
    }
}
