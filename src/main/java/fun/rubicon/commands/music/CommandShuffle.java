package fun.rubicon.commands.music;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.core.music.GuildMusicPlayer;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import net.dv8tion.jda.core.entities.Message;

/**
 * @author Schlaubi / Michael Rittmeister
 */

public class CommandShuffle extends CommandHandler {
    public CommandShuffle() {
        super(new String[] {"shuffle"}, CommandCategory.MUSIC, new PermissionRequirements("shuffle", false, true), "Shuffle up the queue.", "");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        GuildMusicPlayer player = new GuildMusicPlayer(invocation, userPermissions);
        player.shuffleUp();
        return null;
    }
}
