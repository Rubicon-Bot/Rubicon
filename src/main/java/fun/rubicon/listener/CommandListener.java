package fun.rubicon.listener;

import fun.rubicon.command.CommandHandler;
import fun.rubicon.core.Main;
import fun.rubicon.core.permission.PermissionManager;
import fun.rubicon.util.Info;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

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
            String prefix = Main.getMySQL().getGuildValue(g, "prefix");
            if (e.getMessage().getContent().toLowerCase().startsWith(prefix.toLowerCase()) && e.getMessage().getAuthor().getId() != e.getJDA().getSelfUser().getId()) {
                try {
                    CommandHandler.handleCommand(CommandHandler.parser.parse(e.getMessage().getContent(), e));
                } catch (Exception fuck) {
                    fuck.printStackTrace();
                }
            }
            if (e.getMessage().getContent().toLowerCase().startsWith(Info.BOT_DEFAULT_PREFIX.toLowerCase()) && !e.getMessage().getContent().toLowerCase().startsWith(prefix.toLowerCase().toString())){
                //Above for not dubble
            if (e.getMessage().getContent().toLowerCase().startsWith(Info.BOT_DEFAULT_PREFIX.toLowerCase()) && e.getMessage().getAuthor().getId() != e.getJDA().getSelfUser().getId()) {
                try {
                    CommandHandler.handleCommand(CommandHandler.parser.parsep(e.getMessage().getContent(), e));
                } catch (Exception fuck) {
                    fuck.printStackTrace();
                }}
            }
        } catch (NullPointerException ex) {
            //No Guild -> Private Message
        }
    }
}

