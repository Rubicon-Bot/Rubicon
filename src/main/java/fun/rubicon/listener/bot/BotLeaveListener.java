/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.listener.bot;

import fun.rubicon.core.entities.RubiconGuild;
import fun.rubicon.core.entities.RubiconMember;
import fun.rubicon.util.BotListHandler;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class BotLeaveListener extends ListenerAdapter {

    public void onGuildLeave(GuildLeaveEvent event) {
        RubiconGuild rubiconGuild = RubiconGuild.fromGuild(event.getGuild());
        //Database Deletes
        rubiconGuild.delete();
        rubiconGuild.deleteJoinMessage();
        rubiconGuild.deleteLeaveMessage();
        rubiconGuild.deleteMuteSettings();
        rubiconGuild.disableAutorole();

        for (long id : rubiconGuild.getAutochannels()) {
            rubiconGuild.deleteAutochannel(id);
        }
        for (Member member : event.getGuild().getMembers()) {
            RubiconMember.fromMember(member).delete();
        }
        BotListHandler.postStats(false);
    }
}
