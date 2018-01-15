/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.tools;


import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import fun.rubicon.util.Colors;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

import java.time.OffsetDateTime;

public class CommandUserInfo extends CommandHandler {

    public CommandUserInfo() {
        super(new String[]{"userinfo", "whois"}, CommandCategory.TOOLS, new PermissionRequirements(0, "comm"), "Returns some information about the specified user", "[@User]");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        Message message = parsedCommandInvocation.getMessage();
        User info;
        String[] args = parsedCommandInvocation.getArgs();
        if (args.length > 0) {
            if (message.getMentionedUsers().size() > 0)
                info = message.getMentionedUsers().get(0);
            else {
                return createHelpMessage();
            }
        } else {
            info = message.getAuthor();
        }

        Member user = message.getGuild().getMember(info);
        StringBuilder rawRoles = new StringBuilder();
        user.getRoles().forEach(r -> rawRoles.append(r.getName()).append(", "));
        StringBuilder roles = new StringBuilder(rawRoles.toString());
        if (!user.getRoles().isEmpty())
            roles.replace(rawRoles.lastIndexOf(","), roles.lastIndexOf(",") + 1, "");
        EmbedBuilder userinfo = new EmbedBuilder();
        userinfo.setColor(Colors.COLOR_PRIMARY);
        userinfo.setTitle("User information of " + user.getUser().getName());
        userinfo.setThumbnail(info.getAvatarUrl());
        userinfo.addField("Nickname", user.getEffectiveName(), false);
        userinfo.addField("User id", info.getId(), false);
        userinfo.addField("Status", user.getOnlineStatus().toString().replace("_", ""), false);
        if (user.getGame() != null)
            userinfo.addField("Game", user.getGame().toString(), false);
        userinfo.addField("Guild join date", formatDate(user.getJoinDate()), false);
        userinfo.addField("Roles", "`" + roles.toString() + "`", false);
        userinfo.addField("Discord join date", formatDate(info.getCreationTime()), false);
        userinfo.addField("Avatar url", info.getAvatarUrl(), true);
        userinfo.addField("--- RubiconBot Stats ---", "" +
                "Level: " + RubiconBot.getMySQL().getUserValue(user.getUser(), "level") + "\n" +
                "Ruby's: " + RubiconBot.getMySQL().getUserValue(user.getUser(), "money"), true);
        return new MessageBuilder().setEmbed(userinfo.build()).build();
    }

    public String formatDate(OffsetDateTime date) {
        return date.getMonthValue() + "/" + date.getDayOfMonth() + "/" + date.getYear();
    }

}
