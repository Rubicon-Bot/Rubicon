package fun.rubicon.commands.settings;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

/**
 * @author ForYaSee / Yannick Seeger
 */
public class CommandJoinImage extends CommandHandler {

    public CommandJoinImage() {
        super(new String[]{"joinimage", "jimage"}, CommandCategory.SETTINGS, new PermissionRequirements("joinimage", false, false), "Sends a nice image with the avatar and the name of a joined user.", "<disable>\n<enable> <#channel>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        if(invocation.getArgs().length == 0)
            return createHelpMessage();
        String command = invocation.getArgs()[0];
        if(command.equalsIgnoreCase("disable")) {

        } else if(command.equalsIgnoreCase("enable")) {
            if(invocation.getArgs().length != 2)
                return createHelpMessage();
            if(invocation.getMessage().getMentionedChannels().size() == 0)
                return createHelpMessage();
            TextChannel channel = invocation.getMessage().getMentionedChannels().get(0);

        }
        return createHelpMessage();
    }
}
