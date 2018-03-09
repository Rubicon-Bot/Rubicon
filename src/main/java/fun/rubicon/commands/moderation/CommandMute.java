package fun.rubicon.commands.moderation;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.features.PunishmentHandler;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import net.dv8tion.jda.core.entities.Message;

public class CommandMute extends CommandHandler implements PunishmentHandler{
    public CommandMute() {
        super(new String[] {"mute", "tempmute"}, CommandCategory.MODERATION, new PermissionRequirements("mute", false, false), "Mute members temporary or permanent", "<@User> [time]");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {

        return null;
    }

    @Override
    public void loadPunishments() {

    }
}
