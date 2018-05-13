package fun.rubicon.listener.feature;

import fun.rubicon.RubiconBot;
import fun.rubicon.commands.tools.CommandPoll;
import fun.rubicon.core.entities.RubiconPoll;
import fun.rubicon.features.poll.PollManager;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.MessageDeleteEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.Timer;
import java.util.TimerTask;

public class VoteListener extends ListenerAdapter {

    private PollManager pollManager = RubiconBot.getPollManager();

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        new Thread(() -> handleMessageReaction(event), "PollMessageReactHandler-" + event.getMessageId()).start();
    }

    @Override
    public void onMessageDelete(MessageDeleteEvent event) {
        new Thread(() -> handleMessageDeletion(event), "PollMessageDeleteHandler-" + event.getMessageId()).start();
    }

    @Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent event) {
        new Thread(() -> handleReactionRemove(event), "PollReactionRemoveHandler-" + event.getMessageId()).start();
    }

    private void handleMessageReaction(MessageReactionAddEvent event) {
        Guild guild = event.getGuild();
        if (event.getUser().isBot() || !pollManager.pollExists(guild)) return;
        RubiconPoll poll = pollManager.getPollByGuild(guild);
        if (!poll.isPollmsg(event.getMessageId())) return;
        if (poll.getVotes().containsKey(event.getUser().getId())) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    event.getReaction().removeReaction(event.getUser()).queue();
                }
            }, 1000);
            return;
        }
        System.out.println("dassdaa");
        String emoji = event.getReactionEmote().getName();
        event.getReaction().removeReaction(event.getUser()).queue();
        poll.addVote(event.getMember(), poll.getReacts().get(emoji));
        poll.updateMessages(event.getGuild(), CommandPoll.getParsedPoll(poll, event.getGuild()));
        pollManager.replacePoll(poll, guild);
    }

    private void handleMessageDeletion(MessageDeleteEvent event) {
        if (!pollManager.pollExists(event.getGuild())) return;
        RubiconPoll poll = pollManager.getPollByGuild(event.getGuild());
        if (!poll.isPollmsg(event.getMessageId())) return;
        poll.removePollMsg(event.getMessageId());
        pollManager.replacePoll(poll, event.getGuild());
    }

    private void handleReactionRemove(MessageReactionRemoveEvent event) {
        try {
            if (!pollManager.pollExists(event.getGuild())) return;
            RubiconPoll poll = pollManager.getPollByGuild(event.getGuild());
            if (!poll.isPollmsg(event.getMessageId())) return;
            event.getChannel().getMessageById(event.getMessageId()).complete().addReaction(event.getReactionEmote().getName()).queue();
        } catch (Exception ignored) {
        }
    }


}
