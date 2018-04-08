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
 * @author ForYaSee / Yannick Seeger
 */
public class CommandLeave extends CommandHandler {

    public CommandLeave() {
        super(new String[]{"leave"}, CommandCategory.MUSIC, new PermissionRequirements("leave", false, true), "Let the bot leaves your voice channel.", "");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        final GuildMusicPlayer musicPlayer =RubiconBot.getGuildMusicPlayerManager().getAndCreatePlayer(invocation, userPermissions);
        musicPlayer.leave(false);
        return null;
    }
}
