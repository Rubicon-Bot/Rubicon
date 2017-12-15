/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.tools;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command2.CommandHandler;
import fun.rubicon.command2.CommandManager;
import fun.rubicon.data.PermissionLevel;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.exceptions.ErrorResponseException;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CommandVote extends CommandHandler implements Serializable {
    private static final long serialVersionUID = 7197077155306830990L;

    private static TextChannel channel;

    public static HashMap<Guild, Poll> polls = new HashMap<>();

    private static final String[] EMOTI = ("\uD83C\uDF4F \uD83C\uDF4E \uD83C\uDF50 \uD83C\uDF4A \uD83C\uDF4B \uD83C\uDF4C \uD83C\uDF49 \uD83C\uDF47 \uD83C\uDF53 \uD83C\uDF48 \uD83C\uDF52 \uD83C\uDF51 \uD83C\uDF4D \uD83E\uDD5D " +
            "\uD83E\uDD51 \uD83C\uDF45 \uD83C\uDF46 \uD83E\uDD52 \uD83E\uDD55 \uD83C\uDF3D \uD83C\uDF36 \uD83E\uDD54 \uD83C\uDF60 \uD83C\uDF30 \uD83E\uDD5C \uD83C\uDF6F \uD83E\uDD50 \uD83C\uDF5E " +
            "\uD83E\uDD56 \uD83E\uDDC0 \uD83E\uDD5A \uD83C\uDF73 \uD83E\uDD53 \uD83E\uDD5E \uD83C\uDF64 \uD83C\uDF57 \uD83C\uDF56 \uD83C\uDF55 \uD83C\uDF2D \uD83C\uDF54 \uD83C\uDF5F \uD83E\uDD59 " +
            "\uD83C\uDF2E \uD83C\uDF2F \uD83E\uDD57 \uD83E\uDD58 \uD83C\uDF5D \uD83C\uDF5C \uD83C\uDF72 \uD83C\uDF65 \uD83C\uDF63 \uD83C\uDF71 \uD83C\uDF5B \uD83C\uDF5A \uD83C\uDF59 \uD83C\uDF58 " +
            "\uD83C\uDF62 \uD83C\uDF61 \uD83C\uDF67 \uD83C\uDF68 \uD83C\uDF66 \uD83C\uDF70 \uD83C\uDF82 \uD83C\uDF6E \uD83C\uDF6D \uD83C\uDF6C \uD83C\uDF6B \uD83C\uDF7F \uD83C\uDF69 \uD83C\uDF6A \uD83E\uDD5B " +
            "\uD83C\uDF75 \uD83C\uDF76 \uD83C\uDF7A \uD83C\uDF7B \uD83E\uDD42 \uD83C\uDF77 \uD83E\uDD43 \uD83C\uDF78 \uD83C\uDF79 \uD83C\uDF7E \uD83E\uDD44 \uD83C\uDF74 \uD83C\uDF7D").split(" ");

    private List<String> toAddEmojis = new ArrayList<>();

    public CommandVote(boolean disabled) {
        super(new String[]{"vote", "v"}, CommandCategory.TOOLS, new PermissionRequirements(PermissionLevel.EVERYONE, "command.vote"), "Create polls on your server", "create <question>|<answer1>|...", disabled);
    }


    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                savePolls();
            }
        }, 2500);
        String[] args = parsedCommandInvocation.args;
        Message message = parsedCommandInvocation.invocationMessage;
        channel = message.getTextChannel();
        if (args.length < 1) {
            return new MessageBuilder().setEmbed(EmbedUtil.info("Usage", "`vote create <Title>|<Option1>|<Option2>|...  `  -  create a vote\n" +
                    "`vote vote <index of Option>  `  -  vote for a possibility\n" +
                    "`vote stats  `  -  get stats of a current vote\n" +
                    "`vote close  `  -  close a current vote").build()).build();
        }
        if (args[0].equalsIgnoreCase("create")) {
            String[] voteArgs = message.getContent().split("\\|");
            if (voteArgs.length < 2) {
                return new MessageBuilder().setEmbed(EmbedUtil.info("Usage", "`vote create <Title>|<Option1>|<Option2>|...  `  -  create a vote\n" +
                        "`vote vote <index of Option>  `  -  vote for a possibility\n" +
                        "`vote stats  `  -  get stats of a current vote\n" +
                        "`vote close  `  -  close a current vote").build()).build();
            }
            return createPoll(args, message);
        } else if (args[0].equalsIgnoreCase("v")) {
            return votePoll(args, message);
        } else if (args[0].equalsIgnoreCase("stats")) {
            return voteStats(message);
        } else if (args[0].equalsIgnoreCase("close")) {
            return closeVote(message);
        }


        return new MessageBuilder().setEmbed(EmbedUtil.info("Usage", "`vote create <Title>|<Option1>|<Option2>|...  `  -  create a vote\n" +
                "`vote vote <index of Option>  `  -  vote for a possibility\n" +
                "`vote stats  `  -  get stats of a current vote\n" +
                "`vote close  `  -  close a current vote").build()).build();
    }


    private static class Poll implements Serializable {
        private static final long serialVersionUID = 7197077155306830990L;
        private String creator;
        private String heading;
        private List<String> answers;
        private String pollmsg;
        private HashMap<String, Integer> votes;
        private String channel;
        private HashMap<String, Integer> reacts;

        private Poll(Member creator, String heading, List<String> answers, Message pollmsg, TextChannel channel) {
            this.creator = creator.getUser().getId();
            this.heading = heading;
            this.answers = answers;
            this.pollmsg = pollmsg.getId();
            this.votes = new HashMap<>();
            this.channel = channel.getId();
            this.reacts = new HashMap<>();
        }

        public Member getCreator(Guild guild) {
            return guild.getMemberById(creator);
        }

        public String getPollmsg() {
            return pollmsg;
        }

        public HashMap<String, Integer> getReacts() {
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

    private Message voteStats(Message message) {
        Guild guild = message.getGuild();
        if (!polls.containsKey(guild)) {
            return new MessageBuilder().setEmbed(EmbedUtil.error("No poll running", "There is currently no poll running on this guild").build()).build();
        }
        return new MessageBuilder().setEmbed(getParsedPoll(polls.get(guild), guild).build()).build();
    }

    private Message closeVote(Message message) {
        Guild guild = message.getGuild();
        if (!polls.containsKey(guild)) {
            return new MessageBuilder().setEmbed(EmbedUtil.error("No poll running", "There is currently no poll running on this guild").build()).build();
        }

        Poll poll = polls.get(guild);

        if (message.getAuthor().equals(poll.getCreator(guild))) {
            return new MessageBuilder().setEmbed(EmbedUtil.error("Error", ":warning: Only the poll creator can close polls").build()).build();
        }

        polls.remove(guild);
        String file = "data/votes/" + guild.getId() + "/vote.dat";
        new File(file).delete();
        channel.sendMessage(getParsedPoll(poll, guild).build()).queue();
        Message pollmsg = channel.getMessageById(String.valueOf(poll.pollmsg)).complete();
        try {
            pollmsg.delete().queue();
        } catch (ErrorResponseException e) {
            //This is an empty Catch Block
        }
        return new MessageBuilder().setEmbed(EmbedUtil.success("Closed vote", "Poll was closed by " + message.getAuthor().getAsMention()).build()).build();
    }

    private Message createPoll(String[] args, Message message) {
        Guild guild = message.getGuild();
        if (polls.containsKey(guild)) {
            return new MessageBuilder().setEmbed(EmbedUtil.error("No poll running", "There is currently a open vote!").build()).build();
        }

        String argsSTRG = String.join(" ", new ArrayList<>(Arrays.asList(args).subList(1, args.length)));
        List<String> content = Arrays.asList(argsSTRG.split("\\|"));
        String heading = content.get(0);
        List<String> answers = new ArrayList<>(content.subList(1, content.size()));


        Message pollmessage = channel.sendMessage(new EmbedBuilder().setDescription("Creating poll...").setColor(Color.cyan).build()).complete();

        HashMap<String, Integer> reactions = new HashMap<>();
        final AtomicInteger count = new AtomicInteger();
        toAddEmojis = new ArrayList<>(Arrays.asList(EMOTI));
        answers.forEach(a -> {
            reactions.put(toAddEmojis.get(0), count.get() + 1);
            toAddEmojis.remove(0);
            count.addAndGet(1);
        });
        Poll poll = new Poll(message.getMember(), heading, answers, pollmessage, message.getTextChannel());
        polls.put(guild, poll);
        poll.getReacts().putAll(reactions);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                pollmessage.editMessage(getParsedPoll(poll, guild).build()).complete();
                poll.reacts.keySet().forEach(r -> pollmessage.addReaction(r).queue());
            }
        }, 500);

        return null;
    }

    private Message votePoll(String[] args, Message message) {
        Guild guild = message.getGuild();
        if (!polls.containsKey(guild)) {
            return new MessageBuilder().setEmbed(EmbedUtil.error("No poll running", "There is currently no poll running on this guild").build()).build();
        }

        Poll poll = polls.get(guild);

        int vote;
        try {
            vote = Integer.parseInt(args[1]);
            if (vote > poll.answers.size()) {
                throw new Exception();
            }
        } catch (Exception e) {
            return new MessageBuilder().setEmbed(EmbedUtil.error("Wrong answer", ":warning: You entered an wrong answer!").build()).build();
        }

        if (poll.votes.containsKey(message.getAuthor().getId())) {
            //return new MessageBuilder().setEmbed(EmbedUtil.error("Error", "Sorry, but you can only vote at once for a poll").build()).build();
            return null;
        }

        poll.votes.put(message.getAuthor().getId(), vote);
        polls.replace(guild, poll);
        message.getAuthor().openPrivateChannel().complete().sendMessage("You have successfully voted for option `" + args[1] + "`").queue();
        Message pollmsg = channel.getMessageById(String.valueOf(poll.pollmsg)).complete();
        pollmsg.editMessage(getParsedPoll(poll, guild).build()).queue();
        return null;
    }

    public static void reactVote(MessageReactionAddEvent event) {
        if (event.getUser().isBot() || !polls.containsKey(event.getGuild()))
            return;
        Poll poll = polls.get(event.getGuild());

        if (!poll.pollmsg.equals(event.getMessageId()))
            return;

        if (poll.votes.containsKey(event.getUser().getId())) {
            //channel.sendMessage(new EmbedBuilder().setColor(Colors.COLOR_ERROR).setDescription("Sorry, but you can only vote at once for a poll").build()).queue();
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

        Message pollmsg = event.getTextChannel().getMessageById(poll.getPollmsg()).complete();
        pollmsg.editMessage(getParsedPoll(poll, event.getGuild()).build()).queue();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                event.getReaction().removeReaction(event.getUser()).queue();
            }
        }, 1000);

    }

    private void savePoll(Guild guild) throws IOException {
        if (!polls.containsKey(guild)) {
            return;
        }

        String saveFile = "data/votes/" + guild.getId() + "/vote.dat";
        Poll poll = polls.get(guild);

        FileOutputStream fos = new FileOutputStream(saveFile);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(poll);
        oos.close();
    }

    private static Poll getPoll(Guild guild) throws IOException, ClassNotFoundException {
        if (polls.containsKey(guild))
            return null;

        String saveFile = "data/votes/" + guild.getId() + "/vote.dat";
        FileInputStream fis = new FileInputStream(saveFile);
        ObjectInputStream ois = new ObjectInputStream(fis);
        Poll out = (Poll) ois.readObject();
        ois.close();
        return out;
    }

    public static void loadPolls(JDA jda) {
        jda.getGuilds().forEach(g -> {

            File f = new File("data/votes/" + g.getId() + "/vote.dat");
            if (f.exists())
                try {
                    polls.put(g, getPoll(g));
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }

        });
    }

    private void savePolls() {
        polls.forEach((guild, poll) -> {
            new File("data/votes/").mkdirs();
            File path = new File("data/votes/" + guild.getId() + "/");
            if (!path.exists())
                path.mkdirs();
            try {
                savePoll(guild);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }

}
