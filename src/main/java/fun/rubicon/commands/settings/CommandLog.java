package fun.rubicon.commands.settings;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.core.entities.RubiconGuild;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.SafeMessage;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

/**
 * @author Schlaubi / Michael Rittmeister
 */

public class CommandLog extends CommandHandler {
    public CommandLog() {
        super(new String[] {"log"}, CommandCategory.SETTINGS, new PermissionRequirements("logs", false, false), "Easy logging system", "channel <#channel>\n member - Join/Leave log \nrole - Role assignments\n message - Message deletions\n voice - Voice log\n command - Command log\n punishment - Punishment log");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        String[] args = invocation.getArgs();
        Message message = invocation.getMessage();
        RubiconGuild guild = RubiconGuild.fromGuild(invocation.getGuild());
        if(args.length == 0)
            return createHelpMessage();
        switch (args[0]){
            case "channel":
                if(message.getMentionedChannels().isEmpty())
                    return message(error(invocation.translate("command.log.notc.title"), invocation.translate("command.log.notc.description")));
                TextChannel channel = message.getMentionedChannels().get(0);
                if(!invocation.getSelfMember().hasPermission(channel, Permission.MESSAGE_WRITE))
                    return message(error());
                guild.setLogChannel(channel);
                SafeMessage.sendMessage(invocation.getTextChannel(), message(success(invocation.translate("command.log.setchannel.title"), invocation.translate("command.log.setchannel.description"))));
                break;
            case "member":
                if(guild.isMemberLogEnabled()) {
                    guild.setMemberLog(false);
                    SafeMessage.sendMessage(invocation.getTextChannel(), message(success(invocation.translate("command.log.disabled.title"), String.format(invocation.translate("command.log.disabled.description"), invocation.translate("command.log.member")))));
                } else {
                    guild.setMemberLog(true);
                    SafeMessage.sendMessage(invocation.getTextChannel(), message(success(invocation.translate("command.log.enabled.title"), String.format(invocation.translate("command.log.enabled.description"), invocation.translate("command.log.member")))));
                }
                break;
            case "role":
                if(guild.isRoleLogEnabled()) {
                    guild.setRoleLog(false);
                    SafeMessage.sendMessage(invocation.getTextChannel(), message(success(invocation.translate("command.log.disabled.title"), String.format(invocation.translate("command.log.disabled.description"), invocation.translate("command.log.role")))));
                } else {
                    guild.setRoleLog(true);
                    SafeMessage.sendMessage(invocation.getTextChannel(), message(success(invocation.translate("command.log.enabled.title"), String.format(invocation.translate("command.log.enabled.description"), invocation.translate("command.log.role")))));
                }
                break;
            case "message":
                if(guild.isMessageLogEnabled()) {
                    guild.setMessageLog(false);
                    SafeMessage.sendMessage(invocation.getTextChannel(), message(success(invocation.translate("command.log.disabled.title"), String.format(invocation.translate("command.log.disabled.description"), invocation.translate("command.log.message")))));
                } else {
                    guild.setMessageLog(true);
                    SafeMessage.sendMessage(invocation.getTextChannel(), message(success(invocation.translate("command.log.enabled.title"), String.format(invocation.translate("command.log.enabled.description"), invocation.translate("command.log.message")))));
                }
                break;
            case "voice":
                if(guild.isVoiceLogEnabled()) {
                    guild.setVoiceLog(false);
                    SafeMessage.sendMessage(invocation.getTextChannel(), message(success(invocation.translate("command.log.disabled.title"), String.format(invocation.translate("command.log.disabled.description"), invocation.translate("command.log.voice")))));
                } else {
                    guild.setVoiceLog(true);
                    SafeMessage.sendMessage(invocation.getTextChannel(), message(success(invocation.translate("command.log.enabled.title"), String.format(invocation.translate("command.log.enabled.description"), invocation.translate("command.log.voice")))));
                }
                break;
            case "punishments":
            case "punishment":
                if(guild.isPunishmentLogEnabled()) {
                    guild.setPunishmentLog(false);
                    SafeMessage.sendMessage(invocation.getTextChannel(), message(success(invocation.translate("command.log.disabled.title"), String.format(invocation.translate("command.log.disabled.description"), invocation.translate("command.log.punishment")))));
                } else {
                    guild.setPunishmentLog(true);
                    SafeMessage.sendMessage(invocation.getTextChannel(), message(success(invocation.translate("command.log.enabled.title"), String.format(invocation.translate("command.log.enabled.description"), invocation.translate("command.log.punishment")))));
                }
                break;
            case "command":
            case "commands":
                if(guild.isCommandLogEnabled()) {
                    guild.setCommandLog(false);
                    SafeMessage.sendMessage(invocation.getTextChannel(), message(success(invocation.translate("command.log.disabled.title"), String.format(invocation.translate("command.log.disabled.description"), invocation.translate("command.log.command")))));
                } else {
                    guild.setCommandLog(true);
                    SafeMessage.sendMessage(invocation.getTextChannel(), message(success(invocation.translate("command.log.enabled.title"), String.format(invocation.translate("command.log.enabled.description"), invocation.translate("command.log.command")))));
                }
                break;
            default:
                SafeMessage.sendMessage(invocation.getTextChannel(), createHelpMessage());
                break;
        }
        return null;
    }
}
