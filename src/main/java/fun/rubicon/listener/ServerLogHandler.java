package fun.rubicon.listener;

import fun.rubicon.RubiconBot;
import fun.rubicon.command2.CommandManager;
import fun.rubicon.util.Colors;
import fun.rubicon.util.Logger;
import fun.rubicon.util.MySQL;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.guild.GuildBanEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class ServerLogHandler extends ListenerAdapter {

    private Color evJoinColor = new Color(49, 133, 224);
    private Color evLeaveColor = new Color(198, 224, 49);
    private Color evBanColor = new Color(224, 67, 0);
    private Color evVoiceLog = new Color(120, 68, 234);
    private static Color evCommandLog = new Color(165, 100, 24);

    public static ArrayList<Long> bannedUsers = new ArrayList<>();

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        if (!isEventEnabled(event.getGuild(), LogEventKeys.JOIN))
            return;
        TextChannel textChannel = getLogChannel(event.getGuild());
        if (textChannel == null)
            return;

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setAuthor("New member joined the server", null, event.getMember().getUser().getAvatarUrl());
        embedBuilder.setDescription("**" + event.getMember().getEffectiveName() + " (" + event.getMember().getUser().getId() + ")** joined the server");
        embedBuilder.setColor(evJoinColor);
        sendLog(textChannel, embedBuilder);
    }

    @Override
    public void onGuildMemberLeave(GuildMemberLeaveEvent event) {
        if (bannedUsers.contains(event.getUser().getIdLong())) {
            return;
        }
        if (!isEventEnabled(event.getGuild(), LogEventKeys.BAN))
            return;
        TextChannel textChannel = getLogChannel(event.getGuild());
        if (textChannel == null)
            return;

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setAuthor("A member left the server", null, event.getMember().getUser().getAvatarUrl());
        embedBuilder.setDescription("**" + event.getMember().getEffectiveName() + " (" + event.getMember().getUser().getId() + ")** left the server");
        embedBuilder.setColor(evLeaveColor);
        sendLog(textChannel, embedBuilder);
    }

    @Override
    public void onGuildBan(GuildBanEvent event) {
        if (!isEventEnabled(event.getGuild(), LogEventKeys.BAN))
            return;
        TextChannel textChannel = getLogChannel(event.getGuild());
        if (textChannel == null)
            return;
        bannedUsers.add(event.getUser().getIdLong());

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setAuthor("A member was banned", null, event.getUser().getAvatarUrl());
        embedBuilder.setDescription("**" + event.getUser().getName() + " (" + event.getUser().getId() + ")** was banned from the server");
        embedBuilder.setColor(evBanColor);
        sendLog(textChannel, embedBuilder);
    }

    @Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
        if (!isEventEnabled(event.getGuild(), LogEventKeys.VOICE))
            return;
        TextChannel textChannel = getLogChannel(event.getGuild());
        if (textChannel == null)
            return;

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setAuthor("A member created voice connection", null, event.getMember().getUser().getAvatarUrl());
        embedBuilder.setDescription("**" + event.getMember().getEffectiveName() + " (" + event.getMember().getUser().getId() + ")** joined `" + event.getChannelJoined().getName() + "`");
        embedBuilder.setColor(evVoiceLog);
        sendLog(textChannel, embedBuilder);
    }

    @Override
    public void onGuildVoiceMove(GuildVoiceMoveEvent event) {
        if (!isEventEnabled(event.getGuild(), LogEventKeys.VOICE))
            return;
        TextChannel textChannel = getLogChannel(event.getGuild());
        if (textChannel == null)
            return;

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setAuthor("A member changed the channel", null, event.getMember().getUser().getAvatarUrl());
        embedBuilder.setDescription("**" + event.getMember().getEffectiveName() + " (" + event.getMember().getUser().getId() + ")** went from `" + event.getChannelLeft().getName() + "` to `" + event.getChannelJoined().getName() + "`");
        embedBuilder.setColor(evVoiceLog);
        sendLog(textChannel, embedBuilder);
    }

    @Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
        if (!isEventEnabled(event.getGuild(), LogEventKeys.VOICE))
            return;
        TextChannel textChannel = getLogChannel(event.getGuild());
        if (textChannel == null)
            return;

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setAuthor("A member closed voice connection", null, event.getMember().getUser().getAvatarUrl());
        embedBuilder.setDescription("**" + event.getMember().getEffectiveName() + " (" + event.getMember().getUser().getId() + ")** left `" + event.getChannelLeft().getName() + "`");
        embedBuilder.setColor(evVoiceLog);
        sendLog(textChannel, embedBuilder);
    }

    public static void logCommand(CommandManager.ParsedCommandInvocation parsedCommandInvocation) {
        if (!isEventEnabled(parsedCommandInvocation.invocationMessage.getGuild(), LogEventKeys.COMMAND))
            return;
        TextChannel textChannel = getLogChannel(parsedCommandInvocation.invocationMessage.getGuild());
        if (textChannel == null)
            return;

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setAuthor("A command was executed", null);
        embedBuilder.setDescription("**" + parsedCommandInvocation.invocationMessage.getMember().getEffectiveName() + " (" + parsedCommandInvocation.invocationMessage.getMember().getUser().getId() + ")** executed `" + parsedCommandInvocation.serverPrefix + parsedCommandInvocation.invocationCommand + "`");
        embedBuilder.setColor(evCommandLog);
        sendLog(textChannel, embedBuilder);
    }

    public static void sendLog(TextChannel channel, EmbedBuilder builder) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
        builder.setFooter(simpleDateFormat.format(new Date()), null);
        channel.sendMessage(builder.build()).queue();
    }

    private static TextChannel getLogChannel(Guild guild) {
        String entry = new ServerLogSQL(guild).get("channel");
        if (entry == null)
            return null;
        if (entry.equals("0"))
            return null;
        TextChannel textChannel;
        try {
            textChannel = guild.getTextChannelById(entry);
            return textChannel;
        } catch (NullPointerException ignored) {
            //channel deleted or something
        }
        return null;
    }

    private static boolean isEventEnabled(Guild guild, LogEventKeys key) {
        String entry = new ServerLogSQL(guild).get(key.getKey());
        if (entry.equalsIgnoreCase("true"))
            return true;
        else
            return false;
    }

    public static class ServerLogSQL {

        private Guild guild;
        private Connection connection;
        private MySQL mySQL;

        public ServerLogSQL(Guild guild) {
            this.guild = guild;
            this.mySQL = RubiconBot.getMySQL();
            this.connection = RubiconBot.getMySQL().getConnection();
        }

        public String get(String type) {
            createDefaultEntryIfNotExist();
            try {
                if (connection.isClosed())
                    mySQL.connect();
                PreparedStatement ps = connection.prepareStatement("SELECT * FROM serverlog WHERE `guildid` = ?");
                ps.setString(1, guild.getId());
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    return rs.getString(type);
                }
            } catch (SQLException e) {
                Logger.error(e);
            }
            return null;
        }


        public void set(String type, String value) {
            createDefaultEntryIfNotExist();
            try {
                if (connection.isClosed())
                    mySQL.connect();
                PreparedStatement ps = connection.prepareStatement("UPDATE serverlog SET " + type + "=? WHERE guildid=?");
                ps.setString(1, value);
                ps.setString(2, guild.getId());
                ps.execute();
            } catch (SQLException e) {
                Logger.error(e);
            }
        }

        public void createDefaultEntryIfNotExist() {
            try {
                if (connection.isClosed())
                    mySQL.connect();
                PreparedStatement checkStatement = connection.prepareStatement("SELECT * FROM serverlog WHERE guildid = ?");
                checkStatement.setString(1, guild.getId());
                ResultSet checkResult = checkStatement.executeQuery();
                if (checkResult.next())
                    return;
                PreparedStatement ps = connection.prepareStatement("INSERT INTO serverlog (guildid, channel, ev_join, ev_leave, ev_command, ev_ban, ev_voice) VALUES (?, '0', 'false', 'false', 'false', 'false', 'false')");
                ps.setString(1, guild.getId());
                ps.execute();
            } catch (SQLException e) {
                Logger.error(e);
            }
        }
    }

    public enum LogEventKeys {
        JOIN("ev_join", "Join"),
        LEAVE("ev_leave", "Leave"),
        COMMAND("ev_command", "Command"),
        BAN("ev_ban", "Ban"),
        VOICE("ev_voice", "Voice");


        private String key;
        private String displayname;

        LogEventKeys(String key, String displayname) {
            this.key = key;
            this.displayname = displayname;
        }

        public String getKey() {
            return key;
        }

        public String getDisplayname() {
            return displayname;
        }

        public static List<LogEventKeys> getAllKeys() {
            List<LogEventKeys> list = new ArrayList<>();
            list.add(JOIN);
            list.add(LEAVE);
            list.add(COMMAND);
            list.add(BAN);
            list.add(VOICE);
            return list;
        }
    }
}
