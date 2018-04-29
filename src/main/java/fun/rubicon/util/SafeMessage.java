/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.util;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.concurrent.TimeUnit;

public class SafeMessage {
    public static void sendMessage(TextChannel textChannel, Message message) {
        if (hasPermissions(textChannel))
            textChannel.sendMessage(message).queue();
    }

    public static void sendMessage(TextChannel textChannel, Message message, int deleteTime) {
        try {
            if (hasPermissions(textChannel))
                textChannel.sendMessage(message).queue(msg -> msg.delete().queueAfter(deleteTime, TimeUnit.SECONDS));
        } catch (Exception e) {
            // Ignored
        }
    }



    public static Message sendMessageBlocking(TextChannel textChannel, Message message) {
        if (hasPermissions(textChannel))
            return textChannel.sendMessage(message).complete();
        return null;
    }

    public static void sendMessage(TextChannel textChannel, String message) {
        if (hasPermissions(textChannel))
            textChannel.sendMessage(message).queue();
    }

    public static void sendMessage(TextChannel textChannel, String message, int deleteTime) {
        if (hasPermissions(textChannel) && hasDeletePermission(textChannel))
            textChannel.sendMessage(message).queue(msg -> msg.delete().queueAfter(deleteTime, TimeUnit.SECONDS));
        else if (hasPermissions(textChannel))
            textChannel.sendMessage(message).queue();

    }

    public static void sendMessage(TextChannel textChannel, MessageEmbed build) {
        if (hasPermissions(textChannel))
            textChannel.sendMessage(build).queue();
    }

    public static void sendMessage(TextChannel textChannel, MessageEmbed build, int deleteTime) {
        if (hasPermissions(textChannel) && hasDeletePermission(textChannel))
            textChannel.sendMessage(build).queue(msg -> msg.delete().queueAfter(deleteTime, TimeUnit.SECONDS));
        else if (hasPermissions(textChannel))
            textChannel.sendMessage(build).queue();
    }

    public static Message sendMessageBlocking(TextChannel textChannel, String message) {
        if (hasPermissions(textChannel))
            return textChannel.sendMessage(message).complete();
        return null;
    }
    public static Message sendMessageBlocking(TextChannel textChannel, MessageEmbed message) {
        if (hasPermissions(textChannel))
            return textChannel.sendMessage(message).complete();
        return null;
    }


    private static boolean hasPermissions(TextChannel channel) {
        return channel.getGuild().getSelfMember().hasPermission(channel, Permission.MESSAGE_READ) && channel.getGuild().getSelfMember().hasPermission(channel, Permission.MESSAGE_WRITE);
    }



    private static boolean hasDeletePermission(TextChannel channel) {
        return channel.getGuild().getSelfMember().hasPermission(channel, Permission.MESSAGE_MANAGE);
    }
}