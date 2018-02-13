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
import fun.rubicon.util.Info;
import fun.rubicon.util.SafeMessage;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.io.IOException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Handles the 'feedback' command which sends a feedback message to the developer server.
 */
public class CommandFeedback extends CommandHandler {
    private static HashMap<TextChannel, FeedbackTitle> channelMsg = new HashMap<>();
    private static Timer timer = new Timer();

    /**
     * Constructs this CommandHandler.
     */
    public CommandFeedback() {
        super(new String[]{"feedback", "submitidea", "submit-idea", "feature"}, CommandCategory.GENERAL,
                new PermissionRequirements("command.feedback", false, true),
                "Sends a feedback message to the developers.", "<Feedback title>");
    }


    private static String Header = "<p><strong>Feedback</strong><br><br><strong>Feedback report by ";
    private static String Sufix = " </strong><br><br><strong>Description</strong><br><br></p>";

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions permissions) {
        String title = parsedCommandInvocation.getMessage().getContentDisplay().replace(parsedCommandInvocation.getPrefix() + parsedCommandInvocation.getCommandInvocation(), "");
        FeedbackTitle tite1 = new FeedbackTitle(title, parsedCommandInvocation.getAuthor(), parsedCommandInvocation.getTextChannel(), parsedCommandInvocation.getMessage().getContentDisplay());
        channelMsg.put(parsedCommandInvocation.getTextChannel(), tite1);
        SafeMessage.sendMessage(parsedCommandInvocation.getTextChannel(), new EmbedBuilder().setTitle("Set Feedback Description").setDescription("Please write a short Description about the Feedback in this Channel").setFooter("Will abort in 30sec.", null).build(), 30);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                channelMsg.remove(parsedCommandInvocation.getTextChannel());
                parsedCommandInvocation.getTextChannel().sendMessage("Setup abort").queue(message -> {
                    message.delete().queueAfter(5L, TimeUnit.SECONDS);
                });
                return;
            }
        }, 30000);
        return null;
    }

    public static void handle(MessageReceivedEvent event) {
        if (!channelMsg.containsKey(event.getTextChannel()))
            return;
        FeedbackTitle titel = channelMsg.get(event.getTextChannel());
        if (event.getMessage().getContentDisplay().equals(titel.getMessage()))
            return;
        if (event.getAuthor().equals(RubiconBot.getJDA().getSelfUser()))
            return;

        if (!event.getAuthor().equals(titel.getAuthor()))
            return;
        try {
            GitHub gitHub = GitHub.connectUsingOAuth(Info.GITHUB_TOKEN);
            GHRepository repository = gitHub.getOrganization("Rubicon-Bot").getRepository("Rubicon");
            GHIssue Issue = repository.createIssue(titel.getTitle()).body(Header + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator() + Sufix + event.getMessage().getContentDisplay()).label("Enhancement").label("Up for grabs").create();
            channelMsg.remove(event.getTextChannel());
            event.getMessage().delete().queue();
            SafeMessage.sendMessage(event.getTextChannel(), new EmbedBuilder().setTitle("Feedback successfully send!").setDescription("Feedback is available at: " + Issue.getHtmlUrl()).build(), 20);
            timer.cancel();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private class FeedbackTitle {
        private final String title;
        private final User author;
        private final TextChannel channel;
        private final String message;


        private FeedbackTitle(String title, User author, TextChannel channel, String message) {
            this.title = title;
            this.author = author;
            this.channel = channel;
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public String getTitle() {
            return title;
        }

        public User getAuthor() {
            return author;
        }

        public TextChannel getChannel() {
            return channel;
        }
    }
}

