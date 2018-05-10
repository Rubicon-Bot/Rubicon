package fun.rubicon.commands.music;

import fun.rubicon.RubiconBot;
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

public class CommandClearQueue extends CommandHandler {
    public CommandClearQueue() {
        super(new String[]{"clearqueue"}, CommandCategory.MUSIC, new PermissionRequirements("clearqueue", false, false), "", "");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) throws Exception {
        GuildMusicPlayer musicPlayer = RubiconBot.getGuildMusicPlayerManager().getAndCreatePlayer(invocation, userPermissions);
        musicPlayer.clear();
        return null;
    }
}
