package fun.rubicon.commands.music;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.core.music.GuildMusicPlayer;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.entities.Message;

import java.io.UnsupportedEncodingException;

/**
 * @author Schlaubi / Michael Rittmeister
 */

public class CommandVolume extends CommandHandler {
    public CommandVolume() {
        super(new String[]{"volume"}, CommandCategory.MUSIC, new PermissionRequirements("volume", false, true), "Set the volume, cause the default volume is to loud", "<volume>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) throws UnsupportedEncodingException {
        GuildMusicPlayer musicPlayer = RubiconBot.getGuildMusicPlayerManager().getAndCreatePlayer(invocation, userPermissions);
        if (!musicPlayer.checkVoiceAvailability())
            return null;
        if (invocation.getArgs().length == 0) {
            musicPlayer.setVolume(musicPlayer.DEFAULT_VOLUME);
            return EmbedUtil.message(EmbedUtil.success(invocation.translate("command.volume.reset.title"), invocation.translate("command.volume.reset.desc")));
        }
        int volume;
        try {
            volume = Integer.parseInt(invocation.getArgs()[0]);
        } catch (NumberFormatException e) {
            return EmbedUtil.message(EmbedUtil.error(invocation.translate("command.volume.invalidnumber.title"), invocation.translate("command.volume.invalidnumber.description")));
        }
        if (volume > 200 || volume < 0)
            return EmbedUtil.message(EmbedUtil.error(invocation.translate("command.volume.invalidnumber.title"), invocation.translate("command.volume.invalidnumber.description")));

        musicPlayer.setVolume(volume);
        return EmbedUtil.message(EmbedUtil.success(invocation.translate("command.volume.set.title"), String.format(invocation.translate("command.volume.set.description"), volume)));
    }
}
