package fun.rubicon.commands.moderation;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;

/**
 * @author Michael Rittmeister / Schlaubi
 * @license GNU General Public License v3.0
 */
public class CommandUnban extends CommandHandler {
    public CommandUnban() {
        super(new String[]{"unban"}, CommandCategory.MODERATION, new PermissionRequirements("unban", false, false), "Unban members", "");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        return new MessageBuilder().setEmbed(EmbedUtil.info(invocation.translate("command.unban.info.title"), invocation.translate("command.unban.info.description")).build()).build();
    }
}
