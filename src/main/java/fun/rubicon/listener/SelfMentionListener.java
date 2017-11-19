package fun.rubicon.listener;

import fun.rubicon.core.DiscordCore;
import fun.rubicon.util.Colors;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;


/**
 * Rubicon Discord bot
 *
 * @author Leon Kappes / Lee
 * @copyright Rubicon Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package listener
 */

public class SelfMentionListener extends ListenerAdapter{



    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getMessage().getMentionedUsers().contains(DiscordCore.getJDA().getSelfUser())){
            event.getChannel().sendMessage(
                    new EmbedBuilder()
                    .setColor(Colors.COLOR_SECONDARY)
                    .setAuthor(DiscordCore.getJDA().getSelfUser().getName(),null, DiscordCore.getJDA().getSelfUser().getAvatarUrl())
                    .setDescription("Hey, I am Rubicon and here to help **you**!")
                    .addField("**-Prefix**","-`rc!`",false)
                    .addField("**-Invite**", "[Invite](https://discordapp.com/oauth2/authorize?client_id=380713705073147915&scope=bot&permissions=2146958591)", false)
                    .build()
            ).queue();
        }

    }

}
