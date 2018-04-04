package fun.rubicon.commands.music;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.core.entities.RubiconMember;
import fun.rubicon.core.music.GuildMusicPlayer;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.entities.Message;

/**
 * @author ForYaSee / Yannick Seeger
 */
public class CommandForcePlay extends CommandHandler {

    public CommandForcePlay() {
        super(new String[]{"forceplay"}, CommandCategory.MUSIC, new PermissionRequirements("forceplay", false, true), "Let the bot playing a song before continuing the queue.", "<search>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        RubiconMember member = RubiconMember.fromMember(invocation.getMember());
        if(!member.isPremium())
            return EmbedUtil.message(EmbedUtil.noPremium());
        new GuildMusicPlayer(invocation, userPermissions).forcePlay();
        return null;
    }
}
