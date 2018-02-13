/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.general;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.Colors;
import fun.rubicon.util.Info;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

/**
 * Handles the 'info' command.
 *
 * @author Yannick Seeger / ForYaSee
 */
public class CommandInfo extends CommandHandler {

    private String[] arrSupporter = {"Greg"};

    public CommandInfo() {
        super(new String[]{"info", "inf", "version"}, CommandCategory.GENERAL, new PermissionRequirements("command.info", false, true), "Shows some information about the bot!", "");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        //Set some Var´s
        Message message = parsedCommandInvocation.getMessage();
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Colors.COLOR_PRIMARY);
        builder.setAuthor(Info.BOT_NAME + " - Info", "https://rubicon.fun", message.getJDA().getSelfUser().getEffectiveAvatarUrl());
        StringBuilder authors = new StringBuilder();

        //Append Id´s on StringBuilder
        for (long authorId : Info.BOT_AUTHOR_IDS) {
            User authorUser = RubiconBot.getJDA().getUserById(authorId);
            if (authorUser == null)
                authors.append(authorId).append("\n");
            else
                authors.append(authorUser.getName()).append("#").append(authorUser.getDiscriminator()).append("\n");
        }
        //Set the Embed Values
        builder.addField("Bot Name", Info.BOT_NAME, true);
        builder.addField("Bot Version", Info.BOT_VERSION, true);
        builder.addField("Website", "[Link](" + Info.BOT_WEBSITE + ")", true);
        builder.addField("Bot Invite", "[Invite RubiconBot](https://discordapp.com/oauth2/authorize?client_id=380713705073147915&scope=bot&permissions=1898982486)", true);
        builder.addField("Github Link", "[Github Link](" + Info.BOT_GITHUB + ")", true);
        builder.addField("Patreon Link", "[RubiconBot Dev Team](https://www.patreon.com/rubiconbot)", true);
        builder.addField("discordbots.org", "[discordbots.org](https://discordbots.org/bot/380713705073147915)\n", true);
        builder.addField("Support Server","[Link](dc.rucb.co)",true);
        builder.addField("Donators", String.join("\n", arrSupporter), true);
        builder.addField("Devs", authors.toString(), false);
        return new MessageBuilder().setEmbed(builder.build()).build();
    }

}
