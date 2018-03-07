/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.settings;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.core.entities.RubiconGuild;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.Colors;
import fun.rubicon.util.EmbedUtil;
import fun.rubicon.util.PermissionUtil;
import fun.rubicon.util.SafeMessage;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;

import java.util.*;

/**
 * @author ForYaSee / Yannick Seeger
 */
public class CommandAutochannel extends CommandHandler {

    public CommandAutochannel() {
        super(new String[]{"autochannel", "ac"}, CommandCategory.MODERATION, new PermissionRequirements("autochannel", false, false), "Create channels that duplicate themselves upon joining.", "" +
                "list\n" +
                "add <channelname>\n" +
                "remove <channelname>");
    }

    private static HashMap<Guild, ChannelSearch> searches = new HashMap<>();

    private static final String[] EMOJIS = ("\uD83C\uDF4F \uD83C\uDF4E \uD83C\uDF50 \uD83C\uDF4A \uD83C\uDF4B \uD83C\uDF4C \uD83C\uDF49 \uD83C\uDF47 \uD83C\uDF53 \uD83C\uDF48 \uD83C\uDF52 \uD83C\uDF51 \uD83C\uDF4D \uD83E\uDD5D " +
            "\uD83E\uDD51 \uD83C\uDF45 \uD83C\uDF46 \uD83E\uDD52 \uD83E\uDD55 \uD83C\uDF3D \uD83C\uDF36 \uD83E\uDD54 \uD83C\uDF60 \uD83C\uDF30 \uD83E\uDD5C \uD83C\uDF6F \uD83E\uDD50 \uD83C\uDF5E " +
            "\uD83E\uDD56 \uD83E\uDDC0 \uD83E\uDD5A \uD83C\uDF73 \uD83E\uDD53 \uD83E\uDD5E \uD83C\uDF64 \uD83C\uDF57 \uD83C\uDF56 \uD83C\uDF55 \uD83C\uDF2D \uD83C\uDF54 \uD83C\uDF5F \uD83E\uDD59 " +
            "\uD83C\uDF2E \uD83C\uDF2F \uD83E\uDD57 \uD83E\uDD58 \uD83C\uDF5D \uD83C\uDF5C \uD83C\uDF72 \uD83C\uDF65 \uD83C\uDF63 \uD83C\uDF71 \uD83C\uDF5B \uD83C\uDF5A \uD83C\uDF59 \uD83C\uDF58 " +
            "\uD83C\uDF62 \uD83C\uDF61 \uD83C\uDF67 \uD83C\uDF68 \uD83C\uDF66 \uD83C\uDF70 \uD83C\uDF82 \uD83C\uDF6E \uD83C\uDF6D \uD83C\uDF6C \uD83C\uDF6B \uD83C\uDF7F \uD83C\uDF69 \uD83C\uDF6A \uD83E\uDD5B " +
            "\uD83C\uDF75 \uD83C\uDF76 \uD83C\uDF7A \uD83C\uDF7B \uD83E\uDD42 \uD83C\uDF77 \uD83E\uDD43 \uD83C\uDF78 \uD83C\uDF79 \uD83C\uDF7E \uD83E\uDD44 \uD83C\uDF74 \uD83C\uDF7D").split(" ");

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        RubiconGuild rubiconGuild = RubiconGuild.fromGuild(invocation.getGuild());
        if (invocation.getArgs().length == 1) {
            if (invocation.getArgs()[0].equalsIgnoreCase("list")) {
                StringBuilder stringBuilder = new StringBuilder();
                for (long id : rubiconGuild.getAutochannels()) {
                    try {
                        VoiceChannel channel = invocation.getJDA().getVoiceChannelById(id);
                        if (channel == null)
                            continue;
                        stringBuilder.append(String.format("%s(%s)", channel.getName(), channel.getId())).append("\n");
                    } catch (Exception ignored) {
                        // ignored
                    }
                }
                return EmbedUtil.message(EmbedUtil.info(invocation.translate("command.autochannel.list.title"), stringBuilder.toString()));
            } else
                return createHelpMessage();
        } else if (invocation.getArgs().length >= 2) {
            switch (invocation.getArgs()[0].toLowerCase()) {
                case "add":
                case "set":
                    addChannel(invocation);
                    break;
                case "remove":
                    removeChannel(invocation);
                    break;
                default:
                    return createHelpMessage();
            }
        } else
            return createHelpMessage();
        return null;
    }

    private ChannelSearch createChannelSearch(List<VoiceChannel> channelList, CommandManager.ParsedCommandInvocation invocation, boolean delete) {
        HashMap<String, VoiceChannel> channelMap = new HashMap<>();
        StringBuilder channelNames = new StringBuilder();
        ArrayList<String> emojiList = new ArrayList<>(Arrays.asList(EMOJIS));
        channelList.forEach(channel -> {
            String category;
            if (channel.getParent() != null)
                category = channel.getParent().getName();
            else
                category = "NONE";
            channelNames.append(emojiList.get(0)).append(" - ").append(channel.getName()).append("(`").append(channel.getId()).append(String.format("`) (%s: ", invocation.translate("command.autochannel.search.category"))).append(category).append(")\n");
            channelMap.put(emojiList.get(0), channel);
            emojiList.remove(0);
        });
        if (channelMap.isEmpty()) {
            SafeMessage.sendMessage(invocation.getTextChannel(), EmbedUtil.error(invocation.translate("command.autochannel.search.unknown.title"), invocation.translate("command.autochannel.search.unknown.description")).build(), 60);
            return null;
        } else {
            Message msg = SafeMessage.sendMessageBlocking(invocation.getTextChannel(), EmbedUtil.message(new EmbedBuilder().setColor(Colors.COLOR_SECONDARY).setDescription(channelNames.toString())));
            channelMap.keySet().forEach(e -> msg.addReaction(e).queue());
            return new ChannelSearch(msg, channelMap, invocation.getAuthor().getIdLong(), invocation.getLanguage(), delete);
        }
    }

    private ChannelSearch createChannelSearch(List<VoiceChannel> channelList, CommandManager.ParsedCommandInvocation invocation) {
        return createChannelSearch(channelList, invocation, false);
    }

    public static void handleReaction(MessageReactionAddEvent event) {
        if (!searches.containsKey(event.getGuild()))
            return;
        ChannelSearch search = searches.get(event.getGuild());
        if (!event.getMessageId().equals(search.message.getId()))
            return;
        if (event.getUser().getIdLong() != search.requesterId)
            return;
        String emote = event.getReactionEmote().getName();
        event.getReaction().removeReaction(event.getUser()).queue();
        if (!search.channels.containsKey(emote))
            return;
        VoiceChannel channel = search.channels.get(emote);
        if (search.remove) {
            RubiconGuild.fromGuild(event.getGuild()).deleteAutochannel(channel.getIdLong());
            SafeMessage.sendMessage(search.message.getTextChannel(), EmbedUtil.message(EmbedUtil.success(search.language.getString("command.autochannel.search.remove.title"), search.language.getString("command.autochannel.search.remove.description").replaceFirst("%channel%", "`" + channel.getName() + "`"))));
        } else {
            RubiconGuild.fromGuild(event.getGuild()).addAutochannel(channel.getIdLong());
            SafeMessage.sendMessage(search.message.getTextChannel(), EmbedUtil.message(EmbedUtil.success(search.language.getString("command.autochannel.added.title"), search.language.getString("command.autochannel.added.description").replaceFirst("%channel%", "`" + channel.getName() + "`"))));
        }
        if (PermissionUtil.canManageMessages(event.getMember(), event.getTextChannel()))
            event.getTextChannel().getMessageById(event.getMessageId()).complete().getReactions().forEach(r -> r.removeReaction().queue());
    }

    private void addChannel(CommandManager.ParsedCommandInvocation invocation) {
        String searchChannelName = invocation.getArgsString();

        List<VoiceChannel> foundChannels = invocation.getGuild().getVoiceChannelsByName(searchChannelName, true);
        SafeMessage.sendMessage(invocation.getTextChannel(), EmbedUtil.message(EmbedUtil.error(invocation.translate("command.autochannel.add.title"), invocation.translate("command.autochannel.add.description"))));
        if (foundChannels.size() > 1) {
            ChannelSearch search = createChannelSearch(foundChannels, invocation, true);
            searches.put(invocation.getGuild(), search);
        } else {
            VoiceChannel channel = foundChannels.get(0);
            RubiconGuild.fromGuild(invocation.getGuild()).addAutochannel(channel.getIdLong());
            SafeMessage.sendMessage(invocation.getTextChannel(), EmbedUtil.message(EmbedUtil.success(invocation.translate("command.autochannel.added.title"), invocation.translate("command.autochannel.added.description").replaceFirst("%channel%", "`" + channel.getName() + "`"))));
        }
    }

    private void removeChannel(CommandManager.ParsedCommandInvocation invocation) {
        RubiconGuild rubiconGuild = RubiconGuild.fromGuild(invocation.getGuild());
        String searchChannelName = invocation.getArgsString();
        List<VoiceChannel> foundChannels = invocation.getGuild().getVoiceChannelsByName(searchChannelName, true);
        if (foundChannels.size() > 1) {
            ChannelSearch search = createChannelSearch(foundChannels, invocation);
            searches.put(invocation.getGuild(), search);
        } else {
            VoiceChannel channel = foundChannels.get(0);
            RubiconGuild.fromGuild(invocation.getGuild()).addAutochannel(channel.getIdLong());
            SafeMessage.sendMessage(invocation.getTextChannel(), EmbedUtil.message(EmbedUtil.success(invocation.translate("command.autochannel.added.title"), invocation.translate("command.autochannel.added.description").replaceFirst("%channel%", "`" + channel.getName() + "`"))));
        }
    }

    private class ChannelSearch {
        Message message;
        HashMap<String, VoiceChannel> channels;
        ResourceBundle language;
        boolean remove = false;
        long requesterId;

        private ChannelSearch(Message message, HashMap<String, VoiceChannel> channels, long requesterId, ResourceBundle language, boolean remove) {
            this.message = message;
            this.channels = channels;
            this.remove = remove;
            this.language = language;
            this.requesterId = requesterId;
        }

        private ChannelSearch(Message message, HashMap<String, VoiceChannel> channels, long requesterId, ResourceBundle language) {
            this(message, channels, requesterId, language, false);
        }
    }
}
