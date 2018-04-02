/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.listener.bot;

import fun.rubicon.core.entities.RubiconGuild;
import fun.rubicon.util.Colors;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class SelfMentionListener extends ListenerAdapter {

    private final String[] RUBICON_EMOJIS = {"\uD83D\uDEE0","\uD83C\uDDF7", "\uD83C\uDDFA", "\uD83C\uDDE7", "\uD83C\uDDEE", "\uD83C\uDDE8", "\uD83C\uDDF4", "\uD83C\uDDF3"};


    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Message message = event.getMessage();
        if(!message.getMentionedUsers().isEmpty()){
            if(message.getMentionedUsers().contains(RubiconBot.getSelfUser()))
                if(message.getContentDisplay().replaceFirst("@", "").equals(event.getGuild().getSelfMember().getEffectiveName())){
                RubiconGuild guild = RubiconGuild.fromGuild(event.getGuild());
                    Message mymsg = event.getChannel().sendMessage(
                            new EmbedBuilder()
                                    .setColor(Colors.COLOR_SECONDARY)
                                    .setAuthor(event.getJDA().getSelfUser().getName(), null, event.getJDA().getSelfUser().getAvatarUrl())
                                    .setDescription("Hey, I am Rubicon and here to help **you**!\n**WARNING**: Canary build!! This build is not finished")
                                    .addField("**Prefix**", "`" + guild.getPrefix() + "`", false)
                                    .addField("**Documentation**", "[rubicon.fun](https://rubicon.fun)", false)
                                    .build()
                    ).complete();

                    //Warning: Useless code!!
                    //Inspired by Lukass27s's (Lukass27s#6595) NerdBot
                    for (String emoji : RUBICON_EMOJIS) {
                        mymsg.addReaction(emoji).queue();
                    }
                }
        }
    }
}
