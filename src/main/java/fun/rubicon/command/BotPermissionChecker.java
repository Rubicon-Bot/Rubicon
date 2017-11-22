package fun.rubicon.command;

import fun.rubicon.core.DiscordCore;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;


public class BotPermissionChecker {

    public static boolean hasAllPermissions(MessageReceivedEvent e) {
        Member bot = e.getGuild().getMember(DiscordCore.getJDA().getSelfUser());
        if (!bot.hasPermission(Permission.MANAGE_CHANNEL,
                Permission.MESSAGE_ADD_REACTION,
                Permission.MESSAGE_EMBED_LINKS,
                Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_MANAGE,
                Permission.MESSAGE_READ,
                Permission.MESSAGE_WRITE,
                Permission.VOICE_MOVE_OTHERS,
                Permission.VOICE_SPEAK)
                ) {
            e.getGuild().getOwner().getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("The bot needs following permissions:\n" +
                    "-MANAGE_CHANNEL\n" +
                    "-MESSAGE_ADD_REACTION\n" +
                    "-MESSAGE_EMBED_LINKS\n" +
                    "-MESSAGE_HISTORY\n" +
                    "-MESSAGE_MANAGE\n" +
                    "-MESSAGE_READ\n" +
                    "-MESSAGE_WRITE\n" +
                    "-MOVE_VOICE_MEMBERS\n" +
                    "-VOICE_SPEAK").queue());
            return false;
        }
        return true;
    }
}
