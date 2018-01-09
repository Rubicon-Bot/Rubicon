package fun.rubicon.commands.music;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.core.music.MusicManager;
import fun.rubicon.data.PermissionLevel;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import net.dv8tion.jda.core.entities.Message;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class CommandPlay extends CommandHandler {

    public CommandPlay() {
        super(new String[]{"play"}, CommandCategory.MUSIC, new PermissionRequirements(PermissionLevel.EVERYONE, "command.play"), "Play music from YouTube or an link.", "<youtubeurl>\n" +
                "<keyword> (searches music on youtube)\n" +
                "<url>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        MusicManager musicManager = new MusicManager(parsedCommandInvocation);
        return musicManager.playMusic();
    }
}
