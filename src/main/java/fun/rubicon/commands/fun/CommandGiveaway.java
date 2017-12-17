/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.fun;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.util.Colors;
import fun.rubicon.util.EmbedUtil;
import fun.rubicon.util.StringUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class CommandGiveaway extends Command implements Serializable {

    private static boolean running = false;
    private static String emote = "\ud83c\udfc6";
    private static HashMap<Guild, Giveaway> giveaways = new HashMap<>();
    static DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");

    public CommandGiveaway(String command, CommandCategory category) {
        super(command, category);
    }

    private static class Giveaway implements Serializable {
        String expiredate;
        String messageid;
        String reward;
        ArrayList<String> users;
        String guildid;
        String channelid;

        private Giveaway(Date expiry, Message message, String reward) {
            this.expiredate = format.format(expiry);
            this.messageid = message.getId();
            this.reward = reward;
            this.users = new ArrayList<>();
            this.guildid = message.getGuild().getId();
            this.channelid = message.getTextChannel().getId();
        }
    }

    @Override
    protected void execute(String[] args, MessageReceivedEvent e) {
        MessageChannel channel = e.getTextChannel();
        if (args.length < 3) {
            sendUsageMessage();
            return;
        }
        switch (args[0]) {
            case "create":
                String voteargs = "";
                if (giveaways.containsKey(e.getGuild())) {
                    e.getTextChannel().sendMessage(EmbedUtil.error("Already running", "There is already a giveaway running on this guild").build()).queue();
                    return;
                }
                if (!StringUtil.isNumeric(args[1])) {
                    e.getTextChannel().sendMessage(EmbedUtil.error("Error!", "You have to use a valid minute argument!").build()).queue();
                    return;
                }
                for (int i = 2; i < args.length; i++) {
                    voteargs += args[i] + " ";
                }
                Date now = new Date();
                Calendar cal = Calendar.getInstance();
                cal.setTime(now);
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
                cal.add(Calendar.MINUTE, min);
                Date expiry = cal.getTime();
                Giveaway giveaway = new Giveaway(expiry, msg, voteargs);
                giveaways.put(e.getGuild(), giveaway);
                saveGiveaway(giveaway);
                break;
            default:
                sendUsageMessage();
                break;
        }
    }

    private void saveGiveaway(Giveaway giveaway) {
        Guild guild = RubiconBot.getJDA().getGuildById(giveaway.guildid);
        String saveFile = "GIVEAWAYS/" + guild.getId() + "/giveaway.dat";
        String saveDir = "GIVEAWAYS/" + guild.getId();
        File folder = new File(saveDir);
        File file = new File(saveFile);
        try {
            if (!folder.exists())
                folder.mkdirs();
            if (!file.exists())
                file.createNewFile();
            FileOutputStream fos = new FileOutputStream(saveFile);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(giveaway);
            oos.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public static void loadGiveaways(List<Guild> guilds) {
        guilds.forEach(g -> {
            File folder = new File("GIVEAWAYS/" + g.getId());
            File file = new File("GIVEAWAYS/" + g.getId() + "/giveaway.dat");
            if (!folder.exists() || !file.exists())
                return;
            String saveFile = "GIVEAWAYS/" + g.getId() + "/giveaway.dat";
            try {
                FileInputStream fis = new FileInputStream(saveFile);
                ObjectInputStream ois = new ObjectInputStream(fis);
                Giveaway giveaway = (Giveaway) ois.readObject();
                ois.close();
                giveaways.put(g, giveaway);
            } catch (IOException | ClassNotFoundException e1) {
                e1.printStackTrace();
            }
        });
    }

    public static void startGiveawayManager(JDA jda) {
        loadGiveaways(jda.getGuilds());
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                giveaways.values().forEach(g -> {
                    Date now = new Date();
                    try {
                        Date date = format.parse(g.expiredate);
                        if (date.after(now)) {
                            TextChannel channel = RubiconBot.getJDA().getGuildById(g.guildid).getTextChannelById(g.channelid);

                            if (g.users.isEmpty()) {
                                channel.sendMessage(EmbedUtil.error("No winner c:", "Because nobody reacted nobdoy won the giveaway").build()).queue();
                                return;
                            }
                            int rand = ThreadLocalRandom.current().nextInt(0, g.users.size());
                            Member member = RubiconBot.getJDA().getGuildById(g.guildid).getMemberById(g.users.get(rand));
                            channel.sendMessage(new EmbedBuilder().setAuthor("Giveaway is over!", null, null).setColor(Colors.COLOR_NO_PERMISSION).setDescription(member.getAsMention() + " won the Following: ```\n" + g.reward + "```").build()).queue();
                            giveaways.remove(channel.getGuild());
                            File file = new File("GIVEAWAYS/" + channel.getGuild().getId() + "/giveaway.dat");
                            file.delete();
                        }
                    } catch (ParseException pe) {
                        pe.printStackTrace();
                    }


                });
            }
        }, 0, 50000);

    }

    @Override
    public String getDescription() {
        return "Create a simple Giveaway. Take part with Reaction!";
    }

    @Override
    public String getUsage() {
        return "create <runtime in minutes> <award>";
    }

    @Override
    public int getPermissionLevel() {
        return 2;
    }

    public static void handleReaction(MessageReactionAddEvent event) {
        String react = event.getReaction().getReactionEmote().getName();
        if (react.equals(emote)) {
            if (!running) return;
            Giveaway giveaway = giveaways.get(event.getGuild());
            if (giveaway.users.contains(event.getUser().getId())) return;
            PrivateChannel pc = event.getMember().getUser().openPrivateChannel().complete();
            pc.sendMessage("You Take part at the Giveaway at " + event.getGuild().getName()).queue();
            giveaway.users.add(event.getMember().getUser().getId());
        }
    }
}
