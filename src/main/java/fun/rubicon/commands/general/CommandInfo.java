/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.general;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.Colors;
import fun.rubicon.util.EmbedUtil;
import fun.rubicon.util.Info;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class CommandInfo extends CommandHandler {

    public CommandInfo() {
        super(new String[]{"info", "botinfo"}, CommandCategory.GENERAL, new PermissionRequirements("info", false, true), "Shows some information about the bot.", "");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Colors.COLOR_SECONDARY);
        embedBuilder.setAuthor(Info.BOT_NAME + invocation.translate("command.info.title"), "https://rubicon.fun", RubiconBot.getSelfUser().getEffectiveAvatarUrl());

        Guild rubiconGuild = RubiconBot.getShardManager().getGuildById(Info.RUBICON_SERVER);

        Role translatorRole = RubiconBot.getShardManager().getRoleById(Info.ROLE_TRANSLATOR);
        Role donatorRole = RubiconBot.getShardManager().getRoleById(Info.ROLE_DONATOR);

        StringBuilder devsString = new StringBuilder();
        for (long authorId : Info.BOT_AUTHOR_IDS) {
            User authorUser = RubiconBot.getShardManager().getUserById(authorId);
            if (authorUser == null)
                devsString.append(authorId).append("\n");
            else
                devsString.append(authorUser.getName()).append("#").append(authorUser.getDiscriminator()).append("\n");
        }

        StringBuilder staffString = new StringBuilder();
        for (long authorId : Info.COMMUNITY_STAFF_TEAM) {
            User authorUser = RubiconBot.getShardManager().getUserById(authorId);
            if (authorUser == null)
                staffString.append(authorId).append("\n");
            else
                staffString.append(authorUser.getName()).append("#").append(authorUser.getDiscriminator()).append("\n");
        }

        StringBuilder translatorString = new StringBuilder();
        for (Member member : rubiconGuild.getMembers()) {
            if (member.getRoles().contains(translatorRole))
                translatorString.append(member.getUser().getName()).append("#").append(member.getUser().getDiscriminator()).append("\n");
        }

        StringBuilder donatorString = new StringBuilder();
        for (Member member : rubiconGuild.getMembers()) {
            if (member.getRoles().contains(donatorRole))
                donatorString.append(member.getUser().getName()).append("#").append(member.getUser().getDiscriminator()).append("\n");
        }

        embedBuilder.addField(invocation.translate("command.info.version"), Info.BOT_VERSION, true);
        embedBuilder.addField(invocation.translate("command.info.website"), "[rubicon.fun](https://rubicon.fun)", true);
        embedBuilder.addField(invocation.translate("command.info.invite"), "[inv.rucb.co](http://inv.rucb.co)", true);
        embedBuilder.addField("Github", "[github.com](https://github.com/Rubicon-Bot/Rubicon/)", true);
        embedBuilder.addField("Patreon", "[patreon.com](https://www.patreon.com/rubiconbot)", true);
        embedBuilder.addField(invocation.translate("command.info.support"), "[dc.rucb.co](http://dc.rucb.co)", true);
        embedBuilder.addField(invocation.translate("command.info.votes"), "[discordbots.org](https://discordbots.org/bot/380713705073147915)", true);
        embedBuilder.addBlankField(false);
        embedBuilder.addField(invocation.translate("command.info.developer"), devsString.toString(), true);
        embedBuilder.addField(invocation.translate("command.info.staff"), devsString.toString(), true);
        embedBuilder.addBlankField(false);
        embedBuilder.addField(invocation.translate("command.info.translator"), translatorString.toString(), true);
        embedBuilder.addField(invocation.translate("command.info.donator"), donatorString.toString(), true);

        return EmbedUtil.message(embedBuilder);
    }
}
