/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.util;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.TextChannel;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

public class SafeMessage {
    public static void sendMessage(TextChannel textChannel, Message message) {
        if (hasPermissions(textChannel))
           send(textChannel, message);
    }

    public static void sendMessage(TextChannel textChannel, Message message, int deleteTime) {
        try {
            if (hasPermissions(textChannel))
                send(textChannel, message);
        } catch (Exception e) {
            // Ignored
        }
    }



    public static Message sendMessageBlocking(TextChannel textChannel, Message message) {
        if (hasPermissions(textChannel))
            send(textChannel, message);
        return null;
    }

    public static void sendMessage(TextChannel textChannel, String message) {
        if (hasPermissions(textChannel))
            send(textChannel, new MessageBuilder().setContent(message).build());
    }

    public static void sendMessage(TextChannel textChannel, String message, int deleteTime) {
        if (hasPermissions(textChannel) && hasDeletePermission(textChannel))
            send(textChannel, new MessageBuilder().setContent(message).build()).delete().queueAfter(deleteTime, TimeUnit.SECONDS);
        else if (hasPermissions(textChannel))
            send(textChannel, new MessageBuilder().setContent(message).build());

    }

    public static void sendMessage(TextChannel textChannel, MessageEmbed build) {
        if (hasPermissions(textChannel))
            send(textChannel, build);
    }

    public static void sendMessage(TextChannel textChannel, MessageEmbed build, int deleteTime) {
        if (hasPermissions(textChannel) && hasDeletePermission(textChannel))
            send(textChannel, build).delete().queueAfter(deleteTime, TimeUnit.SECONDS);
        else if (hasPermissions(textChannel))
            send(textChannel, build);
    }

    public static Message sendMessageBlocking(TextChannel textChannel, String message) {
        if (hasPermissions(textChannel))
            return send(textChannel, new MessageBuilder().setContent(message).build());
        return null;
    }
    public static Message sendMessageBlocking(TextChannel textChannel, MessageEmbed message) {
        if (hasPermissions(textChannel))
            return send(textChannel, message);
        return null;
    }


    private static boolean hasPermissions(TextChannel channel) {
        return channel.getGuild().getSelfMember().hasPermission(channel, Permission.MESSAGE_READ);
    }

    private static boolean hasEmbedPermissions(TextChannel channel){
        return channel.getGuild().getSelfMember().hasPermission(channel, Permission.MESSAGE_EMBED_LINKS);
    }

    private static Message send(TextChannel textChannel, Message msg){
        if(!hasEmbedPermissions(textChannel) && !msg.getEmbeds().isEmpty())
            return textChannel.sendMessage(formatEmbed(msg.getEmbeds().get(0))).complete();
        else
            return textChannel.sendMessage(msg).complete();
    }
    private static Message send(TextChannel textChannel, MessageEmbed msg){
        if(!hasEmbedPermissions(textChannel))
            return textChannel.sendMessage(formatEmbed(msg)).complete();
        else
            return textChannel.sendMessage(msg).complete();
    }

    private static String formatEmbed(MessageEmbed embed){
        StringBuilder string = new StringBuilder();
        if(embed.getTitle() != null)
            string.append("**__").append(embed.getTitle()).append("__**").append("\n");
        if(embed.getDescription() != null)
            string.append(embed.getDescription());
        embed.getFields().forEach(field -> {
            string.append("**__").append(field.getName()).append("__**\n").append(field.getValue()).append("\n");
        });
        if(embed.getFooter() != null)
            string.append("\n").append("_").append(embed.getFooter().getText()).append("_");
        String out = string.toString();
        if(string.length() > 1024)
            out = "This message is to longer than 1024 chars, please give me `MESSAGE_EMBED_LINKS` permission and try again";
        return out;
    }

    private static boolean hasDeletePermission(TextChannel channel) {
        return channel.getGuild().getSelfMember().hasPermission(channel, Permission.MESSAGE_MANAGE);
    }

    public static void sendFile(TextChannel channel, Message message, InputStream image) {
        if(hasPermissions(channel))
            channel.sendFile(image, "file.png", message).queue();
    }

    public static Message sendFileBlocking(TextChannel channel, Message message, InputStream image) {
        if(hasPermissions(channel))
            return channel.sendFile(image, "file.png", message).complete();
        return null;
    }
}