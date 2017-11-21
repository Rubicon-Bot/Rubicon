package fun.rubicon.commands.admin;

import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.commands.tools.CommandVote;
import fun.rubicon.util.Colors;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.exceptions.ErrorResponseException;

import java.awt.*;
import java.io.*;
import java.text.ParseException;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CommandGiveaway extends Command implements Serializable{
    private static TextChannel channel;

    public static HashMap<Guild, Giv> polls = new HashMap<>();


    public CommandGiveaway(String command, CommandCategory category) {
        super(command, category);
    }

    @Override
    protected void execute(String[] args, MessageReceivedEvent e) throws ParseException {

    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getUsage() {
        return null;
    }

    @Override
    public int getPermissionLevel() {
        return 0;
    }
    private static class Giv implements Serializable{
        private String creator;
        private String heading;
        private String msg;
        private HashMap<String, Integer> votes;
        private String channel;
        private HashMap<String, Integer> reacts;

        private Giv(Member creator, String heading, List<String> answers, Message pollmsg, TextChannel channel){
            this.creator = creator.getUser().getId();
            this.heading = heading;
            this.msg = pollmsg.getId();
            this.votes = new HashMap<>();
            this.channel = channel.getId();
            this.reacts = new HashMap<>();
        }
        public Member getCreator(Guild guild) {
            return guild.getMemberById(creator);
        }

        public String getHeading() {
            return heading;
        }

        public String getPollmsg() {
            return msg;
        }

        public HashMap<String, Integer> getVotes() {
            return votes;
        }

        public HashMap<String, Integer> getReacts() {
            return reacts;
        }


    }



    private void closeVote(MessageReceivedEvent event){
        Message message = event.getMessage();
        User author = event.getAuthor();
        if(!polls.containsKey(event.getGuild())){
            sendErrorMessage("There is currently no poll running on this guild");
            return;
        }

        Giv poll = polls.get(event.getGuild());

        if(e.getAuthor().equals(poll.getCreator(e.getGuild()))){
            sendErrorMessage(":warning: Only the poll creator can close polls");
            return;
        }

        polls.remove(event.getGuild());
        sendEmbededMessage(":white_check_mark: Poll was closed by" + event.getAuthor().getAsMention());
        Message pollmsg = channel.getMessageById(String.valueOf(poll.msg)).complete();
        try {
            pollmsg.delete().queue();
        } catch (ErrorResponseException e){
            //This is an empty Catch Block
        }
    }

    private void createPoll(String[] args, MessageReceivedEvent event){
        if(polls.containsKey(event.getGuild())){
            sendErrorMessage("There is already a poll running on this guild");
            return;
        }
        Message message = event.getMessage();
        User author = event.getAuthor();


        String argsSTRG = String.join(" ", new ArrayList<>(Arrays.asList(args).subList(1, args.length)));
        List<String> content = Arrays.asList(argsSTRG.split("\\|"));
        String heading = content.get(0);
        List<String> answers = new ArrayList<>(content.subList(1, content.size()));


        Message pollmessage = channel.sendMessage(new EmbedBuilder().setDescription("Creating poll...").setColor(Color.cyan).build()).complete();

        HashMap<String, Integer> reactions = new HashMap<>();
        final AtomicInteger count = new AtomicInteger();
        toAddEmojis = new ArrayList<String>(Arrays.asList(EMOTI));
        answers.forEach(a ->{
            reactions.put(toAddEmojis.get(0), count.get() + 1);
            toAddEmojis.remove(0);
            count.addAndGet(1);
        });
        CommandVote.Poll poll = new CommandVote.Poll(event.getMember(), heading, answers, pollmessage, e.getTextChannel());
        polls.put(event.getGuild(), poll);
        poll.getReacts().putAll(reactions);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                pollmessage.editMessage(getParsedPoll(poll, e.getGuild()).build()).complete();
                poll.reacts.keySet().forEach(r -> {
                    pollmessage.addReaction(r).queue();
                });
            }
        }, 500);
    }

    private void votePoll(String[] args, MessageReceivedEvent event){

        if(!polls.containsKey(event.getGuild())){
            sendErrorMessage("There is currently no poll running on this guild");
            return;
        }

        CommandVote.Poll poll = polls.get(event.getGuild());

        int vote;
        try{
            vote = Integer.parseInt(args[1]);
            if(vote > poll.answers.size()){
                throw new Exception();
            }
        } catch (Exception e){
            sendErrorMessage(":warning: You entered an wrong answer!");
            return;
        }

        if(poll.votes.containsKey(event.getAuthor().getId())){
            sendErrorMessage("Sorry, but you can only vote at once for a poll");
            return;
        }

        poll.votes.put(event.getAuthor().getId(), vote);
        polls.replace(event.getGuild(), poll);
        event.getAuthor().openPrivateChannel().complete().sendMessage("You have successfully voted for option `" + args[1] + "`");
        Message pollmsg =  channel.getMessageById(String.valueOf(poll.pollmsg)).complete();
        pollmsg.editMessage(getParsedPoll(poll, event.getGuild()).build()).queue();
    }

    public static void reactVote(MessageReactionAddEvent event){
        if(event.getUser().isBot() || !polls.containsKey(event.getGuild()))
            return;
        Giv poll = polls.get(event.getGuild());

        if(!poll.msg.equals(event.getMessageId()))
            return;

        if(poll.votes.containsKey(event.getUser().getId())){
            channel.sendMessage(new EmbedBuilder().setColor(Colors.COLOR_ERROR).setDescription("Sorry, but you can only vote at once for a poll").build()).queue();
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    event.getReaction().removeReaction(event.getUser()).queue();
                }
            }, 1000);
            return;
        }
        String emoji = event.getReaction().getEmote().getName();

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
        if(!polls.containsKey(guild)){
            return;
        }

        String saveFile = "SERVER_SETTINGS/" + guild.getId() + "/giv.dat";
        Giv poll = polls.get(guild);

        FileOutputStream fos = new FileOutputStream(saveFile);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(poll);
        oos.close();
    }
    private static Giv getPoll(Guild guild) throws IOException, ClassNotFoundException {
        if(polls.containsKey(guild))
            return null;

        String saveFile = "SERVER_SETTINGS/" + guild.getId() + "/giv.dat";
        FileInputStream fis = new FileInputStream(saveFile);
        ObjectInputStream ois = new ObjectInputStream(fis);
        Giv out = (Giv) ois.readObject();
        ois.close();
        return out;
    }

    public static void loadPolls(JDA jda){
        jda.getGuilds().forEach(g ->{

            File f = new File("SERVER_SETTINGS/" + g.getId() + "/giv.dat");
            if(f.exists())
                try {
                    polls.put(g, getPoll(g));
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }

        });
    }
}
