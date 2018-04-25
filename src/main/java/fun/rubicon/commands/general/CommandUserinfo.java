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
import fun.rubicon.util.Colors;
import fun.rubicon.util.DateUtil;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class CommandUserinfo extends CommandHandler {

    public CommandUserinfo() {
        super(new String[]{"userinfo"}, CommandCategory.GENERAL, new PermissionRequirements("userinfo", false, true), "Shows some information about a user.", "| Information about yourself\n<@User>\nInformation about a other user.");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        User infoUser = invocation.getMessage().getMentionedUsers().size() == 1 ? invocation.getMessage().getMentionedUsers().get(0) : invocation.getAuthor();
        Member infoMember = invocation.getGuild().getMember(infoUser);
        StringBuilder rawRoles = new StringBuilder();
        infoMember.getRoles().forEach(r -> rawRoles.append(r.getName()).append(", "));
        StringBuilder roles = new StringBuilder(rawRoles.toString());
        if (!infoMember.getRoles().isEmpty())
            roles.replace(rawRoles.lastIndexOf(","), roles.lastIndexOf(",") + 1, "");
        String resRoles = roles.toString();
        if (resRoles.length() > 400) {
            resRoles = resRoles.substring(0, 400) + "., more ....";
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setAuthor(invocation.translate("command.userinfo.title") + " - " + infoUser.getName(), null, infoUser.getAvatarUrl());
        embedBuilder.setColor(Colors.COLOR_SECONDARY);
        embedBuilder.setThumbnail(infoUser.getAvatarUrl());
        embedBuilder.setDescription(invocation.translate("command.userinfo.description").replace("%user%", "`" + infoUser.getName() + "`"));
        embedBuilder.addField(invocation.translate("command.userinfo.id"), infoUser.getId(), true);
        embedBuilder.addField(invocation.translate("command.userinfo.tag"), infoUser.getName() + "#" + infoUser.getDiscriminator(), true);
        embedBuilder.addField(invocation.translate("command.userinfo.creation.title"), DateUtil.formatDate(infoUser.getCreationTime(), invocation.translate("date.format")), true);
        embedBuilder.addField(invocation.translate("command.userinfo.joindate.title"), DateUtil.formatDate(infoMember.getJoinDate(), invocation.translate("date.format")), true);
        embedBuilder.addField(invocation.translate("command.userinfo.avatar.title"), infoUser.getAvatarUrl(), false);
        embedBuilder.addField(invocation.translate("command.userinfo.roles.title"), "```" + resRoles + "```", false);
        return EmbedUtil.message(embedBuilder);
    }
}
