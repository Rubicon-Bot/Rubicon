package fun.rubicon.core.music;

import fun.rubicon.RubiconBot;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageDeleteEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Schlaubi / Michael Rittmeister
 */

public class QueueMessage {
    public static HashMap<Long, QueueMessage> queueMessageStorage = new HashMap<>();

    public int sideNumbAll;
    public int currentSideNumb;
    public List<String> tracks;
    public User author;
    public Message message;

    public QueueMessage(Integer sideNumbAll, Message message, List<String> tracks, User author){
        this.sideNumbAll = sideNumbAll;
        this.currentSideNumb = 1;
        this.message = message;
        this.tracks = tracks;
        this.author = author;
        queueMessageStorage.put(message.getIdLong(), this);
    }

    public static void handleReaction(MessageReactionAddEvent event){
        if(event.getUser().isBot()) return;
        if(queueMessageStorage.containsKey(Long.parseLong(event.getMessageId()))){
            event.getReaction().removeReaction(event.getUser()).queue();
            QueueMessage queueMessage = queueMessageStorage.get(event.getMessageIdLong());
            if(!queueMessage.author.equals(event.getUser())) return;
            Message message = event.getTextChannel().getMessageById(event.getMessageIdLong()).complete();
            String reaction = event.getReactionEmote().getName();
            if(reaction.equals("⬅")){
                queueMessage.currentSideNumb--;
            } else if (reaction.equals("➡")){
                queueMessage.currentSideNumb++;
            }
            List<String> tracks = queueMessage.tracks.subList((queueMessage.currentSideNumb - 1) * 20, (queueMessage.currentSideNumb - 1) * 20 + 20);
            message.getReactions().forEach(r -> r.removeReaction().queue());
            String formattedQueue = tracks.stream().collect(Collectors.joining("\n"));
            message.editMessage(new EmbedBuilder().setDescription("**CURRENT QUEUE:**\n" +
                    "*[" + RubiconBot.getGuildMusicPlayerManager().getPlayerByGuild(event.getGuild()).getTrackList().size() + " Tracks | Side " + queueMessage.currentSideNumb + " / " + queueMessage.sideNumbAll + "]* \n" + formattedQueue).build()).queue();
            queueMessageStorage.replace(event.getMessageIdLong(), queueMessage);
            if(queueMessage.currentSideNumb > 1) {
                message.addReaction("⬅").queue();
            }

            if(queueMessage.currentSideNumb < queueMessage.sideNumbAll) {
                message.addReaction("➡").queue();
            }
        }
        //Stop Thread because we don't need him anymore
        Thread.currentThread().interrupt();
    }

    public static void handleMessageDeletion(MessageDeleteEvent event) {
        if(queueMessageStorage.containsKey(Long.parseLong(event.getMessageId()))) {
            queueMessageStorage.remove(event.getGuild().getIdLong());
        }
        //Stop Thread because we don't need him anymore
        Thread.currentThread().interrupt();
    }


}
