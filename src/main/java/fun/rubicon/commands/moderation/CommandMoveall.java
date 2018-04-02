package fun.rubicon.commands.moderation;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.GuildController;

import java.util.List;

/**
 * @author ForYaSee / Yannick Seeger
 */
public class CommandMoveall extends CommandHandler {

    public CommandMoveall() {
        super(new String[]{"moveall", "mvall"}, CommandCategory.MODERATION, new PermissionRequirements("moveall", false, false), "Move all members of your voice channel in another.", "<channelname>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        VoiceChannel memberChannel = invocation.getMember().getVoiceState().getChannel();
        if (invocation.getArgs().length == 0)
            return createHelpMessage();
        if (!invocation.getMember().getVoiceState().inVoiceChannel())
            return EmbedUtil.message(EmbedUtil.error(invocation.translate("command.moveall.error.novc.title"), invocation.translate("command.moveall.error.novc.description")));
        if (!invocation.getGuild().getSelfMember().hasPermission(Permission.VOICE_MOVE_OTHERS))
            return EmbedUtil.message(EmbedUtil.error(invocation.translate("command.moveall.error.noperms.title"), invocation.translate("command.moveall.error.noperms.description")));
        List<VoiceChannel> foundChannels = invocation.getGuild().getVoiceChannelsByName(invocation.getArgsString(), true);
        if (foundChannels.isEmpty())
            return EmbedUtil.message(EmbedUtil.error(invocation.translate("command.moveall.error.novcfound.title"), invocation.translate("command.moveall.error.novcfound.description")));
        VoiceChannel targetChannel = foundChannels.get(0);
        if (targetChannel.equals(memberChannel))
            return EmbedUtil.message(EmbedUtil.error(invocation.translate("command.moveall.error.same.title"), invocation.translate("command.moveall.error.same.description")));

        GuildController controller = invocation.getGuild().getController();
        invocation.getMember().getVoiceState().getChannel().getMembers().forEach(member -> {
            if (!invocation.getSelfMember().canInteract(member))
                return;
            controller.moveVoiceMember(member, targetChannel).queue();
        });
        return EmbedUtil.message(EmbedUtil.success(invocation.translate("command.moveall.title"), invocation.translate("command.moveall.description").replace("%old%", memberChannel.getName()).replace("%new%", targetChannel.getName())));
    }
}
