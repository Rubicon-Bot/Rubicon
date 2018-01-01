/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.tools;


import fun.rubicon.command.CommandCategory;
import fun.rubicon.command2.CommandHandler;
import fun.rubicon.command2.CommandManager;
import fun.rubicon.data.PermissionLevel;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import fun.rubicon.util.Colors;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.time.format.DateTimeFormatter;

public class CommandServerInfo extends CommandHandler{

    public CommandServerInfo() {
        super(new String[]{"serverinfo", "guild", "guildinfo"}, CommandCategory.TOOLS, new PermissionRequirements(PermissionLevel.EVERYONE, "command.serverinfo"), "Returns some information about the current server", "");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        Message message = parsedCommandInvocation.invocationMessage;
        Guild guild = message.getGuild();
        TextChannel channel = message.getTextChannel();

        StringBuilder rawRoles = new StringBuilder();
        guild.getRoles().forEach(r -> rawRoles.append(r.getName()).append(", "));
        StringBuilder roles = new StringBuilder(rawRoles.toString());
        roles.replace(rawRoles.lastIndexOf(","), roles.lastIndexOf(",") + 1, "" );
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
        serverInfo.addField("Server Creation Date", guild.getCreationTime().format(DateTimeFormatter.ISO_DATE_TIME), false);
        return new MessageBuilder().setEmbed(serverInfo.build()).build();
    }
}
