package fun.rubicon.commands.music;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.core.music.GuildMusicPlayer;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.entities.Message;

/**
 * @author ForYaSee / Yannick Seeger
 */
public class CommandPlay extends CommandHandler {

    public CommandPlay() {
        super(new String[]{"play", "p"}, CommandCategory.MUSIC, new PermissionRequirements("play", false, true), "Starts playing music.", "<link or keyword>");
    }


    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        GuildMusicPlayer musicPlayer = new GuildMusicPlayer(invocation, userPermissions);
        musicPlayer.playMusic(false);
        return null;
    }
}
