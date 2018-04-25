/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.general;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import fun.rubicon.util.SafeMessage;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class CommandSay extends CommandHandler {

    public CommandSay() {
        super(new String[]{"say"}, CommandCategory.GENERAL, new PermissionRequirements("say", false, false), "Send a message in a channel as Rubicon", "<#channel> <message>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        if(invocation.getArgs().length < 2)
            return createHelpMessage();
        if(invocation.getMessage().getMentionedChannels().size() == 0)
            return createHelpMessage();

        TextChannel textChannel = invocation.getMessage().getMentionedChannels().get(0);
        if(!invocation.getSelfMember().hasPermission(textChannel, Permission.MESSAGE_WRITE))
            return EmbedUtil.message(EmbedUtil.error(invocation.translate("command.say.noperms.self.title") + "!", invocation.translate("command.say.noperms.self.description")));

        if(!invocation.getMember().hasPermission(textChannel, Permission.MESSAGE_WRITE))
            return EmbedUtil.message(EmbedUtil.error(invocation.translate("command.say.noperms.author.title") + "!", invocation.translate("command.say.noperms.author.description")));

        String sayText = invocation.getArgsString().replaceFirst("#" + textChannel.getName(), "");
        if (!invocation.getArgs()[0].contains("#")) {
            return EmbedUtil.message(EmbedUtil.error(invocation.translate("command.say.error.noChannel.title") + "!", invocation.translate("command.say.error.noChannel.description")));
        }

        if (invocation.getMessage().getMentionedUsers().size() >= 1) {
            for (User user : invocation.getMessage().getMentionedUsers()) {
                Member member = invocation.getGuild().getMember(user);
                sayText = sayText.replace("@" + member.getEffectiveName(), member.getAsMention());
            }
        }
        if (invocation.getMessage().getMentionedRoles().size() >= 1) {
            for (Role role : invocation.getMessage().getMentionedRoles()) {
                sayText = sayText.replace("@" + role.getName(), role.getAsMention());
            }
        }
        if (invocation.getMessage().getMentionedChannels().size() >= 2) {
            for (int i = 1; i < invocation.getMessage().getMentionedChannels().size(); i++) {
                TextChannel channel = invocation.getMessage().getMentionedChannels().get(i);
                sayText = sayText.replace("#" + channel.getName(), channel.getAsMention());
            }
        }
        SafeMessage.sendMessage(textChannel, sayText);
        return EmbedUtil.message(EmbedUtil.success(invocation.translate("command.say.success.sent.title") + "!", invocation.translate("command.say.success.sent.description").replace("%channel%", textChannel.getAsMention())));
    }
}
