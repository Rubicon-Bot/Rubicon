package fun.rubicon.core;

import fun.rubicon.RubiconBot;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

/**
 * @author Yannick Seeger / ForYaSee
 */
public abstract class BotPermissionChecker {

    private User botUser = RubiconBot.getJDA().getSelfUser();

    public boolean canRead(Guild guild, TextChannel textChannel) {
        Member bot = guild.getMember(botUser);
        if (bot.getPermissions(textChannel).contains(Permission.MESSAGE_READ))
            return true;
        return false;
    }

    public boolean canWrite(Guild guild, TextChannel textChannel) {
        Member bot = guild.getMember(botUser);
        if (bot.getPermissions(textChannel).contains(Permission.MESSAGE_WRITE))
            return true;
        return false;
    }

    public boolean canManageMessage(Guild guild, TextChannel textChannel) {
        Member bot = guild.getMember(botUser);
        if(bot.getPermissions(textChannel).contains(Permission.MESSAGE_MANAGE))
            return true;
        return false;
    }
}
