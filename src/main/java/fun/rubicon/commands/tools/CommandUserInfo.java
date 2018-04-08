/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.tools;


import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.Colors;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

import java.time.OffsetDateTime;

public class CommandUserInfo extends CommandHandler {

    public CommandUserInfo() {
        super(new String[]{"userinfo", "whois"}, CommandCategory.TOOLS, new PermissionRequirements("command.userinfo", false, true), "Returns some information about the specified user", "[@User]");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {

        Member member = parsedCommandInvocation.getMessage().getMentionedMembers().isEmpty() ? parsedCommandInvocation.getMessage().getMember() : parsedCommandInvocation.getMessage().getMentionedMembers().get(0);

        StringBuilder rawRoles = new StringBuilder();
        member.getRoles().forEach(r -> rawRoles.append(r.getName()).append(", "));
        StringBuilder roles = new StringBuilder(rawRoles.toString());
        if (!member.getRoles().isEmpty())
            roles.replace(rawRoles.lastIndexOf(","), roles.lastIndexOf(",") + 1, "");
        EmbedBuilder userinfo = new EmbedBuilder();
        userinfo.setColor(Colors.COLOR_PRIMARY);
        userinfo.setTitle("User information of " + member.getUser().getName());
        userinfo.setThumbnail(member.getUser().getAvatarUrl());
        userinfo.addField("Nickname", member.getEffectiveName(), false);
        userinfo.addField("User id", member.getUser().getId(), false);
        userinfo.addField("Status", member.getOnlineStatus().toString().replace("_", ""), false);
        if (member.getGame() != null)
            userinfo.addField("Game", member.getGame().getName(), false);
        userinfo.addField("Guild join date", formatDate(member.getJoinDate()), false);
        userinfo.addField("Roles", "`" + roles.toString() + "`", false);
        userinfo.addField("Discord join date", formatDate(member.getUser().getCreationTime()), false);
        userinfo.addField("Avatar url", (member.getUser().getAvatarUrl() != null) ? member.getUser().getAvatarUrl() : "https://rubicon.fun", true);
        return new MessageBuilder().setEmbed(userinfo.build()).build();
    }

    private String formatDate(OffsetDateTime date) {
        return date.getMonthValue() + "/" + date.getDayOfMonth() + "/" + date.getYear();
    }
}
