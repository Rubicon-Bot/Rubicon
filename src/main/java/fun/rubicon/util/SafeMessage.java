package fun.rubicon.util;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.concurrent.TimeUnit;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class SafeMessage {

    public static void sendMessage(TextChannel textChannel, Message message) {
        if (hasPermissions(textChannel))
            textChannel.sendMessage(message).queue();
    }

    public static void sendMessage(TextChannel textChannel, Message message, int deleteTime) {
        if (hasPermissions(textChannel))
            textChannel.sendMessage(message).queue(msg -> msg.delete().queueAfter(deleteTime, TimeUnit.SECONDS));
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
        else if(hasPermissions(textChannel))
            textChannel.sendMessage(message).queue();

    }

    public static void sendMessage(TextChannel textChannel, MessageEmbed build) {
        if (hasPermissions(textChannel))
            textChannel.sendMessage(build).queue();
    }

    public static void sendMessage(TextChannel textChannel, MessageEmbed build, int deleteTime) {
        if (hasPermissions(textChannel) && hasDeletePermission(textChannel))
            textChannel.sendMessage(build).queue(msg -> msg.delete().queueAfter(deleteTime, TimeUnit.SECONDS));
        else if(hasPermissions(textChannel))
            textChannel.sendMessage(build).queue();
    }

    public static Message sendMessageBlocking(TextChannel textChannel, String message) {
        if (hasPermissions(textChannel))
            return textChannel.sendMessage(message).complete();
        return null;
    }


    private static boolean hasPermissions(TextChannel channel) {
        if (channel.getGuild().getSelfMember().hasPermission(channel, Permission.MESSAGE_READ) && channel.getGuild().getSelfMember().hasPermission(channel, Permission.MESSAGE_WRITE))
            return true;
        channel.getGuild().getOwner().getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(EmbedUtil.error("Permission Error!", "The bot need the `MESSAGE_READ` and `MESSAGE_WRITE` permissions in the ``" + channel.getName() + "` channel to run without errors.").build()));
        return false;
    }
    private static boolean hasDeletePermission(TextChannel channel){
        if(channel.getGuild().getSelfMember().hasPermission(channel, Permission.MESSAGE_MANAGE))
            return true;
        channel.getGuild().getOwner().getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(EmbedUtil.error("Permission Error!", "The bot need the `MESSAGE_READ` and `MESSAGE_MANAGE` permissions in the ``" + channel.getName() + "` channel to run without errors.").build()));
        return false;
    }


}
