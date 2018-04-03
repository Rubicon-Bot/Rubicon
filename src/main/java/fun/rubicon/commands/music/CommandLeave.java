package fun.rubicon.commands.music;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.entities.GuildVoiceState;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.VoiceChannel;

/**
 * @author ForYaSee / Yannick Seeger
 */
public class CommandLeave extends CommandHandler {

    public CommandLeave() {
        super(new String[]{"leave"}, CommandCategory.MUSIC, new PermissionRequirements("leave", false, true), "Let the bot leaves your voice channel.", "");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        GuildVoiceState voiceState = invocation.getMember().getVoiceState();
        VoiceChannel botChannel = RubiconBot.getLavalinkManager().getLink(invocation.getGuild().getId()).getChannel();
        if (botChannel == null)
            return EmbedUtil.message(EmbedUtil.error(invocation.translate("command.leave.novc.title"), invocation.translate("command.leave.novc.description")));

        if (!voiceState.inVoiceChannel() || voiceState.getChannel() != botChannel)
            return EmbedUtil.message(EmbedUtil.error(invocation.translate("command.leave.nosame.title"), invocation.translate("command.leave.nosame.title")));
        RubiconBot.getLavalinkManager().closeConnection(invocation.getGuild().getId());
        return EmbedUtil.message(EmbedUtil.error(invocation.translate("command.leave.left.title"), invocation.translate("command.leave.left.title")));
    }
}
