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
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

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

        StringBuilder devsString = new StringBuilder().append("\n");
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

        String partner = "[ZapHosting](https://zap-hosting.com/rubicon)\n" +
                "[Groovy - Music Bot](https://rxsto.github.io/musicBot/invite/)\n";

        embedBuilder.setDescription("**" + invocation.translate("command.info.version") + ":** " + Info.BOT_VERSION + "\n" +
                "**" + invocation.translate("command.info.website") + ":** [rubicon.fun](https://rubicon.fun)\n" +
                "**" + invocation.translate("command.info.invite") + ":** [inv.rucb.co](http://inv.rucb.co)\n" +
                "**Github:** [github.com](https://github.com/Rubicon-Bot/Rubicon/)\n" +
                "**Patreon:** [patreon.com](https://www.patreon.com/rubiconbot)\n" +
                "**" + invocation.translate("command.info.support") + ":** [dc.rucb.co](http://dc.rucb.co)\n" +
                "**" + invocation.translate("command.info.votes") + ":** [discordbots.org](https://discordbots.org/bot/380713705073147915)\n\n" +
                "**" + invocation.translate("command.info.developer") + "** " + devsString.toString() + "\n" +
                "**" + invocation.translate("command.info.staff") + "**\n" + staffString.toString() + "\n" +
                "**" + invocation.translate("command.info.partner") + "**\n" + partner);
        return EmbedUtil.message(embedBuilder);
    }
}
