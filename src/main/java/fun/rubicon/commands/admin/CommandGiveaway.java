package fun.rubicon.commands.admin;

import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.commands.tools.CommandVote;
import fun.rubicon.util.Colors;
import fun.rubicon.util.EmbedUtil;
import fun.rubicon.util.StringUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class CommandGiveaway extends Command implements Serializable {
    private static boolean running = false;
    private static String emote = "\ud83c\udfc6";
    public static ArrayList<String> voteMember = new ArrayList<>();

    public CommandGiveaway(String command, CommandCategory category) {
        super(command, category);
    }

    @Override
    protected void execute(String[] args, MessageReceivedEvent e) throws ParseException {
        MessageChannel channel = e.getTextChannel();
        if (args.length < 3) {
            sendUsageMessage();
            return;
        }
        switch (args[0]) {
            case "create":
                String voteargs = "";
                if (!StringUtil.isNumeric(args[1])) {
                    e.getTextChannel().sendMessage(EmbedUtil.error("Error!", "You have to use a valid minute argument!").build()).queue();
                    return;
                }
                for (int i = 2; i < args.length; i++) {
                    voteargs += args[i] + " ";
                }
                Message msg = channel.sendMessage(new EmbedBuilder()
                        .setAuthor("Giveaway by " + e.getMember().getEffectiveName(), null, e.getAuthor().getAvatarUrl())
                        .setTitle("Take Part with Reaction!")
                        .setColor(Colors.COLOR_NO_PERMISSION)
                        .setDescription(voteargs)
                        .build()).complete();
                msg.addReaction("\uD83C\uDFC6").queue();
                e.getMessage().delete().queue();
                running = true;
                int min = Integer.parseInt(args[1]);
                Timer timer = new Timer();
                String finalVoteargs = voteargs;
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        int rand = ThreadLocalRandom.current().nextInt(0, voteMember.size());
                        Member member = e.getGuild().getMemberById(voteMember.get(rand));
                        msg.getChannel().sendMessage(new EmbedBuilder().setAuthor(e.getAuthor().getName() + "'s Giveaway is over!", null, e.getAuthor().getAvatarUrl()).setColor(Colors.COLOR_NO_PERMISSION).setDescription(member.getAsMention() + " won the Following: ```\n" + finalVoteargs + "```").build()).queue();
                        msg.delete().queue();
                        running = false;
                    }
                }, 1000 * 60 * min);
                break;
            default:
                sendUsageMessage();
                break;
        }
    }

    @Override
    public String getDescription() {
        return "Create a simple Giveaway. Take part with Reaction!";
    }

    @Override
    public String getUsage() {
        return "giveaway create <runtime in minutes> <award>";
    }

    @Override
    public int getPermissionLevel() {
        return 2;
    }

    public static void handleReaction(MessageReactionAddEvent event) {
        String react = event.getReaction().getReactionEmote().getName();
        if (react.equals(emote)) {
            if (running == false) return;
            if (voteMember.contains(event.getMember().getUser().getId())) return;
            PrivateChannel pc = event.getMember().getUser().openPrivateChannel().complete();
            pc.sendMessage("Yaaaaaaaaaa. You Take part at the Giveaway").queue();
            voteMember.add(event.getMember().getUser().getId());
        }
    }
    


}
