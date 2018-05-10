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

public class CommandStop extends CommandHandler {
    public CommandStop() {
        super(new String[]{"stop"}, CommandCategory.MUSIC, new PermissionRequirements("stop", false, true), "Stop the current music", "");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) throws Exception {
        GuildMusicPlayer player = RubiconBot.getGuildMusicPlayerManager().getAndCreatePlayer(invocation, userPermissions);
        player.stopMusic();
        return null;
    }
}
