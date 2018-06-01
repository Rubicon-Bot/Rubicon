package fun.rubicon.provider;

import fun.rubicon.RubiconBot;
import fun.rubicon.entities.Member;
import fun.rubicon.io.Data;
import fun.rubicon.util.DoubleLong;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ForYaSee / Yannick Seeger
 */
public class MemberProvider {

    @Getter
    private static final Map<DoubleLong, Member> cache = new HashMap<>();

    public static Member getMemberByIds(long userId, long guildId) {
        //TODO Replace this line
        assert RubiconBot.getShardManager() != null;
        net.dv8tion.jda.core.entities.Member jdaMember = RubiconBot.getShardManager().getGuildById(guildId).getMember(RubiconBot.getShardManager().getUserById(userId));
        if (jdaMember == null)
            return null;
        return cache.containsKey(new DoubleLong(userId, guildId)) ? cache.get(new DoubleLong(userId, guildId)) : Data.db().getMember(jdaMember);
    }

    public static List<Member> getMembers() {
        return new ArrayList<>(cache.values());
    }

    public static void addMember(Member member) {
        if (!cache.containsKey(new DoubleLong(member.getUser().getIdLong(), member.getGuild().getIdLong())))
            cache.put(new DoubleLong(member.getUser().getIdLong(), member.getGuild().getIdLong()), member);
    }
}
