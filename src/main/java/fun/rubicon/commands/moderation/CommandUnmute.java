package fun.rubicon.commands.moderation;


import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.core.entities.RubiconGuild;
import fun.rubicon.core.entities.RubiconMember;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import fun.rubicon.util.SafeMessage;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.exceptions.InsufficientPermissionException;

public class CommandUnmute extends CommandHandler {

    public CommandUnmute() {

        super(new String[] {"unmute"}, CommandCategory.MODERATION, new PermissionRequirements("unmute", false, false), "Unmutes a muted member.", "<@User>");

    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation command, UserPermissions userPermissions) {
        String[] args = command.getArgs();
        Message message = command.getMessage();
        Member member = command.getMember();
        Guild guild = command.getGuild();
        if(args.length == 0)
            return createHelpMessage();
        if(message.getMentionedUsers().isEmpty())
            return new MessageBuilder().setEmbed(EmbedUtil.error(command.translate("command.mute.unknownuser.title"), command.translate("command.mute.unknownuser.description")).build()).build();
        Member victimMember = guild.getMember(message.getMentionedUsers().get(0));
        RubiconMember victim = RubiconMember.fromMember(victimMember);
        if(!victim.isMuted())
            return new MessageBuilder().setEmbed(EmbedUtil.error(command.translate("command.unmute.notmuted.title"), command.translate("command.unmute.notmuted.description")).build()).build();
        if(!member.canInteract(victimMember))
            return new MessageBuilder().setEmbed(EmbedUtil.error(command.translate("command.mute.nopermissions.user.title"), String.format(command.translate("command.mute.nopermissions.user.description"), victimMember.getAsMention())).build()).build();
        if(!command.getSelfMember().canInteract(victimMember))
            return new MessageBuilder().setEmbed(EmbedUtil.error(command.translate("command.mute.nopermissions.bot.title"), String.format(command.translate("command.mute.nopermissions.bot.description"), victimMember.getAsMention())).build()).build();
        victim.unmute();
        if(!deassignRole(victimMember))
            return new MessageBuilder().setEmbed(EmbedUtil.error(command.translate("command.mute.nopermissions.role.title"), command.translate("command.unmute.nopermissions.role.description")).build()).build();
        RubiconGuild rGuild = RubiconGuild.fromGuild(guild);
        if(rGuild.useMuteSettings())
            SafeMessage.sendMessage(rGuild.getMuteChannel(), rGuild.getUnmuteMessage().replace("%moderator%", member.getAsMention()).replace("%mention%", victim.getMember().getAsMention()));
        return new MessageBuilder().setEmbed(EmbedUtil.success(command.translate("command.unmute.unmuted.title"), String.format(command.translate("command.unmute.unmuted.description"), victimMember.getAsMention())).build()).build();
        }

        public static boolean deassignRole(Member member){
            Role role = CommandMute.createMutedRole(member.getGuild());
            if(role == null) return false;
            if(!member.getGuild().getSelfMember().canInteract(role)) return false;
            try{
                member.getGuild().getController().removeSingleRoleFromMember(member, role).queue();
                return true;
            } catch (InsufficientPermissionException e){
                return false;
            }
        }
}
