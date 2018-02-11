/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.settings;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.sql.MySQL;
import fun.rubicon.util.Colors;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.channel.text.TextChannelDeleteEvent;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class CommandBlacklist extends CommandHandler {
    public CommandBlacklist() {
        super(new String[]{"blacklist", "bl", "block"}, CommandCategory.SETTINGS, new PermissionRequirements("command.blacklist", false, false), "Blacklist channels from commands", "blacklist add <#Channel>\n blacklist remove <#Channel>\n blacklist list", false);
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        Message message = parsedCommandInvocation.getMessage();
        String[] args = parsedCommandInvocation.getArgs();
        if (args.length == 0) {
            return createHelpMessage(parsedCommandInvocation);
        }
        switch (args[0]) {
            case "list":
            case "ls":
                listChannels(parsedCommandInvocation);
                break;
            case "remove":
            case "rm":
            case "unblock":
                unblockChannel(parsedCommandInvocation);
                break;
            case "block":
            case "add":
                blockChannel(parsedCommandInvocation);
                break;
            default:
                message.getTextChannel().sendMessage(createHelpMessage(parsedCommandInvocation)).queue(msg -> msg.delete().queueAfter(5, TimeUnit.SECONDS));
                break;

        }
        return null;
    }

    private void listChannels(CommandManager.ParsedCommandInvocation parsedCommandInvocation) {
        Message message = parsedCommandInvocation.getMessage();
        String channels = RubiconBot.getMySQL().getGuildValue(message.getGuild(), "blacklist");
        if (channels.equals(""))
            message.getTextChannel().sendMessage(EmbedUtil.error("Empty!", "Your blacklist is empty").build()).queue(msg -> msg.delete().queueAfter(5, TimeUnit.SECONDS));
        else
            message.getTextChannel().sendMessage(createChannels(message.getGuild()).build()).queue(msg -> msg.delete().queueAfter(10, TimeUnit.SECONDS));
    }

    private void unblockChannel(CommandManager.ParsedCommandInvocation parsedCommandInvocation) {
        Message message = parsedCommandInvocation.getMessage();
        if (message.getMentionedChannels().isEmpty()) {
            message.getTextChannel().sendMessage(EmbedUtil.error("Unknown channel", "Please mention the textchannel that should be blacklisted").build()).queue(msg -> msg.delete().queueAfter(5, TimeUnit.SECONDS));
            return;
        }
        TextChannel channel = message.getMentionedChannels().get(0);
        if (!RubiconBot.getMySQL().isBlacklisted(channel)) {
            message.getTextChannel().sendMessage(EmbedUtil.error("Unblacklisted channel", "That channel is not blacklisted").build()).queue(msg -> msg.delete().queueAfter(5, TimeUnit.SECONDS));
            return;
        }
        unblockChannel(channel);
        message.getTextChannel().sendMessage(EmbedUtil.success("Unblacklisted channel!", "Successfully removed channel from blacklist").build()).queue(msg -> msg.delete().queueAfter(5, TimeUnit.SECONDS));

    }

    private void blockChannel(CommandManager.ParsedCommandInvocation parsedCommandInvocation) {
        Message message = parsedCommandInvocation.getMessage();
        if (message.getMentionedChannels().isEmpty()) {
            message.getTextChannel().sendMessage(EmbedUtil.error("Unknown channel", "Please mention the textchannel that should be blacklisted").build()).queue(msg -> msg.delete().queueAfter(5, TimeUnit.SECONDS));
            return;
        }
        TextChannel channel = message.getMentionedChannels().get(0);
        if (RubiconBot.getMySQL().isBlacklisted(channel)) {
            message.getTextChannel().sendMessage(EmbedUtil.error("Blacklisted channel", "That channel is already blacklisted").build()).queue(msg -> msg.delete().queueAfter(5, TimeUnit.SECONDS));
            return;
        }
        String oldEntry = RubiconBot.getMySQL().getGuildValue(message.getGuild(), "blacklist");
        String newEntry;
        if (oldEntry.equals(""))
            newEntry = channel.getId();
        else
            newEntry = oldEntry + "," + channel.getId();
        RubiconBot.getMySQL().updateGuildValue(message.getGuild(), "blacklist", newEntry);
        message.getTextChannel().sendMessage(EmbedUtil.success("Blacklisted channel!", "Successfully added channel to blacklist").build()).queue(msg -> msg.delete().queueAfter(5, TimeUnit.SECONDS));
    }

    private EmbedBuilder createChannels(Guild guild) {
        String[] channels = RubiconBot.getMySQL().getGuildValue(guild, "blacklist").split(",");
        StringBuilder channelnames = new StringBuilder();
        Arrays.asList(channels).forEach(c -> {
            TextChannel channel = guild.getTextChannelById(c);
            channelnames.append(channel.getName() + "`(" + channel.getId() + ")`, ");
        });
        channelnames.replace(channelnames.lastIndexOf(","), channelnames.lastIndexOf(",") + 1, "");
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Colors.COLOR_SECONDARY);
        embed.setTitle("Blacklisted channels");
        embed.setAuthor(guild.getName(), guild.getIconUrl());
        embed.setDescription(channelnames.toString());
        return embed;
    }

    private static MySQL unblockChannel(TextChannel channel) {
        String oldEntry = RubiconBot.getMySQL().getGuildValue(channel.getGuild(), "blacklist");
        String newEntry = oldEntry.replace("," + channel.getId(), "");
        return RubiconBot.getMySQL().updateGuildValue(channel.getGuild(), "blacklist", newEntry);
    }

    public static void handleChannelDeletion(TextChannelDeleteEvent event) {
        TextChannel channel = event.getChannel();
        if (RubiconBot.getMySQL().isBlacklisted(channel))
            unblockChannel(channel);
    }

}
