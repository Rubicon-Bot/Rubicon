package fun.rubicon.commands.general;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.data.PermissionLevel;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import net.dv8tion.jda.core.entities.Message;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class CommandMusic extends CommandHandler {

    //TODO Parameter Usage
    public CommandMusic() {
        super(new String[]{"music", "m"}, CommandCategory.FUN, new PermissionRequirements(PermissionLevel.WITH_PERMISSION, "command.music"), "Chill with your friends and listen to music.", "");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        if(args.length == 0) {
            return createHelpMessage();
        } else if(args.length == 1) {
            switch (args[0]) {
                case "join":
                case "summon":
                case "start":
                    break;

                case "stop":
                case "leave":
                    break;
            }
        }
        return createHelpMessage();
    }
}
