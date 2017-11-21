package fun.rubicon.listener;

import fun.rubicon.command.CommandHandler;
import fun.rubicon.core.Main;
import fun.rubicon.util.Info;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * Rubicon Discord bot
 *
 * @author Leon Kappes / Lee
 * @copyright Rubicon Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package listener
 */

public class CommandListener extends ListenerAdapter {

    public void onMessageReceived(MessageReceivedEvent e) {
        try {
            Guild g = e.getGuild();
            if (!Main.getMySQL().ifUserExist(e.getAuthor())) {
                Main.getMySQL().createUser(e.getAuthor());
                return;
            }
            if (!Main.getMySQL().ifGuildExits(g)) {
                Main.getMySQL().createGuildServer(g);
                return;
            }
            if (!Main.getMySQL().ifMemberExist(e.getMember())) {
                Main.getMySQL().createMember(e.getMember());
                return;
            }
            if (e.getMessage().getMentionedUsers().size() > 0) {
                for (User user : e.getMessage().getMentionedUsers()) {
                    Member member = g.getMember(user);
                    if (!Main.getMySQL().ifMemberExist(member)) {
                        Main.getMySQL().createMember(member);
                        return;
                    }
                    if (!Main.getMySQL().ifUserExist(user)) {
                        Main.getMySQL().createUser(user);
                        return;
                    }
                }
            }
            String prefix = Main.getMySQL().getGuildValue(g, "prefix");
            if (e.getMessage().getContent().toLowerCase().startsWith(prefix.toLowerCase()) && e.getMessage().getAuthor().getId() != e.getJDA().getSelfUser().getId()) {
                try {
                    CommandHandler.handleCommand(CommandHandler.parser.parse(e.getMessage().getContent(), e));
                } catch (Exception fuck) {
                    fuck.printStackTrace();
                }
            }
            if (e.getMessage().getContent().toLowerCase().startsWith(Info.BOT_DEFAULT_PREFIX.toLowerCase()) && !e.getMessage().getContent().toLowerCase().startsWith(prefix.toLowerCase())) {
                //Above for not dubble
                if (e.getMessage().getContent().toLowerCase().startsWith(Info.BOT_DEFAULT_PREFIX.toLowerCase()) && e.getMessage().getAuthor().getId() != e.getJDA().getSelfUser().getId()) {
                    try {
                        CommandHandler.handleCommand(CommandHandler.parser.parsep(e.getMessage().getContent(), e));
                    } catch (Exception fuck) {
                        fuck.printStackTrace();
                    }
                }
            }
        } catch (NullPointerException ex) {
            //No Guild -> Private Message
        }
    }
}

