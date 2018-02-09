/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.listener;

import fun.rubicon.RubiconBot;
import fun.rubicon.util.Colors;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.concurrent.TimeUnit;

/**
 * @author Leon Kappes / Lee
 */
public class SelfMentionListener extends ListenerAdapter {

    private final String[] RUBICON_EMOJIS = {"\uD83C\uDDF7", "\uD83C\uDDFA", "\uD83C\uDDE7", "\uD83C\uDDEE", "\uD83C\uDDE8", "\uD83C\uDDF4", "\uD83C\uDDF3"};

    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if (e.getMessage().getMentionedUsers().contains(e.getJDA().getSelfUser())) {
            if(!e.getMessage().getContentDisplay().replaceFirst("@", "").equals(e.getGuild().getSelfMember().getEffectiveName())) return;
            Message message = e.getChannel().sendMessage(
                    new EmbedBuilder()
                            .setColor(Colors.COLOR_SECONDARY)
                            .setAuthor(e.getJDA().getSelfUser().getName(), null, e.getJDA().getSelfUser().getAvatarUrl())
                            .setDescription("Hey, I am Rubicon and here to help **you**!")
                            .addField("**Prefix**", "`" + RubiconBot.getMySQL().getGuildValue(e.getGuild(), "prefix") + "`", false)
                            .addField("**Documentation**", "[rubicon.fun](https://rubicon.fun)", false)
                            .build()
            ).complete();
            //Warning: Useless code!!
            //Inspired by Lukass27s's (Lukass27s#6595) NerdBot
            for (String emoji : RUBICON_EMOJIS) {
                message.addReaction(emoji).queue();
            }
            if(!e.getGuild().getSelfMember().getPermissions(e.getChannel()).contains(Permission.MESSAGE_MANAGE)) return; // Do not try to delete message when bot is not allowed to
            message.delete().queueAfter(5, TimeUnit.MINUTES);
            e.getMessage().delete().queue();
        }
    }

}
