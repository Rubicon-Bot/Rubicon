package fun.rubicon.listener;

import fun.rubicon.RubiconBot;
import fun.rubicon.util.Colors;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;


/**
 * Rubicon Discord bot
 *
 * @author Leon Kappes / Lee
 * @copyright RubiconBot Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package listener
 */

public class SelfMentionListener extends ListenerAdapter{



    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if (e.getMessage().getMentionedUsers().contains(e.getJDA().getSelfUser())){
            e.getChannel().sendMessage(
                    new EmbedBuilder()
                    .setColor(Colors.COLOR_SECONDARY)
                    .setAuthor(e.getJDA().getSelfUser().getName(),null, e.getJDA().getSelfUser().getAvatarUrl())
                    .setDescription("Hey, I am Rubicon and here to help **you**!")
                    .addField("**Prefix**","`" + RubiconBot.getMySQL().getGuildValue(e.getGuild(), "prefix") + "`",false)
                    .addField("**Invite**", "[My Invite](https://discordapp.com/oauth2/authorize?client_id=380713705073147915&scope=bot&permissions=1898982486)", false)
                    .build()
            ).queue();
        }

    }

}
