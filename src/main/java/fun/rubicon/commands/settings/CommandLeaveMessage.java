/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.settings;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.core.entities.RubiconGuild;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class CommandLeaveMessage extends CommandHandler {

    public CommandLeaveMessage() {
        super(new String[]{"leavemessage", "leavemessages", "leavemsg"}, CommandCategory.SETTINGS, new PermissionRequirements("leavemessage", false, false), "Enables/Disables automated messages if a user leaves your server.",
                "set <#channel> <message>\n" +
                        "channel <#channel> \n" +
                        "message <message> \n" +
                        "disable");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        RubiconGuild rubiconGuild = RubiconGuild.fromGuild(invocation.getGuild());
        if (invocation.getArgs().length == 0)
            return createHelpMessage();
        if (invocation.getArgs().length == 1) {
            if (invocation.getArgs()[0].equalsIgnoreCase("disable")) {
                if (rubiconGuild.hasLeaveMessagesEnabled()) {
                    rubiconGuild.deleteLeaveMessage();
                    return EmbedUtil.message(EmbedUtil.success(invocation.translate("command.leavemessage.disabled.title"), invocation.translate("command.leavemessage.disabled.description")));
                } else {
                    return EmbedUtil.message(EmbedUtil.error(invocation.translate("command.leavemessage.ne.title"), invocation.translate("command.leavemessage.ne.description").replaceFirst("%command%", String.format("`%shelp leavemessage`", invocation.getPrefix()))));
                }
            }
        }
        if (invocation.getArgs()[0].equalsIgnoreCase("set")) {
            if (invocation.getMessage().getMentionedChannels().size() == 0)
                return EmbedUtil.message(EmbedUtil.error(invocation.translate("command.leavemessage.wrongArgument"), invocation.translate("command.leavemessage.error.channel")));
            TextChannel channel = invocation.getMessage().getMentionedChannels().get(0);
            String text = invocation.getArgsString().replaceFirst("set #" + channel.getName() + " ", "");
            rubiconGuild.setLeaveMessage(text, channel.getIdLong());
            return EmbedUtil.message(EmbedUtil.success(invocation.translate("command.leavemessage.enabled.title"), invocation.translate("command.leavemessage.enabled.description").replaceFirst("%channel%", channel.getAsMention()).replaceFirst("%message%", String.format("`%s`", text))));
        }
        if (invocation.getArgs()[0].equalsIgnoreCase("channel")) {
            boolean check = rubiconGuild.hasLeaveMessagesEnabled();
            if (!check)
                return EmbedUtil.message(EmbedUtil.error(invocation.translate("command.leavemessage.ne.title"), invocation.translate("command.leavemessage.ne.description").replaceFirst("%command%", String.format("`%shelp leavemessage`", invocation.getPrefix()))));
            if (invocation.getMessage().getMentionedChannels().size() != 1)
                return EmbedUtil.message(EmbedUtil.error(invocation.translate("command.leavemessage.wrongArgument"), invocation.translate("command.leavemessage.error.channel")));
            rubiconGuild.setLeaveMessage(invocation.getMessage().getMentionedChannels().get(0).getIdLong());
            return EmbedUtil.message(EmbedUtil.success(invocation.translate("command.leavemessage.channel.title"), invocation.translate("command.leavemessage.channel.description").replaceFirst("%channel%", invocation.getMessage().getMentionedChannels().get(0).getAsMention())));
        }
        if (invocation.getArgs()[0].equalsIgnoreCase("message")) {
            boolean check = rubiconGuild.hasLeaveMessagesEnabled();
            if (!check)
                return EmbedUtil.message(EmbedUtil.error(invocation.translate("command.leavemessage.ne.title"), invocation.translate("command.leavemessage.ne.description").replaceFirst("%command%", String.format("`%shelp leavemessage`", invocation.getPrefix()))));
            String msg = invocation.getArgsString().replace("message ", "");
            rubiconGuild.setLeaveMessage(msg);
            return EmbedUtil.message(EmbedUtil.success(invocation.translate("command.leavemessage.message.title"), invocation.translate("command.leavemessage.message.description").replaceFirst("%message%", String.format("`%s`", msg))));
        }
        return createHelpMessage();
    }

    public static class LeaveMessage {
        private final long channelId;
        private final String message;

        public LeaveMessage(long channelId, String message) {
            this.channelId = channelId;
            this.message = message;
        }

        public long getChannelId() {
            return channelId;
        }

        public String getMessage() {
            return message;
        }
    }
}
