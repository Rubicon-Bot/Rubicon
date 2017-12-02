package fun.rubicon.commands.settings;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command2.CommandHandler;
import fun.rubicon.command2.CommandManager;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import net.dv8tion.jda.core.entities.Message;

public class CommandToggleWelcome extends CommandHandler{
    public CommandToggleWelcome(){
        super(new String[]{"welmsg","twel","weltoggle","togglewelcome"}, CommandCategory.SETTINGS,new PermissionRequirements(2,"command.weltoggle"),"Toggle the Private Welcome Messages of the Bot","welmsg");
    }
    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        return null;
    }
}
