package fun.rubicon.commands.music;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.EmbedUtil;
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
        if (!invocation.getMember().getVoiceState().inVoiceChannel())
            return EmbedUtil.message(EmbedUtil.error(invocation.translate("phrase.novc.title"), invocation.translate("phrase.novc.description")));
        if (!RubiconBot.getLavalinkManager().isConnected(invocation.getGuild().getId()))
            return joinChannel(invocation);

        PermissionRequirements moveIfInVoiceChannelPermissions = new PermissionRequirements("join.move", false, true);

        if (!moveIfInVoiceChannelPermissions.coveredBy(userPermissions)) {
            return EmbedUtil.message(EmbedUtil.no_permissions());
        }
        return joinChannel(invocation);
    }

    private Message joinChannel(CommandManager.ParsedCommandInvocation invocation) {
        RubiconBot.getLavalinkManager().createConnection(invocation.getMember().getVoiceState().getChannel());
        return EmbedUtil.message(EmbedUtil.success(invocation.translate("command.join.joined.title"), invocation.translate("command.join.joined.description").replace("%channel%", invocation.getMember().getVoiceState().getChannel().getName())));
    }
}
