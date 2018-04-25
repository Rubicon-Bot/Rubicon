package fun.rubicon.commands.general;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.entities.Message;

/**
 * @author ForYaSee / Yannick Seeger
 */
public class CommandPing extends CommandHandler {

    public CommandPing() {
        super(new String[]{"ping"}, CommandCategory.GENERAL, new PermissionRequirements("ping", false, true), "Shows the average ping to the discord API.", "");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        return EmbedUtil.message(EmbedUtil.info(invocation.translate("command.ping.title"), String.format(invocation.translate("command.ping.description"), (int) RubiconBot.getShardManager().getAveragePing())));
    }
}
