package fun.rubicon.commands.general;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.EmbedUtil;
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
    private static HashMap<Long, ReportHolder> reportMap = new HashMap<>();

    public CommandGitBug() {
        super(new String[]{"bug", "bugreport"}, CommandCategory.GENERAL, new PermissionRequirements("command.bug", false, true), "Report an Bug", "<Bug title>");
    }

    private static final String ISSUE_HEADER = "<p><strong>Bugreport</strong><br><br><strong>Bug report by ";
    private static final String ISSUE_SUFFIX = " </strong><br><br><strong>Description</strong><br><br></p>";


    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        Message infoMessage = SafeMessage.sendMessageBlocking(parsedCommandInvocation.getTextChannel(), EmbedUtil.message(new EmbedBuilder().setTitle("Description....").setDescription("Please enter a short description.").setFooter("Will abort in 60 seconds.", null)));
        reportMap.put(parsedCommandInvocation.getAuthor().getIdLong(), new ReportHolder(
                parsedCommandInvocation.getTextChannel(),
                parsedCommandInvocation.getMessage().getContentDisplay().replace(parsedCommandInvocation.getPrefix() + parsedCommandInvocation.getCommandInvocation() + " ", ""),
                parsedCommandInvocation.getAuthor(),
                infoMessage
        ));
        return null;
    }

    public static void handle(MessageReceivedEvent event) {
        if (!reportMap.containsKey(event.getAuthor().getIdLong())) {
            return;
        }
        ReportHolder reportHolder = reportMap.get(event.getAuthor().getIdLong());
        if (event.getMessage().getContentDisplay().contains(reportHolder.title))
            return;
        if (!event.getTextChannel().getId().equals(reportHolder.textChannel.getId()))
            return;
        String description = event.getMessage().getContentDisplay();
        try {
            GitHub gitHub = GitHub.connectUsingOAuth(Info.GITHUB_TOKEN);
            GHRepository repository = gitHub.getOrganization("Rubicon-Bot").getRepository("Rubicon");
            GHIssue issue = repository.createIssue(reportHolder.title).body(ISSUE_HEADER + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator() + ISSUE_SUFFIX + description).label("Bug").label("Requires Testing").create();
            reportHolder.delete(issue.getHtmlUrl().toString());
            event.getMessage().delete().queue();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ReportHolder {
        private TextChannel textChannel;
        private String title;
        private User author;
        private Message infoMessage;

        private Timer timer;

        private ReportHolder(TextChannel textChannel, String title, User author, Message infoMessage) {
            this.textChannel = textChannel;
            this.title = title;
            this.author = author;
            this.infoMessage = infoMessage;

            //Abort
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    infoMessage.delete().queue();
                    SafeMessage.sendMessage(textChannel, EmbedUtil.message(EmbedUtil.error("Aborted!", "Aborted bug report.")));
                    reportMap.remove(author.getIdLong());
                }
            }, 60000);
        }

        private void delete(String link) {
            reportMap.remove(author.getIdLong());
            timer.cancel();
            SafeMessage.sendMessage(textChannel, EmbedUtil.message(EmbedUtil.success("Success!", "Successfully reported bug. [Your Report](" + link + ")")));
            infoMessage.delete().queue();
        }
    }
}

