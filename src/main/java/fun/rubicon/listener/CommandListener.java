package fun.rubicon.listener;

import fun.rubicon.command.CommandHandler;
import fun.rubicon.core.Main;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class CommandListener extends ListenerAdapter {

    public void onMessageReceived(MessageReceivedEvent e) {
        Guild g = e.getGuild();
        if (!Main.getMySQL().ifGuildExits(g)) {
            Main.getMySQL().createGuildServer(g);
            return;
        }
        if (!Main.getMySQL().ifUserExist(e.getAuthor())) {
            Main.getMySQL().createUser(e.getAuthor());
            return;
        }
        if (!Main.getMySQL().ifMemberExist(e.getMember())) {
            Main.getMySQL().createMember(e.getMember());
            return;
        }
        String prefix = Main.getMySQL().getGuildValue(g, "prefix");
        if (e.getMessage().getContent().startsWith(prefix) && e.getMessage().getAuthor().getId() != e.getJDA().getSelfUser().getId()) {
            try {
                CommandHandler.handleCommand(CommandHandler.parser.parse(e.getMessage().getContent(), e));
            } catch (Exception fuck) {
                fuck.printStackTrace();
            }
        }
    }
}

