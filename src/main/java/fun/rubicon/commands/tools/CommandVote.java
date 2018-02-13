package fun.rubicon.commands.tools;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageDeleteEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.exceptions.ErrorResponseException;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class CommandVote extends CommandHandler implements Serializable {
    public static final long serialVersionUID = 13902;

    private static TextChannel channel;

    private static HashMap<Guild, Poll> polls = new HashMap<>();

    private static final String[] EMOTI = ("\uD83C\uDF4F \uD83C\uDF4E \uD83C\uDF50 \uD83C\uDF4A \uD83C\uDF4B \uD83C\uDF4C \uD83C\uDF49 \uD83C\uDF47 \uD83C\uDF53 \uD83C\uDF48 \uD83C\uDF52 \uD83C\uDF51 \uD83C\uDF4D \uD83E\uDD5D " +
            "\uD83E\uDD51 \uD83C\uDF45 \uD83C\uDF46 \uD83E\uDD52 \uD83E\uDD55 \uD83C\uDF3D \uD83C\uDF36 \uD83E\uDD54 \uD83C\uDF60 \uD83C\uDF30 \uD83E\uDD5C \uD83C\uDF6F \uD83E\uDD50 \uD83C\uDF5E " +
            "\uD83E\uDD56 \uD83E\uDDC0 \uD83E\uDD5A \uD83C\uDF73 \uD83E\uDD53 \uD83E\uDD5E \uD83C\uDF64 \uD83C\uDF57 \uD83C\uDF56 \uD83C\uDF55 \uD83C\uDF2D \uD83C\uDF54 \uD83C\uDF5F \uD83E\uDD59 " +
            "\uD83C\uDF2E \uD83C\uDF2F \uD83E\uDD57 \uD83E\uDD58 \uD83C\uDF5D \uD83C\uDF5C \uD83C\uDF72 \uD83C\uDF65 \uD83C\uDF63 \uD83C\uDF71 \uD83C\uDF5B \uD83C\uDF5A \uD83C\uDF59 \uD83C\uDF58 " +
            "\uD83C\uDF62 \uD83C\uDF61 \uD83C\uDF67 \uD83C\uDF68 \uD83C\uDF66 \uD83C\uDF70 \uD83C\uDF82 \uD83C\uDF6E \uD83C\uDF6D \uD83C\uDF6C \uD83C\uDF6B \uD83C\uDF7F \uD83C\uDF69 \uD83C\uDF6A \uD83E\uDD5B " +
            "\uD83C\uDF75 \uD83C\uDF76 \uD83C\uDF7A \uD83C\uDF7B \uD83E\uDD42 \uD83C\uDF77 \uD83E\uDD43 \uD83C\uDF78 \uD83C\uDF79 \uD83C\uDF7E \uD83E\uDD44 \uD83C\uDF74 \uD83C\uDF7D").split(" ");

    private List<String> toAddEmojis = new ArrayList<>();

    public CommandVote() {
        super(new String[]{"vote", "v", "poll"}, CommandCategory.TOOLS, new PermissionRequirements("command.vote", false, true), "Polls", "create <Title>|<Option1>|<Option2>|...\nvote <index of Option>\nstats\nclose");
    }


    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        Message message = parsedCommandInvocation.getMessage();
        String[] args = parsedCommandInvocation.getArgs();
        channel = message.getTextChannel();
        if (args.length < 1) {
            return new MessageBuilder().setEmbed(EmbedUtil.info("Usage", "USAGE: \n`vote create <Title>|<Option1>|<Option2>|...  `  -  create a vote\n`vote vote <index of Option>  `  -  vote for a possibility\n`vote stats  `  -  get stats of a current vote\n`vote close  `  -  close a current vote").build()).build();
        }
        switch (args[0]) {
            case "create":
                String[] voteargs = message.getContentDisplay().split("\\|");
                if (voteargs.length < 2) {
                    return new MessageBuilder().setEmbed(EmbedUtil.info("Usage", "USAGE: \n`vote create <Title>|<Option1>|<Option2>|...  `  -  create a vote\n`vote vote <index of Option>  `  -  vote for a possibility\n`vote stats  `  -  get stats of a current vote\n`vote close  `  -  close a current vote").build()).build();
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

        polls.forEach((guild, poll) -> {
            File path = new File("data/votes");
            if (!path.exists())
                path.mkdirs();
            try {
                savePoll(message.getGuild());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
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
            poll.pollmsgs.forEach((m, c) -> {
                messages.add(guild.getTextChannelById(c).getMessageById(m).complete());
            });
            return messages;
        }

        HashMap<String, Integer> getReacts() {
            return reacts;
        }
    }


    private static EmbedBuilder getParsedPoll(Poll poll, Guild guild) {

        StringBuilder ansSTR = new StringBuilder();
        final AtomicInteger count = new AtomicInteger();

        poll.answers.forEach(s -> {
            long votescount = poll.votes.keySet().stream().filter(k -> poll.votes.get(k).equals(count.get() + 1)).count();
            ansSTR.append(EMOTI[count.get()] + " - " + (count.get() + 1) + "  -  " + s + "  -  Votes: `" + votescount + "` \n");
            count.addAndGet(1);
        });

        return new EmbedBuilder()
                .setAuthor(poll.getCreator(guild).getEffectiveName() + "'s poll", null, poll.getCreator(guild).getUser().getAvatarUrl())
                .setDescription(":pencil:   " + poll.heading + "\n\n" + ansSTR.toString())
                .setFooter("Enter: 'vote v <number>' or react to vote", null)
                .setColor(Color.CYAN);

    }

    private void voteStats(CommandManager.ParsedCommandInvocation parsedCommandInvocation) {
        Message message = parsedCommandInvocation.getMessage();
        if (!polls.containsKey(message.getGuild())) {
            message.getTextChannel().sendMessage(EmbedUtil.error("No poll", "There is currently no poll running on this guild").build()).queue(msg -> msg.delete().queueAfter(10, TimeUnit.SECONDS));
            return;
        }
        Poll poll = polls.get(message.getGuild());
        Message pollmsg = channel.sendMessage(getParsedPoll(polls.get(message.getGuild()), message.getGuild()).build()).complete();
        poll.pollmsgs.put(pollmsg.getId(), pollmsg.getTextChannel().getId());
        poll.reacts.keySet().forEach(r -> {
            pollmsg.addReaction(r).queue();
        });
        polls.replace(message.getGuild(), poll);
    }

    private void closeVote(CommandManager.ParsedCommandInvocation parsedCommandInvocation) {
        Message message = parsedCommandInvocation.getMessage();
        User author = message.getAuthor();
        if (!polls.containsKey(message.getGuild())) {
            message.getTextChannel().sendMessage(EmbedUtil.error("No poll", "There is currently no poll running on this guild").build()).queue(msg -> msg.delete().queueAfter(10, TimeUnit.SECONDS));
            return;
        }

        Poll poll = polls.get(message.getGuild());

        if (message.getAuthor().equals(poll.getCreator(message.getGuild()))) {
            message.getTextChannel().sendMessage(EmbedUtil.error("No permission", ":warning: Only the poll creator can close polls").build()).queue(msg -> msg.delete().queueAfter(10, TimeUnit.SECONDS));
            return;
        }

        polls.remove(message.getGuild());
        channel.sendMessage(getParsedPoll(poll, message.getGuild()).build()).queue();
        message.getTextChannel().sendMessage(EmbedUtil.success("Closed", "Poll was closed by " + message.getAuthor().getAsMention()).build()).queue(msg -> msg.delete().queueAfter(10, TimeUnit.SECONDS));
        try {
            poll.pollmsgs.forEach((m, c) -> {
                Message pollmsg = message.getGuild().getTextChannelById(c).getMessageById(m).complete();
                pollmsg.editMessage(getParsedPoll(poll, message.getGuild()).build()).queue();
            });
        } catch (ErrorResponseException e) {
            //This is an empty Catch Block
        }
        try {
            poll.getPollMessages(parsedCommandInvocation.getMessage().getGuild()).forEach(m -> {
                m.delete().queue();
            });
        } catch (Exception ignored) {

        }
        File file = new File("data/votes/" + message.getGuild().getId() + ".dat");
        if (file.exists())
            file.delete();
    }

    private void createPoll(CommandManager.ParsedCommandInvocation parsedCommandInvocation) {
        Message message = parsedCommandInvocation.getMessage();
        String[] args = parsedCommandInvocation.getArgs();
        if (polls.containsKey(message.getGuild())) {
            message.getTextChannel().sendMessage(EmbedUtil.error("Already running", "There is already a poll running on this guild").build()).queue(msg -> msg.delete().queueAfter(10, TimeUnit.SECONDS));
            return;
        }

        String argsSTRG = String.join(" ", new ArrayList<>(Arrays.asList(args).subList(1, args.length)));
        List<String> content = Arrays.asList(argsSTRG.split("\\|"));
        String heading = content.get(0);
        List<String> answers = new ArrayList<>(content.subList(1, content.size()));


        Message pollmessage = channel.sendMessage(new EmbedBuilder().setDescription("Creating poll...").setColor(Color.cyan).build()).complete();

        HashMap<String, Integer> reactions = new HashMap<>();
        final AtomicInteger count = new AtomicInteger();
        toAddEmojis = new ArrayList<String>(Arrays.asList(EMOTI));
        answers.forEach(a -> {
            reactions.put(toAddEmojis.get(0), count.get() + 1);
            toAddEmojis.remove(0);
            count.addAndGet(1);
        });
        Poll poll = new Poll(message.getMember(), heading, answers, pollmessage, message.getTextChannel());
        polls.put(message.getGuild(), poll);
        poll.getReacts().putAll(reactions);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                pollmessage.editMessage(getParsedPoll(poll, message.getGuild()).build()).complete();
                poll.reacts.keySet().forEach(r -> {
                    pollmessage.addReaction(r).queue();
                });
            }
        }, 500);
    }

    private void votePoll(CommandManager.ParsedCommandInvocation parsedCommandInvocation) {
        Message message = parsedCommandInvocation.getMessage();
        String[] args = parsedCommandInvocation.getArgs();
        if (!polls.containsKey(message.getGuild())) {
            message.getTextChannel().sendMessage(EmbedUtil.error("No poll", "There is currently no poll running on this guild").build()).queue(msg -> msg.delete().queueAfter(10, TimeUnit.SECONDS));
            return;
        }

        Poll poll = polls.get(message.getGuild());

        int vote;
        try {
            vote = Integer.parseInt(args[1]);
            if (vote > poll.answers.size()) {
                throw new Exception();
            }
        } catch (Exception e) {
            message.getTextChannel().sendMessage(EmbedUtil.error("Wrong answer", ":warning: You entered an wrong answer!").build()).queue(msg -> msg.delete().queueAfter(10, TimeUnit.SECONDS));
            return;
        }

        if (poll.votes.containsKey(message.getAuthor().getId())) {
            return;
        }

        poll.votes.put(message.getAuthor().getId(), vote);
        polls.replace(message.getGuild(), poll);
        message.getAuthor().openPrivateChannel().complete().sendMessage("You have successfully voted for option `" + args[1] + "`");
        poll.pollmsgs.forEach((m, c) -> {
            Message pollmsg = message.getGuild().getTextChannelById(c).getMessageById(m).complete();
            pollmsg.editMessage(getParsedPoll(poll, message.getGuild()).build()).queue();
        });
    }

    public static void reactVote(MessageReactionAddEvent event) {
        if (event.getUser().isBot() || !polls.containsKey(event.getGuild()))
            return;
        Poll poll = polls.get(event.getGuild());
        if (!poll.isPollmsg(event.getMessageId())) return;
        if (poll.votes.containsKey(event.getUser().getId())) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    event.getReaction().removeReaction(event.getUser()).queue();
                }
            }, 1000);
            return;
        }
        String emoji = event.getReaction().getReactionEmote().getName();

        poll.votes.put(event.getUser().getId(), poll.reacts.get(emoji));
        polls.replace(event.getGuild(), poll);

        poll.pollmsgs.forEach((m, c) -> {
            Message pollmsg = event.getGuild().getTextChannelById(c).getMessageById(m).complete();
            pollmsg.editMessage(getParsedPoll(poll, event.getGuild()).build()).queue();
        });

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                event.getReaction().removeReaction(event.getUser()).queue();
            }
        }, 1000);

        CommandVote.polls.keySet().forEach((guild) -> {
            File path = new File("data/votes");
            if (!path.exists())
                path.mkdirs();
            try {
                CommandVote.savePoll(event.getGuild());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    private static void savePoll(Guild guild) throws IOException {
        if (!polls.containsKey(guild)) {
            return;
        }

        String saveFile = "data/votes/" + guild.getId() + ".dat";
        Poll poll = polls.get(guild);

        FileOutputStream fos = new FileOutputStream(saveFile);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(poll);
        oos.close();
    }

    private static Poll getPoll(Guild guild) throws IOException, ClassNotFoundException {
        if (polls.containsKey(guild))
            return null;

        String saveFile = "data/votes/" + guild.getId() + ".dat";
        FileInputStream fis = new FileInputStream(saveFile);
        ObjectInputStream ois = new ObjectInputStream(fis);
        Poll out = (Poll) ois.readObject();
        ois.close();
        return out;
    }

    public static void loadPolls(JDA jda) {
        jda.getGuilds().forEach(g -> {

            String saveFile = "data/votes/" + g.getId() + ".dat";
            File f = new File(saveFile);
            if (f.exists())
                try {
                    polls.put(g, getPoll(g));
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
        });
    }

    public static void handleMessageDeletion(MessageDeleteEvent event) {
        try {
            if (!polls.containsKey(event.getGuild())) return;
            Poll poll = getPoll(event.getGuild());
            if (!poll.isPollmsg(event.getMessageId())) return;
            poll.pollmsgs.remove(event.getMessageId());
            polls.replace(event.getGuild(), poll);
            savePoll(event.getGuild());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NullPointerException ignored) {

        }
    }
}