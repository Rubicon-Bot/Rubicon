package fun.rubicon.listener;

import fun.rubicon.RubiconBot;
import fun.rubicon.util.MySQL;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class UserJoinListener extends ListenerAdapter{
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {

        MySQL SQL = RubiconBot.getMySQL();
        Guild guild = event.getGuild();
        if(!SQL.ifGuildExits(guild))
            SQL.createGuildServer(guild);
        if (event.getMember().getUser().isBot()) return;
        if (event.getGuild().getId().equals("307084334198816769")) return;
        PrivateChannel pc = event.getMember().getUser().openPrivateChannel().complete();
            if (SQL.getGuildValue(event.getGuild(),"welmsg").equals("0"))
                return;
            pc.sendMessage(
                    "**Hey,** " + event.getMember().getAsMention() + " and welcome on " + event.getGuild().getName() + " :wave:\n\n" +
                            "Now, have a nice day and a lot of fun on the server! ;)"
            ).queue();
        }
    }
