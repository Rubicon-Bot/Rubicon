package fun.rubicon.commands.general;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.SafeMessage;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;

/**
 * @author Leon Kappes / Lee
 * @copyright RubiconBot Dev Team 2018
 * @License GPL-3.0 License <http://rubicon.fun/license>
 */
public class CommandPermissionCheck extends CommandHandler {

    public CommandPermissionCheck() {
        super(new String[]{"permissioncheck", "permcheck"}, CommandCategory.GENERAL, new PermissionRequirements("permissioncheck", false, true), "Check which Permission Rubicon has/is missing", "");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        String header = buildPermssionMessage(invocation);

        if (invocation.getGuild().getMember(RubiconBot.getSelfUser()).hasPermission(invocation.getTextChannel(), Permission.MESSAGE_WRITE))
            SafeMessage.sendMessage(invocation.getTextChannel(), header);
        else
            try {
                invocation.getGuild().getOwner().getUser().openPrivateChannel().complete().sendMessage(header).queue();
            } catch (Exception ignored) {
            }

        return null;
    }

    public static String buildPermssionMessage(CommandManager.ParsedCommandInvocation invocation) {
        String header = "**Permissions**\n\n";
        if (invocation.getGuild().getMember(RubiconBot.getSelfUser()).hasPermission(Permission.MESSAGE_EMBED_LINKS))
            header += ":white_check_mark: Embed links\n";
        else
            header += ":warning: Embed links\n";
        if (invocation.getGuild().getMember(RubiconBot.getSelfUser()).hasPermission(Permission.MESSAGE_WRITE))
            header += ":white_check_mark: Send Messages\n";
        else
            header += ":warning: Send Messages\n";
        if (invocation.getGuild().getMember(RubiconBot.getSelfUser()).hasPermission(Permission.MANAGE_ROLES) && invocation.getGuild().getMember(RubiconBot.getSelfUser()).hasPermission(Permission.MANAGE_PERMISSIONS))
            header += ":white_check_mark: Manage Roles and Permissions\n";
        else
            header += ":warning: Manage Roles or Permissions\n";
        if (invocation.getGuild().getMember(RubiconBot.getSelfUser()).hasPermission(Permission.MESSAGE_MANAGE))
            header += ":white_check_mark: Manage Messages\n";
        else
            header += ":warning: Manage Messages\n";
        return header;

    }

}