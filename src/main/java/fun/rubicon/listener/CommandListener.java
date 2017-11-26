package fun.rubicon.listener;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandParser;
import fun.rubicon.core.Main;
import fun.rubicon.util.Info;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * Rubicon Discord bot
 *
 * @author Yannick Seeger / ForYaSee
 * @copyright RubiconBot Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package listener
 */

public class CommandListener extends ListenerAdapter {

    public void onMessageReceived(MessageReceivedEvent e) {
        try {
            Guild g = e.getGuild();
            if (g == null) return;
            if (!RubiconBot.getMySQL().ifMemberExist(e.getMember())) {
                RubiconBot.getMySQL().createMember(e.getMember());
            }
            if (!RubiconBot.getMySQL().ifUserExist(e.getAuthor())) {
                RubiconBot.getMySQL().createUser(e.getAuthor());
            }
            if (!RubiconBot.getMySQL().ifGuildExits(g)) {
                RubiconBot.getMySQL().createGuildServer(g);
            }
            if (e.getMessage().getMentionedUsers().size() > 0) {
                for (User user : e.getMessage().getMentionedUsers()) {
                    Member member = g.getMember(user);
                    if (!RubiconBot.getMySQL().ifMemberExist(member)) {
                        RubiconBot.getMySQL().createMember(member);
                    }
                    if (!RubiconBot.getMySQL().ifUserExist(user)) {
                        RubiconBot.getMySQL().createUser(user);
                    }
                }
            }
            String prefix = RubiconBot.getMySQL().getGuildValue(g, "prefix");
            String messageContent = e.getMessage().getContent().toLowerCase();
            if (!e.getAuthor().getId().equals(e.getJDA().getSelfUser().getId())) {
                if (messageContent.startsWith(prefix.toLowerCase()) || messageContent.startsWith(Info.BOT_DEFAULT_PREFIX)) {
                    try {
                        CommandHandler.handleCommand(CommandParser.parse(e.getMessage().getContent(), e));
                    } catch (Exception ex) {
                        Logger.error(ex);
                    }
                }
            }
        } catch (NullPointerException ex) {
            Logger.error(ex);
        }
    }
}

