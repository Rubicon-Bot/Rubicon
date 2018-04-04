package fun.rubicon.commands.botowner;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Message;

/**
 * @author ForYaSee / Yannick Seeger
 */
public class CommandBotstatus extends CommandHandler {

    public CommandBotstatus() {
        super(new String[]{"botstatus"}, CommandCategory.BOT_OWNER, new PermissionRequirements("botstatus", true, false), "Set the bots online status.", "<online/dnd/idle/offline>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        if (invocation.getArgs().length != 1)
            return createHelpMessage();
        switch (invocation.getArgs()[0]) {
            case "online":
                RubiconBot.getShardManager().setStatus(OnlineStatus.ONLINE);
                return EmbedUtil.message(EmbedUtil.success("Set bot status!", "Successfully set status to `online`."));
            case "dnd":
                RubiconBot.getShardManager().setStatus(OnlineStatus.DO_NOT_DISTURB);
                return EmbedUtil.message(EmbedUtil.success("Set bot status!", "Successfully set status to `dnd`"));
            case "idle":
                RubiconBot.getShardManager().setStatus(OnlineStatus.IDLE);
                return EmbedUtil.message(EmbedUtil.success("Set bot status!", "Successfully set status to `idle`"));
            case "offline":
                RubiconBot.getShardManager().setStatus(OnlineStatus.OFFLINE);
                return EmbedUtil.message(EmbedUtil.success("Set bot status!", "Successfully set status to `offline`"));
        }
        return null;
    }
}
