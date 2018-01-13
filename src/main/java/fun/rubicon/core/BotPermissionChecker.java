package fun.rubicon.core;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;

/**
 * @author Yannick Seeger / ForYaSee
 */
public abstract class BotPermissionChecker {

    public boolean canRead(Guild guild, TextChannel textChannel) {
        if (guild.getSelfMember().hasPermission(textChannel, Permission.MESSAGE_READ))
            return true;
        return false;
    }

    public boolean canWrite(Guild guild, TextChannel textChannel) {
        if (!canRead(guild, textChannel))
            return false;
        if (guild.getSelfMember().hasPermission(textChannel, Permission.MESSAGE_WRITE))
            return true;
        return false;
    }

    public boolean canManageMessage(Guild guild, TextChannel textChannel) {
        if (guild.getSelfMember().hasPermission(textChannel, Permission.MESSAGE_MANAGE))
            return true;
        return false;
    }
}
