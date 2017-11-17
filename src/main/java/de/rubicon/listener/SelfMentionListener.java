package de.rubicon.listener;

import de.rubicon.command.Command;
import de.rubicon.core.DiscordCore;
import de.rubicon.util.Colors;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class SelfMentionListener extends ListenerAdapter{



    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getMessage().getMentionedUsers().contains(DiscordCore.getJDA().getSelfUser())){
            event.getChannel().sendMessage(
                    new EmbedBuilder()
                    .setColor(Colors.COLOR_SECONDARY)
                    .setAuthor(DiscordCore.getJDA().getSelfUser().getName(),null, DiscordCore.getJDA().getSelfUser().getAvatarUrl())
                    .setDescription("Hey, i am Rubicon and I´m here to help **you**!")
                    .addField("**-Prefix**","-`rc!`",false)
                    .build()
            ).queue();
        }

    }

}
