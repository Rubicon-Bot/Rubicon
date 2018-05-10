/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.listener.bot;

import com.rethinkdb.net.Cursor;
import fun.rubicon.RubiconBot;
import fun.rubicon.core.entities.RubiconGuild;
import fun.rubicon.core.entities.RubiconMember;
import fun.rubicon.util.BotListHandler;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class BotJoinListener extends ListenerAdapter {

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        //Database Inserts
        RubiconGuild.fromGuild(event.getGuild());
        for (Member member : event.getGuild().getMembers()) {
            new RubiconMember(member);
        }
        BotListHandler.postStats(false);
        if (RubiconBot.getConfiguration().getInt("beta") == 1) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    Cursor cursor = RubiconBot.getRethink().db.table("guilds").filter(RubiconBot.getRethink().rethinkDB.hashMap("guildId", event.getGuild().getId())).run(RubiconBot.getRethink().getConnection());
                    List l = cursor.toList();
                    if (l.size() > 0) {
                        Map map = (Map) l.get(0);
                        if (map.get("beta") == null)
                            event.getGuild().leave().complete();
                    }
                }
            }, 30000);
            User owner = event.getGuild().getOwner().getUser();
            owner.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Hey " + owner.getAsMention() + " ,\nRubicon is currently in **BETA MODE**. If you have a beta key, you can redeem it with `rc!redeem <yourkey>`.").queue());

        }
    }
}
