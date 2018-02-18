package fun.rubicon.features;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.command.UnavailableCommandHandler;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.sql.MySQL;
import fun.rubicon.util.Colors;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static fun.rubicon.util.EmbedUtil.*;

/**
 * Rubicon Discord bot
 *
 * @author Edited copy of fun.rubicon.features.GiveawayHandler (Leon Kappes / Lee)
 * @copyright Rubicon Dev Team 2018
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.features
 */
public class RemindHandler extends CommandHandler {

    private class Remind {
        private final long textChannelId;
        private final long messageId;
        private final String remindmsg;
        private final long remindtime;
        private final long authorId;
        private final TimerTask resolveTask = new TimerTask() {
            @Override
            public void run() {
                remind();
            }
        };
        private boolean open = true;


        /**
         * Constructs a Remind object from the given parameters, ensures its singularity and schedules the resolving.
         *
         * @param textChannelId the id of the text channel the remindme message was posted in.
         * @param messageId     the message's id.
         * @param remindmsg     the remind text that will be reminded to.
         * @param remindtime    the millis-date for the giveaway expiration.
         * @param authorId      the user id of the person who wants to be reminded away something.
         */

        private Remind(long textChannelId, long messageId, String remindmsg, long remindtime, long authorId) {
            this.textChannelId = textChannelId;
            this.messageId = messageId;
            this.remindmsg = remindmsg;
            this.remindtime = remindtime;
            this.authorId = authorId;

            // ensure there is not object of this giveaway yet to keep a single task
            if (reminders.contains(this))
                throw new IllegalStateException("Remind object already exists for the specified message");
            reminders.add(this);

            // schedule resolving
            RubiconBot.getTimer().schedule(resolveTask, new Date(remindtime));
        }


        private void remind() {
            if (!open)
                throw new IllegalStateException("Remind is not open any more");

            PrivateChannel pc = getAuthor().openPrivateChannel().complete();
            pc.sendMessage(new EmbedBuilder()
                    .setColor(Colors.COLOR_SECONDARY)
                    .setAuthor(getAuthor().getName(), "http://rubicon.fun", getAuthor().getAvatarUrl())
                    .setTitle("Hey, " + getAuthor().getName() + "!")
                    .setDescription("You wanted to do following: " +
                            "\n```fix" +
                            "\n" + remindmsg + "```")
                    .build()
            ).queue();


            delete();
        }


        public boolean save() {
            // check whether the giveaway was resolved yet
            if (!open)
                throw new IllegalStateException("Remind is not open any more");

            try {
                PreparedStatement insertStatement = MySQL.getConnection().prepareStatement("INSERT INTO `reminders-v1` (" +
                        "`textchannelid`, " +
                        "`messageid`," +
                        "`remindmsg`," +
                        "`remindtime`," +
                        "`authorid`) " +
                        "VALUES (?, ?, ?, ?, ?);");
                insertStatement.setLong(1, textChannelId);
                insertStatement.setLong(2, messageId);
                insertStatement.setString(3, remindmsg);
                insertStatement.setLong(4, remindtime);
                insertStatement.setLong(5, authorId);
                insertStatement.execute();
            } catch (SQLException e) {
                Logger.error("Could not save reminder '" + toString() + "'.");
                Logger.error(e);
                return false;
            }
            return true;
        }


        public void delete() {
            open = false;
            resolveTask.cancel();
            reminders.remove(this);
            try {
                PreparedStatement deleteStatement = MySQL.getConnection()
                        .prepareStatement("DELETE FROM `reminders-v1` WHERE `textchannelid` = ? AND `messageid` = ?;");
                deleteStatement.setLong(1, textChannelId);
                deleteStatement.setLong(2, messageId);
                deleteStatement.execute();
            } catch (SQLException e) {
                Logger.error("Could not delete reminder '" + toString() + "'.");
                Logger.error(e);
            }
        }

        public long getTextChannelId() {
            return textChannelId;
        }

        public long getMessageId() {
            return messageId;
        }

        public User getAuthor() {
            return RubiconBot.getJDA().getUserById(authorId);
        }


    }


    private Set<Remind> reminders = new HashSet<>();

    public RemindHandler() {
        super(new String[]{"remindme", "remind"}, CommandCategory.GENERAL, new PermissionRequirements("command.remindme", false, true), "Get reminded of whatever you want", "create 9d Buy Cheese - you will get Reminded in 9 Days\n" +
                "create 5w Buy another Cheese! - you will get Reminded in 5 Weeks\n" +
                "create 1m Get this Message! - you will get Reminded in 5 Minutes\n" +
                "create 12mon Lol new Year! - you will get Reminded in 12 Months");
        try {
            MySQL.getConnection().prepareStatement(
                    "CREATE TABLE IF NOT EXISTS `reminders-v1` (" +
                            "`textchannelid` BIGINT SIGNED, " +
                            "`messageid` BIGINT SIGNED, " +
                            "`remindmsg` VARCHAR(300), " +
                            "`remindtime` BIGINT SIGNED, " +
                            "`authorid` BIGINT SIGNED" +
                            ");")
                    .execute();
        } catch (SQLException e) {
            Logger.error("Could not create reminder table. Disabling giveaways.");
            Logger.error(e);
            RubiconBot.getCommandManager().registerCommandHandler(new UnavailableCommandHandler(this));
            return;
        }

        try {
            for (TextChannel channel : RubiconBot.getJDA().getTextChannels()) {
                PreparedStatement selectStatement = MySQL.getConnection()
                        .prepareStatement("SELECT * FROM `reminders-v1` WHERE `textchannelid` = ?;");
                selectStatement.setLong(1, channel.getIdLong());
                ResultSet channelResult = selectStatement.executeQuery();
                while (channelResult.next())
                    new Remind(channelResult.getLong("textchannelid"),
                            channelResult.getLong("messageid"),
                            channelResult.getString("remindmsg"),
                            channelResult.getLong("remindtime"),
                            channelResult.getLong("authorid"));
            }
        } catch (SQLException e) {
            Logger.error("Could not load reminders, disabling them.");
            Logger.error(e);
            RubiconBot.getCommandManager().registerCommandHandler(new UnavailableCommandHandler(this));
            return;
        }

        RubiconBot.getCommandManager().registerCommandHandler(this);
    }

    private Remind getRemindById(long textChannelId, long messageId) {
        for (Remind remind : reminders)
            if (remind.getTextChannelId() == textChannelId && remind.getMessageId() == messageId)
                return remind;
        return null;
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        if (invocation.getArgs().length < 1) {
            return createHelpMessage();
        } else {
            switch (invocation.getArgs()[0]) {
                case "create":
                    if (invocation.getArgs().length < 3)
                        return createHelpMessage(invocation);
                    TimeUnit timeunit = TimeUnit.MINUTES;
                    int runtime = 0;
                    double extra = 0;
                    try {
                        int time;
                        if (invocation.getArgs()[1].endsWith("m")) {
                            timeunit = TimeUnit.MINUTES;
                            runtime = Integer.parseInt(invocation.getArgs()[1].replace("m", ""));
                        } else if (invocation.getArgs()[1].endsWith("d")) {
                            timeunit = TimeUnit.DAYS;
                            runtime = Integer.parseInt(invocation.getArgs()[1].replace("d", ""));
                        } else if (invocation.getArgs()[1].endsWith("w")) {
                            extra = 604800000.002;
                            runtime = Integer.parseInt(invocation.getArgs()[1].replace("w", ""));
                        } else if (invocation.getArgs()[1].endsWith("mon")) {
                            extra = 2629745999.989;
                            runtime = Integer.parseInt(invocation.getArgs()[1].replace("mon", ""));
                        }

                        if (runtime < 0)
                            throw new IllegalArgumentException();
                    } catch (IllegalArgumentException e) {
                        return message(error("Invalid argument",
                                "The time must be an integer number greater than 0."));
                    }

                    StringBuilder prize = new StringBuilder(invocation.getArgs()[2]);
                    for (int i = 3; i < invocation.getArgs().length; i++)
                        prize.append(" ").append(invocation.getArgs()[i]);
                    if (extra == 0) {
                        Remind remind = createRemind(invocation.getTextChannel().getIdLong(),
                                prize.toString(),
                                (System.currentTimeMillis() + timeunit.toMillis(runtime)),
                                invocation.getAuthor().getIdLong());

                        return remind == null ? message(error()) : null;
                    } else {
                        Remind remind = createRemind(invocation.getTextChannel().getIdLong(),
                                prize.toString(),
                                (long) (System.currentTimeMillis() + runtime * extra),
                                invocation.getAuthor().getIdLong());
                        return remind == null ? message(error()) : null;
                    }
                default:
                    return createHelpMessage(invocation);
            }
        }
    }

    public Remind createRemind(long textChannelId, String prize, long expirationDate, long authorId) {
        // create and send giveaway message
        Message message = RubiconBot.getJDA().getTextChannelById(textChannelId).sendMessage(message(
                remindEmbed("Successfully set reminder!",
                        "I will remind you to do following: `" + prize + "`")))
                .complete();
        message.delete().queueAfter(10, TimeUnit.SECONDS);
        // create and giveaway
        Remind remind = new Remind(textChannelId, message.getIdLong(), prize, expirationDate, authorId);
        remind.save();
        return remind;
    }

    private static EmbedBuilder remindEmbed(String title, String description) {
        return embed(":clock: " + title, description).setColor(Colors.COLOR_PRIMARY);
    }
}
