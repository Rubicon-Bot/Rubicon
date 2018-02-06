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
import fun.rubicon.data.PermissionLevel;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import fun.rubicon.util.Colors;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class CommandServerInfo extends CommandHandler {

    public CommandServerInfo() {
        super(new String[]{"serverinfo", "guild", "guildinfo"}, CommandCategory.TOOLS, new PermissionRequirements(PermissionLevel.EVERYONE, "command.serverinfo"), "Returns some information about the current/an other server", "[serverid]");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {

        if (RubiconBot.getJDA().getGuildById(parsedCommandInvocation.getArgs()[0]).equals(null)){
        Message message = parsedCommandInvocation.getMessage();
        Guild guild = message.getGuild();

        StringBuilder rawRoles = new StringBuilder();
        guild.getRoles().forEach(r -> rawRoles.append(r.getName()).append(", "));
        StringBuilder roles = new StringBuilder(rawRoles.toString());
        roles.replace(rawRoles.lastIndexOf(","), roles.lastIndexOf(",") + 1, "");
        EmbedBuilder serverInfo = new EmbedBuilder();
        serverInfo.setColor(Colors.COLOR_PRIMARY);
        serverInfo.setTitle(":desktop: Serverinfo of " + guild.getName());
        serverInfo.setThumbnail(guild.getIconUrl());
        serverInfo.addField("ID", "`" + guild.getId() + "`", false);
        serverInfo.addField("Guildname", "`" + guild.getName() + "`", false);
        serverInfo.addField("Server region", guild.getRegion().toString(), false);
        serverInfo.addField("Members", String.valueOf(guild.getMembers().size()), false);
        serverInfo.addField("Textchannels", String.valueOf(guild.getTextChannels().size()), false);
        serverInfo.addField("Voicechannels", String.valueOf(guild.getVoiceChannels().size()), false);
        serverInfo.addField("Roles", String.valueOf(guild.getRoles().size()) + "\n ```" + roles.toString() + "```", false);
        serverInfo.addField("Server owner", guild.getOwner().getUser().getName() + "#" + guild.getOwner().getUser().getDiscriminator(), false);
        serverInfo.addField("Server icon url", guild.getIconUrl(), false);
        serverInfo.addField("Server Creation Date", formatDate(guild.getCreationTime()), false);
        return new MessageBuilder().setEmbed(serverInfo.build()).build();
        }else {
            Message message = parsedCommandInvocation.getMessage();
            Guild guild = message.getGuild();
        }
        System.out.println();
return null;
    }

    public String formatDate(OffsetDateTime date) {
        return date.getMonthValue() + "/" + date.getDayOfMonth() + "/" + date.getYear();
    }

}
