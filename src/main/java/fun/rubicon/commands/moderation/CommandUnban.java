package fun.rubicon.commands.moderation;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;

public class CommandUnban extends CommandHandler{
    public CommandUnban() {
        super(new String[] {"unban"}, CommandCategory.MODERATION, new PermissionRequirements("unban", false, false), "Remove (temp)bans", "<@User>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation command, UserPermissions userPermissions) {
        return new MessageBuilder().setEmbed(EmbedUtil.info(command.translate("command.unban.info.title"), command.translate("command.unban.info.description")).build()).build();
    }

}
