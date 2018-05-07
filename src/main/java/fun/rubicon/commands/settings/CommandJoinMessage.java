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
public class CommandJoinMessage extends CommandHandler {

    public CommandJoinMessage() {
        super(new String[]{"joinmessage", "joinmessages", "joinmsg"}, CommandCategory.SETTINGS, new PermissionRequirements("joinmessage", false, false), "Enables/Disables automated messages if a user joins your server.",
                "set <#channel> <message> | Enabled the joinmessages. / Use %user% for the user as mention, %server% for the servername and %count% for the new member count.\n" +
                        "channel <#channel> | Sets a new channel. / Only if joinmessages are enabled.\n" +
                        "message <message> | Use %user% for the user as mention, %guild% for the servername and %count% for the new member count. / Only if joinmessages are enabled.\n" +
                        "disable | Disables the joinmessages.");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        RubiconGuild rubiconGuild = RubiconGuild.fromGuild(invocation.getGuild());
        if (invocation.getArgs().length == 0)
            return createHelpMessage();
        if (invocation.getArgs().length == 1) {
            if (invocation.getArgs()[0].equalsIgnoreCase("disable")) {
                if (rubiconGuild.hasJoinMessagesEnabled()) {
                    rubiconGuild.deleteJoinMessage();
                    return EmbedUtil.message(EmbedUtil.success(invocation.translate("command.joinmessage.disabled.title"), invocation.translate("command.joinmessage.disabled.description")));
                } else {
                    return EmbedUtil.message(EmbedUtil.error(invocation.translate("command.joinmessage.ne.title"), invocation.translate("command.joinmessage.ne.description").replaceFirst("%command%", String.format("`%shelp joinmessage`", invocation.getPrefix()))));
                }
            }
        }
        if (invocation.getArgs()[0].equalsIgnoreCase("set")) {
            if (invocation.getMessage().getMentionedChannels().size() == 0)
                return EmbedUtil.message(EmbedUtil.error(invocation.translate("command.joinmessage.wrongArgument"), invocation.translate("command.joinmessage.error.channel")));
            TextChannel channel = invocation.getMessage().getMentionedChannels().get(0);
            String text = invocation.getArgsString().replaceFirst("set #" + channel.getName() + " ", "");
            rubiconGuild.setJoinMessage(text, channel.getId());
            return EmbedUtil.message(EmbedUtil.success(invocation.translate("command.joinmessage.enabled.title"), invocation.translate("command.joinmessage.enabled.description").replaceFirst("%channel%", channel.getAsMention()).replaceFirst("%message%", String.format("`%s`", text))));
        }
        if (invocation.getArgs()[0].equalsIgnoreCase("channel")) {
            boolean check = rubiconGuild.hasJoinMessagesEnabled();
            if (!check)
                return EmbedUtil.message(EmbedUtil.error(invocation.translate("command.joinmessage.ne.title"), invocation.translate("command.joinmessage.ne.description").replaceFirst("%command%", String.format("`%shelp joinmessage`", invocation.getPrefix()))));
            if (invocation.getMessage().getMentionedChannels().size() != 1)
                return EmbedUtil.message(EmbedUtil.error(invocation.translate("command.joinmessage.wrongArgument"), invocation.translate("command.joinmessage.error.channel")));
            rubiconGuild.setJoinMessageChannel(invocation.getMessage().getMentionedChannels().get(0).getId());
            return EmbedUtil.message(EmbedUtil.success(invocation.translate("command.joinmessage.channel.title"), invocation.translate("command.joinmessage.channel.description").replaceFirst("%channel%", invocation.getMessage().getMentionedChannels().get(0).getAsMention())));
        }
        if (invocation.getArgs()[0].equalsIgnoreCase("message")) {
            boolean check = rubiconGuild.hasJoinMessagesEnabled();
            if (!check)
                return EmbedUtil.message(EmbedUtil.error(invocation.translate("command.joinmessage.ne.title"), invocation.translate("command.joinmessage.ne.description").replaceFirst("%command%", String.format("`%shelp joinmessage`", invocation.getPrefix()))));
            String msg = invocation.getArgsString().replace("message ", "");
            rubiconGuild.setJoinMessage(msg);
            return EmbedUtil.message(EmbedUtil.success(invocation.translate("command.joinmessage.message.title"), invocation.translate("command.joinmessage.message.description").replaceFirst("%message%", String.format("`%s`", msg))));
        }
        return createHelpMessage();
    }

    public static class JoinMessage {
        private final String channelId;
        private final String message;

        public JoinMessage(String channelId, String message) {
            this.channelId = channelId;
            this.message = message;
        }

        public String getChannelId() {
            return channelId;
        }

        public String getMessage() {
            return message;
        }
    }
}
