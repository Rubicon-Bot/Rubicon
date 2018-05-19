package fun.rubicon.core.entities;

import fun.rubicon.RubiconBot;
import fun.rubicon.io.deprecated_rethink.Rethink;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Michael Rittmeister / Schlaubi
 * @license GNU General Public License v3.0
 */
public class RubiconPoll implements Serializable {
    Rethink rethink = RubiconBot.getRethink();

    /*The id of the poll creator*/
    private String creator;
    /*The guild of the poll*/
    private String guild;
    /*The heading of the poll*/
    private String heading;
    /*List with all answers*/
    private List<String> answers;
    /*Hashmap with all pollmsgs and their channels*/
    private HashMap<String, String> pollmsgs;
    /* Hashmap with count of voted for every option*/
    private HashMap<String, String> votes;
    /*Hashmaps with all emotes and their vote options*/
    private HashMap<String, String> reacts;

    public String getCreator() {
        return creator;
    }

    public String getHeading() {
        return heading;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public HashMap<String, String> getPollmsgs() {
        return pollmsgs;
    }

    public HashMap<String, Integer> getVotes() {
        HashMap<String, Integer> map = new HashMap<>();
        votes.forEach((s, i) -> map.put(s, Integer.parseInt(i)));
        return map;
    }

    public HashMap<String, Integer> getReacts() {
        HashMap<String, Integer> map = new HashMap<>();
        reacts.forEach((s, i) -> map.put(s, Integer.parseInt(i)));
        return map;
    }

    public String getGuild() {
        return guild;
    }

    public void removePollMsg(String message) {
        pollmsgs.remove(message);
    }

    public boolean isPollmsg(String message) {
        return pollmsgs.containsKey(message);
    }

    public Member getCreator(Guild guild) {
        return guild.getMemberById(getCreator());
    }

    private RubiconPoll(Member creator, String heading, List<String> answers, Message message, Guild guild) {
        System.out.println(creator.getUser().getId() + "ID");
        this.creator = creator.getUser().getId();
        this.heading = heading;
        this.answers = answers;
        this.pollmsgs = new HashMap<>();
        this.votes = new HashMap<>();
        this.reacts = new HashMap<>();
        this.guild = guild.getId();

        this.pollmsgs.put(message.getId(), message.getTextChannel().getId());
    }

    public RubiconPoll(Member creator, String heading, List<String> answers, HashMap<String, String> pollmsgs, HashMap<String, Integer> votesInput, HashMap<String, Integer> reactsInput, Guild guild) {
        this.creator = creator.getUser().getId();
        this.heading = heading;
        this.answers = answers;
        this.pollmsgs = pollmsgs;
        this.votes = new HashMap<>();
        this.reacts = new HashMap<>();
        this.guild = guild.getId();
        votesInput.forEach((s, i) -> votes.put(s, String.valueOf(i)));
        reactsInput.forEach((s, i) -> reacts.put(s, String.valueOf(i)));

    }

    public RubiconPoll(String creator, String heading, List<String> answers, HashMap<String, String> pollmsgs, HashMap<String, String> votesInput, HashMap<String, String> reactsInput, Guild guild) {
        this.creator = creator;
        this.heading = heading;
        this.answers = answers;
        this.pollmsgs = pollmsgs;
        this.votes = votesInput;
        this.reacts = reactsInput;
        this.guild = guild.getId();

    }

    public static RubiconPoll createPoll(String heading, List<String> answers, Message message, HashMap<String, Integer> emotes, Member author) {
        RubiconPoll poll = new RubiconPoll(author, heading, answers, message, message.getGuild());
        emotes.forEach((s, i) -> poll.reacts.put(s, String.valueOf(i)));
        RubiconBot.getPollManager().getPolls().put(message.getGuild(), poll);
        return poll;
    }

    public RubiconPoll savePoll() {
        /*Delete old poll*/
        delete();
        System.out.println("OMI");
        System.out.println(votes);
        rethink.db.table("votes").insert(rethink.rethinkDB.array(rethink.rethinkDB.hashMap("creator", creator).with("heading", heading).with("answers", answers).with("pollmsgs", pollmsgs).with("votes", votes).with("reacts", reacts).with("guild", guild))).run(rethink.getConnection());
        return this;
    }

    public boolean delete() {
        rethink.db.table("votes").filter(rethink.rethinkDB.hashMap("guild", guild)).delete().run(rethink.getConnection());
        return true;
    }

    public void updateMessages(Guild guild, EmbedBuilder message) {
        getPollmsgs().forEach((m, c) -> {
            try {
                Message pollmsg = guild.getTextChannelById(c).getMessageById(m).complete();
                pollmsg.editMessage(message.build()).queue();
            } catch (Exception ignored) {
            }
        });
    }

    public List<Message> getPollMessages(Guild guild) {
        List<Message> messages = new ArrayList<>();
        this.pollmsgs.forEach((m, c) -> messages.add(guild.getTextChannelById(c).getMessageById(m).complete()));
        return messages;
    }

    public void addVote(Member member, Integer voteID){
        votes.put(member.getUser().getId(), String.valueOf(voteID));
    }

}