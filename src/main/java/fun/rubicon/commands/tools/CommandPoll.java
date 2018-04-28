package fun.rubicon.commands.tools;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.core.entities.RubiconPoll;
import fun.rubicon.features.poll.PollManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import fun.rubicon.util.SafeMessage;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.exceptions.ErrorResponseException;

import java.awt.*;
import java.io.Serializable;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class CommandPoll extends CommandHandler implements Serializable {
    public static final long serialVersionUID = 13902;

    private static TextChannel channel;

    private PollManager pollManager = RubiconBot.getPollManager();

    private static final String[] EMOTI = ("\uD83C\uDF4F \uD83C\uDF4E \uD83C\uDF50 \uD83C\uDF4A \uD83C\uDF4B \uD83C\uDF4C \uD83C\uDF49 \uD83C\uDF47 \uD83C\uDF53 \uD83C\uDF48 \uD83C\uDF52 \uD83C\uDF51 \uD83C\uDF4D \uD83E\uDD5D " +
            "\uD83E\uDD51 \uD83C\uDF45 \uD83C\uDF46 \uD83E\uDD52 \uD83E\uDD55 \uD83C\uDF3D \uD83C\uDF36 \uD83E\uDD54 \uD83C\uDF60 \uD83C\uDF30 \uD83E\uDD5C \uD83C\uDF6F \uD83E\uDD50 \uD83C\uDF5E " +
            "\uD83E\uDD56 \uD83E\uDDC0 \uD83E\uDD5A \uD83C\uDF73 \uD83E\uDD53 \uD83E\uDD5E \uD83C\uDF64 \uD83C\uDF57 \uD83C\uDF56 \uD83C\uDF55 \uD83C\uDF2D \uD83C\uDF54 \uD83C\uDF5F \uD83E\uDD59 " +
            "\uD83C\uDF2E \uD83C\uDF2F \uD83E\uDD57 \uD83E\uDD58 \uD83C\uDF5D \uD83C\uDF5C \uD83C\uDF72 \uD83C\uDF65 \uD83C\uDF63 \uD83C\uDF71 \uD83C\uDF5B \uD83C\uDF5A \uD83C\uDF59 \uD83C\uDF58 " +
            "\uD83C\uDF62 \uD83C\uDF61 \uD83C\uDF67 \uD83C\uDF68 \uD83C\uDF66 \uD83C\uDF70 \uD83C\uDF82 \uD83C\uDF6E \uD83C\uDF6D \uD83C\uDF6C \uD83C\uDF6B \uD83C\uDF7F \uD83C\uDF69 \uD83C\uDF6A \uD83E\uDD5B " +
            "\uD83C\uDF75 \uD83C\uDF76 \uD83C\uDF7A \uD83C\uDF7B \uD83E\uDD42 \uD83C\uDF77 \uD83E\uDD43 \uD83C\uDF78 \uD83C\uDF79 \uD83C\uDF7E \uD83E\uDD44 \uD83C\uDF74 \uD83C\uDF7D").split(" ");

    private List<String> toAddEmojis = new ArrayList<>();

    public CommandPoll() {
        super(new String[]{"vote", "v", "poll"}, CommandCategory.TOOLS, new PermissionRequirements("vote", false, false), "Let your members vote for something.", "create <Title>|<Option1>|<Option2>|...\nvote <index of Option>\nstats\nclose");
    }


    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        Message message = parsedCommandInvocation.getMessage();
        String[] args = parsedCommandInvocation.getArgs();
        channel = message.getTextChannel();
        if (args.length < 1) {
            return createHelpMessage();
        }
        switch (args[0]) {
            case "create":
                String[] voteargs = message.getContentDisplay().split("\\|");
                if (voteargs.length < 2) {
                    return createHelpMessage();
                }
                createPoll(parsedCommandInvocation);
                break;
            case "v":
                votePoll(parsedCommandInvocation);
                break;
            case "stats":
                voteStats(parsedCommandInvocation);
                break;
            case "close":
                closeVote(parsedCommandInvocation);
                break;
        }

        return null;
    }

    private static class Poll implements Serializable {
        private String creator;
        private String heading;
        private List<String> answers;
        private HashMap<String, String> pollmsgs;
        private HashMap<String, Integer> votes;
        private String channel;
        private HashMap<String, Integer> reacts;

        private Poll(Member creator, String heading, List<String> answers, Message pollmsg, TextChannel channel) {
            this.creator = creator.getUser().getId();
            this.heading = heading;
            this.answers = answers;
            this.pollmsgs = new HashMap<>();
            this.votes = new HashMap<>();
            this.channel = channel.getId();
            this.reacts = new HashMap<>();

            this.pollmsgs.put(pollmsg.getId(), pollmsg.getTextChannel().getId());
        }

        Member getCreator(Guild guild) {
            return guild.getMemberById(creator);
        }

        User getCreatorUser() {
            return RubiconBot.getShardManager().getUserById(creator);
        }

        public String getHeading() {
            return heading;
        }

        public List<String> getAnswers() {
            return answers;
        }

        public boolean isPollmsg(String messageid) {
            return pollmsgs.containsKey(messageid);
        }

        public HashMap<String, Integer> getVotes() {
            return votes;
        }

        public List<Message> getPollMessages(Guild guild) {
            List<Message> messages = new ArrayList<>();
            Poll poll = this;
            poll.pollmsgs.forEach((m, c) -> messages.add(guild.getTextChannelById(c).getMessageById(m).complete()));
            return messages;
        }

        HashMap<String, Integer> getReacts() {
            return reacts;
        }
    }


    private EmbedBuilder getParsedPoll(RubiconPoll poll, Guild guild, CommandManager.ParsedCommandInvocation command) {

        StringBuilder ansSTR = new StringBuilder();
        final AtomicInteger count = new AtomicInteger();

        poll.getAnswers().forEach(s -> {
            long votescount = poll.getVotes().keySet().stream().filter(k -> poll.getVotes().get(k).equals(count.get() + 1)).count();
            ansSTR.append(EMOTI[count.get()]).append(" - ").append(count.get() + 1).append("  -  ").append(s).append("  -  Votes: `").append(votescount).append("` \n");
            count.addAndGet(1);
        });

        return new EmbedBuilder()
                .setAuthor(String.format(command.translate("pollembed.heading"), poll.getCreator(guild).getEffectiveName()), null, poll.getCreator(guild).getUser().getAvatarUrl())
                .setDescription(":pencil:   " + poll.getHeading() + "\n\n" + ansSTR.toString())
                .setFooter(command.translate("pollembed.footer"), null)
                .setColor(Color.CYAN);

    }

    public static EmbedBuilder getParsedPoll(RubiconPoll poll, Guild guild) {

        StringBuilder ansSTR = new StringBuilder();
        final AtomicInteger count = new AtomicInteger();

        poll.getAnswers().forEach(s -> {
            long votescount = poll.getVotes().keySet().stream().filter(k -> poll.getVotes().get(k).equals(count.get() + 1)).count();
            ansSTR.append(EMOTI[count.get()]).append(" - ").append(count.get() + 1).append("  -  ").append(s).append("  -  Votes: `").append(votescount).append("` \n");
            count.addAndGet(1);
        });

        return new EmbedBuilder()
                .setAuthor(String.format(RubiconBot.sGetTranslations().getDefaultTranslationLocale().getResourceBundle().getString("pollembed.heading"), poll.getCreator(guild).getEffectiveName()), null, poll.getCreator(guild).getUser().getAvatarUrl())
                .setDescription(":pencil:   " + poll.getHeading() + "\n\n" + ansSTR.toString())
                .setFooter(RubiconBot.sGetTranslations().getDefaultTranslationLocale().getResourceBundle().getString("pollembed.footer"), null)
                .setColor(Color.CYAN);

    }

    private static EmbedBuilder getParsedPoll(Poll poll, Guild guild, User user) {
        ResourceBundle locale = RubiconBot.sGetTranslations().getUserLocale(user).getResourceBundle();
        StringBuilder ansSTR = new StringBuilder();
        final AtomicInteger count = new AtomicInteger();

        poll.answers.forEach(s -> {
            long votescount = poll.votes.keySet().stream().filter(k -> poll.votes.get(k).equals(count.get() + 1)).count();
            ansSTR.append(EMOTI[count.get()]).append(" - ").append(count.get() + 1).append("  -  ").append(s).append("  -  Votes: `").append(votescount).append("` \n");
            count.addAndGet(1);
        });

        return new EmbedBuilder()
                .setAuthor(String.format(locale.getString("pollembed.heading"), poll.getCreator(guild).getEffectiveName()), null, poll.getCreator(guild).getUser().getAvatarUrl())
                .setDescription(":pencil:   " + poll.heading + "\n\n" + ansSTR.toString())
                .setFooter(locale.getString("pollembed.footer"), null)
                .setColor(Color.CYAN);

    }

    private void voteStats(CommandManager.ParsedCommandInvocation parsedCommandInvocation) {
        Message message = parsedCommandInvocation.getMessage();
        if (!pollManager.pollExists(message.getGuild())) {
            message.getTextChannel().sendMessage(EmbedUtil.error(parsedCommandInvocation.translate("command.poll.nopoll.title"), parsedCommandInvocation.translate("command.poll.nopoll.description")).build()).queue(msg -> msg.delete().queueAfter(10, TimeUnit.SECONDS));
            return;
        }
        RubiconPoll poll = pollManager.getPollByGuild(message.getGuild());
        Message pollmsg = channel.sendMessage(getParsedPoll(pollManager.getPollByGuild(message.getGuild()), message.getGuild(), parsedCommandInvocation).build()).complete();
        poll.getPollmsgs().put(pollmsg.getId(), pollmsg.getTextChannel().getId());
        poll.getReacts().keySet().forEach(r -> pollmsg.addReaction(r).queue());
        pollManager.replacePoll(poll, message.getGuild());
    }

    private void closeVote(CommandManager.ParsedCommandInvocation parsedCommandInvocation) {
        Message message = parsedCommandInvocation.getMessage();
        User author = message.getAuthor();
        if (!pollManager.pollExists(message.getGuild())) {
            message.getTextChannel().sendMessage(EmbedUtil.error(parsedCommandInvocation.translate("command.poll.nopoll.title"), parsedCommandInvocation.translate("command.poll.nopoll.description")).build()).queue(msg -> msg.delete().queueAfter(10, TimeUnit.SECONDS));
            return;
        }

        RubiconPoll poll = pollManager.getPollByGuild(message.getGuild());

        if (message.getAuthor().equals(poll.getCreator(message.getGuild()))) {
            message.getTextChannel().sendMessage(EmbedUtil.error(parsedCommandInvocation.translate("command.poll.close.noperms.title"), parsedCommandInvocation.translate("command.poll.close.noperms.description")).build()).queue(msg -> msg.delete().queueAfter(10, TimeUnit.SECONDS));
            return;
        }

        channel.sendMessage(getParsedPoll(poll, message.getGuild(), parsedCommandInvocation).build()).queue();
        message.getTextChannel().sendMessage(EmbedUtil.success(parsedCommandInvocation.translate("command.poll.close.closed.title"), String.format(parsedCommandInvocation.translate("command.poll.close.closed.description"), author.getAsMention())).build()).queue(msg -> msg.delete().queueAfter(10, TimeUnit.SECONDS));
        try {
            poll.getPollmsgs().forEach((m, c) -> {
                Message pollmsg = message.getGuild().getTextChannelById(c).getMessageById(m).complete();
                pollmsg.editMessage(getParsedPoll(poll, message.getGuild(), parsedCommandInvocation).build()).queue();
            });
        } catch (ErrorResponseException e) {
            //This is an empty Catch Block
        }
        try {
            poll.getPollMessages(parsedCommandInvocation.getMessage().getGuild()).forEach(m -> m.delete().queue());
        } catch (Exception ignored) {

        }
        poll.delete();
    }

    private void createPoll(CommandManager.ParsedCommandInvocation parsedCommandInvocation) {
        Message message = parsedCommandInvocation.getMessage();
        String[] args = parsedCommandInvocation.getArgs();
        if (pollManager.pollExists(message.getGuild())) {
            message.getTextChannel().sendMessage(EmbedUtil.error(parsedCommandInvocation.translate("command.poll.create.alreadyrunning.title"), parsedCommandInvocation.translate("command.poll.create.alreadyrunning.description")).build()).queue(msg -> msg.delete().queueAfter(10, TimeUnit.SECONDS));
            return;
        }

        String argsSTRG = String.join(" ", new ArrayList<>(Arrays.asList(args).subList(1, args.length)));
        List<String> content = Arrays.asList(argsSTRG.split("\\|"));
        String heading = content.get(0);
        List<String> answers = new ArrayList<>(content.subList(1, content.size()));


        Message pollmessage = channel.sendMessage(new EmbedBuilder().setDescription(parsedCommandInvocation.translate("command.poll.create.creating")).setColor(Color.cyan).build()).complete();

        HashMap<String, Integer> reactions = new HashMap<>();
        final AtomicInteger count = new AtomicInteger();
        toAddEmojis = new ArrayList<>(Arrays.asList(EMOTI));
        answers.forEach(a -> {
            reactions.put(toAddEmojis.get(0), count.get() + 1);
            toAddEmojis.remove(0);
            count.addAndGet(1);
        });
        RubiconPoll poll = RubiconPoll.createPoll(heading, answers, pollmessage, reactions).savePoll();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                pollmessage.editMessage(getParsedPoll(poll, message.getGuild(), parsedCommandInvocation).build()).complete();
                poll.getReacts().keySet().forEach(r -> pollmessage.addReaction(r).queue());
            }
        }, 500);
    }

    private void votePoll(CommandManager.ParsedCommandInvocation parsedCommandInvocation) {
        Message message = parsedCommandInvocation.getMessage();
        String[] args = parsedCommandInvocation.getArgs();
        if (!pollManager.pollExists(message.getGuild())) {
            message.getTextChannel().sendMessage(EmbedUtil.error(parsedCommandInvocation.translate("command.poll.nopoll.title"), parsedCommandInvocation.translate("command.poll.nopoll.description")).build()).queue(msg -> msg.delete().queueAfter(10, TimeUnit.SECONDS));
            return;
        }

        RubiconPoll poll = pollManager.getPollByGuild(message.getGuild());

        int vote;
        try {
            vote = Integer.parseInt(args[1]);
            if (vote > poll.getAnswers().size()) {
                throw new Exception();
            }
        } catch (Exception e) {
            message.getTextChannel().sendMessage(EmbedUtil.error(parsedCommandInvocation.translate("command.poll.vote.wronganswer.title"), parsedCommandInvocation.translate("command.poll.vote.wronganswer.description")).build()).queue(msg -> msg.delete().queueAfter(10, TimeUnit.SECONDS));
            return;
        }

        if (poll.getVotes().containsKey(message.getAuthor().getId())) {
            return;
        }

        poll.getVotes().put(message.getAuthor().getId(), vote);
        pollManager.replacePoll(poll, message.getGuild());
        SafeMessage.sendMessage((TextChannel) message.getAuthor().openPrivateChannel().complete(), String.format(parsedCommandInvocation.translate("command.poll.vote.voted"), vote));
        EmbedBuilder messageText = getParsedPoll(poll, message.getGuild(), parsedCommandInvocation);
        poll.updateMessages(message.getGuild(), messageText);
    }


}