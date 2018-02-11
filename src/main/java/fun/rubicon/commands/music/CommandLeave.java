package fun.rubicon.commands.music;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.core.music.MusicManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import net.dv8tion.jda.core.entities.Message;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class CommandLeave extends CommandHandler {

    public CommandLeave() {
        super(new String[]{"leave"}, CommandCategory.MUSIC, new PermissionRequirements("command.leave", false, true), "Stops playing music.", "");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        MusicManager musicManager = new MusicManager(parsedCommandInvocation);
        return musicManager.leaveVoiceChannel();
    }
}
