package fun.rubicon.commands.general;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.data.PermissionLevel;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import fun.rubicon.sql.UserSQL;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.entities.Message;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class CommandBio extends CommandHandler {

    public CommandBio() {
        super(new String[]{"bio"}, CommandCategory.GENERAL, new PermissionRequirements(PermissionLevel.EVERYONE, "command.bio"), "Set your bio that is displayed in the rc!profile command. ", "set <text>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        if (invocation.getArgs().length == 0) {
            return createHelpMessage();
        }
        if (invocation.getArgs()[0].equalsIgnoreCase("set")) {
            UserSQL sql = new UserSQL(invocation.getAuthor());
            String bioText = invocation.getMessage().getContentDisplay().replace(invocation.getPrefix() + invocation.getCommandInvocation() + " set", "");
            sql.set("bio", bioText);
            return EmbedUtil.message(EmbedUtil.success("Updated Bio!", "Successfully updated your bio."));
        }
        return createHelpMessage();
    }
}
