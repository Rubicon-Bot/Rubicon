package fun.rubicon.commands.general;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.core.entities.RubiconUser;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import fun.rubicon.util.SafeMessage;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static fun.rubicon.util.EmbedUtil.message;

/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */
public class CommandYouTube extends CommandHandler {

    private static HashMap<TextChannel, Announce> announceHashMap = new HashMap<>();
    private static Timer timer = new Timer();


    public CommandYouTube() {
        super(new String[]{"youtube"}, CommandCategory.GENERAL, new PermissionRequirements("youtube", false, false), "Announce your newest YouTube Videos!", "<Message for new Video (%url% = Video,%channel% for Youtube Channel name)>");

    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        if (invocation.getArgs().length < 1)
            return message(EmbedUtil.error("Invalid parameters", "Use `rc!help youtube` for more info!"));
        Announce announce = new Announce(invocation.getArgsString(), 0, "", invocation.getAuthor());
        announceHashMap.put(invocation.getTextChannel(), announce);
        SafeMessage.sendMessage(invocation.getTextChannel(), new EmbedBuilder().setTitle("Set Announce Channel").setDescription("Please Mention the Channel where the Notifications should be sent").setFooter("Will abort in 30sec.", null).build(), 30);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                announceHashMap.remove(invocation.getTextChannel());
                invocation.getTextChannel().sendMessage("Setup abort").queue(message -> message.delete().queueAfter(7L, TimeUnit.SECONDS));
            }
        }, 30000);
        return null;
    }

    public static void handle(MessageReceivedEvent event) {
        if (!announceHashMap.containsKey(event.getTextChannel()))
            return;
        Announce announce = announceHashMap.get(event.getTextChannel());
        if (event.getMessage().getContentDisplay().startsWith("rc!"))
            return;
        if (event.getAuthor().equals(RubiconBot.getSelfUser()))
            return;
        if (!event.getAuthor().equals(announce.getAuthor()))
            return;
        if (announce.getTextchannel() == 0) {
            if (event.getMessage().getMentionedChannels().size() < 1)
                return;
            if (!RubiconUser.fromUser(event.getAuthor()).isPremium()) {
                message(EmbedUtil.error("No Premium", "Sorry, but you have no Premium."));
                return;
            }
            announce.setTextchannel(event.getMessage().getMentionedChannels().get(0).getIdLong());
            SafeMessage.sendMessage(event.getTextChannel(), "Ok Channel is set! Please now send the YouTube ChannelID!");
        } else if (announce.getYoutubechannel().equals("")) {
            if (!RubiconUser.fromUser(event.getAuthor()).isPremium()) {
                message(EmbedUtil.error("No Premium", "Sorry, but you have no Premium."));
                return;
            }
            announce.setYoutubechannel(event.getMessage().getContentDisplay());
            timer.cancel();
            announce.fini();
            announce.save();

        }

    }

    private class Announce {
        private String message;
        private long textchannel;
        private String youtubechannel;
        private User author;
        private boolean allset = false;

        public void fini() {
            allset = true;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public long getTextchannel() {
            return textchannel;
        }

        public void setTextchannel(long textchannel) {
            this.textchannel = textchannel;
        }

        public String getYoutubechannel() {
            return youtubechannel;
        }

        public void setYoutubechannel(String youtubechannel) {
            this.youtubechannel = youtubechannel;
        }

        public User getAuthor() {
            return author;
        }

        public void setAuthor(User author) {
            this.author = author;
        }

        public void save() {
            if (!allset)
                throw new IllegalStateException("Not all Set!");
            try {
                PreparedStatement ps = RubiconBot.getMySQL().getConnection().prepareStatement("INSERT INTO `youtube` (" +
                        "`serverid`," +
                        "`youmsg`," +
                        "`youchannel`," +
                        "`youcreator`)" +
                        "VALUES (?,?,?,?)");
                ps.setLong(1, RubiconBot.getShardManager().getGuildById(getTextchannel()).getIdLong());
                ps.setString(2, getMessage());
                ps.setLong(3, getTextchannel());
                ps.setString(4, getYoutubechannel());
                ps.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

        public Announce(String message, long textchannel, String youtubechannel, User author) {
            this.author = author;
            this.message = message;
            this.textchannel = textchannel;
            this.youtubechannel = youtubechannel;
        }


    }

}
//Old Method
/*switch (invocation.getArgs()[0]) {
            case "message":
                if (!RubiconUser.fromUser(invocation.getAuthor()).isPremium())
                    return message(EmbedUtil.error("No Premium", "Sorry, but you have no Premium."));
                if (!userPermissions.hasPermissionNode("youtube.message"))
                    return message(EmbedUtil.no_permissions("youtube.message"));
                if (RubiconBot.getMySQL() != null) {
                    if (invocation.getArgs().length < 3)
                        return message(EmbedUtil.error("Invalid parameters", "Use `rc!help youtube` for more info!"));
                    try {
                        String message = invocation.getArgsString().replace(invocation.getArgs()[0], "");
                        PreparedStatement ps = RubiconBot.getMySQL().prepareStatement("UPDATE `guilds` SET `youmsg`=? WHERE `serverid`=?");
                        ps.setString(1, message);
                        ps.setLong(2, invocation.getGuild().getIdLong());
                        ps.execute();
                        return message(EmbedUtil.success("Successfully set Message", "Message was set to `" + message + "`"));
                    } catch (SQLException | NullPointerException e) {
                        e.printStackTrace();
                        return message(EmbedUtil.error());
                    }
                }
                break;
            case "channel":
                if (!RubiconUser.fromUser(invocation.getAuthor()).isPremium())
                    return message(EmbedUtil.error("No Premium", "Sorry, but you have no Premium."));
                if (!userPermissions.hasPermissionNode("youtube.channel"))
                    return message(EmbedUtil.no_permissions("youtube.channel"));
                if (RubiconBot.getMySQL() != null) {
                    if (invocation.getMessage().getMentionedChannels().size() < 1)
                        return message(EmbedUtil.error("Invalid parameters", "Use `rc!help youtube` for more info!"));
                    try {
                        PreparedStatement ps = RubiconBot.getMySQL().prepareStatement("UPDATE `guilds` SET `youchannel`=? WHERE `serverid`=?");
                        ps.setLong(1, invocation.getMessage().getMentionedChannels().get(0).getIdLong());
                        ps.setLong(2, invocation.getGuild().getIdLong());
                        ps.execute();
                        return message(EmbedUtil.success("Successfully set Channel", "Channel set to " + invocation.getMessage().getMentionedChannels().get(0).getAsMention()));
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }*/

