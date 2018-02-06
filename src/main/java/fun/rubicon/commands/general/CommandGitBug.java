package fun.rubicon.commands.general;


import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.data.PermissionLevel;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
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
 * Rubicon Discord bot
 *
 * @author Leon Kappes / Lee
 * @copyright Rubicon Dev Team 2018
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.commands.general
 */
public class CommandGitBug extends CommandHandler {
    public static HashMap<TextChannel, Titel> channelMsg = new HashMap<>();

    public CommandGitBug() {
        super(new String[]{"bug","bugreport"}, CommandCategory.GENERAL, new PermissionRequirements(PermissionLevel.EVERYONE, "command.gitbug"), "Report an Bug", "<Bug title>");
    }

    private static String Header = "<p><strong>Issue</strong><br><br><strong>Issue Type</strong><br> - [x] Bug <br> - [ ] Feature <br><br><br><br><strong>Report</strong><br><br><strong>Description</strong><br><br></p>";

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        String title = parsedCommandInvocation.getMessage().getContentDisplay().replace(parsedCommandInvocation.getPrefix() + parsedCommandInvocation.getCommandInvocation(), "");
        Titel tite1 = new Titel(title, parsedCommandInvocation.getAuthor(), parsedCommandInvocation.getTextChannel());
        channelMsg.put(parsedCommandInvocation.getTextChannel(), tite1);
        SafeMessage.sendMessage(parsedCommandInvocation.getTextChannel(), new EmbedBuilder().setTitle("Set Bug Description").setDescription("Please write a short Description about the Bog in this Channel").setFooter("Will abort in 30sec.", null).build(), 30);

        return null;
    }

    public static void handle(MessageReceivedEvent event) {
        if (!channelMsg.containsKey(event.getTextChannel()))
            return;
        if (event.getMessage().getContentDisplay().startsWith("rc!gitbug"))
            return;
        if (event.getAuthor().equals(RubiconBot.getJDA().getSelfUser()))
            return;
        Titel titel = channelMsg.get(event.getTextChannel());
        if (!event.getAuthor().equals(titel.getAuthor()))
            return;
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                channelMsg.remove(event.getTextChannel());
                titel.getChannel().sendMessage("Setup abort").queue(message -> {
                    message.delete().queueAfter(5L, TimeUnit.SECONDS);
                });
                return;
            }
        }, 30000);
        try {
            GitHub gitHub = GitHub.connectUsingOAuth(Info.GITHUB_TOKEN);
            GHRepository repository = gitHub.getOrganization("Rubicon-Bot").getRepository("Rubicon");
            GHIssue Issue = repository.createIssue(titel.getTitle()).body(Header + event.getMessage().getContentDisplay()).label("Bug").label("Requires Testing").create();
            channelMsg.remove(event.getTextChannel());
            event.getMessage().delete().queue();
            SafeMessage.sendMessage(event.getTextChannel(), new EmbedBuilder().setTitle("Bug successfully send!").setDescription("Bug is available at: " + Issue.getHtmlUrl()).build(), 20);
            timer.cancel();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class Titel {
        private final String title;
        private final User author;
        private final TextChannel channel;


        private Titel(String title, User author, TextChannel channel) {
            this.title = title;
            this.author = author;
            this.channel = channel;
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

