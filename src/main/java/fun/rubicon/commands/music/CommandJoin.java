package fun.rubicon.commands.music;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.core.music.GuildMusicPlayer;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import net.dv8tion.jda.core.entities.Message;

/**
 * @author ForYaSee / Yannick Seeger
 */
public class CommandJoin extends CommandHandler {

    public CommandJoin() {
        super(new String[]{"join", "summon"}, CommandCategory.MUSIC, new PermissionRequirements("join", false, true), "Let the bot join in your channel.", "");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        final GuildMusicPlayer musicPlayer = new GuildMusicPlayer(invocation, userPermissions);
        musicPlayer.join();
        return null;
    }
}
