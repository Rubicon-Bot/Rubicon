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
public class CommandJoin extends CommandHandler {

    public CommandJoin() {
        super(new String[]{"join", "summon"}, CommandCategory.MUSIC, new PermissionRequirements("command.join", false, true), "Lets the bot join in your voice channel.", "");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        MusicManager musicManager = new MusicManager(parsedCommandInvocation);
        return musicManager.joinInVoiceChannel();
    }
}
