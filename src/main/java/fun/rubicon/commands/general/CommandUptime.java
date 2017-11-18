package fun.rubicon.commands.general;

import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.util.Info;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CommandUptime extends Command{

    private String getTime(Date date, String pattern) {
        return new SimpleDateFormat(pattern).format(date);
    }

    private String getTimeDiff(Date date1, Date date2) {
        long diff = date1.getTime() - date2.getTime();
        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffDays = diff / (24 * 60 * 60 * 1000);
        return diffDays + " Days, " + parseTimeNumbs(diffHours) + " Hours, " + parseTimeNumbs(diffMinutes) + " Minutes, " + parseTimeNumbs(diffSeconds) + " Seconds";
    }

    private String parseTimeNumbs(long time) {
        String timeString = time + "";
        if (timeString.length() < 2)
            timeString = "0" + time;
        return timeString;
    }
    public CommandUptime(String command, CommandCategory category) {
        super(command, category);
    }

    @Override
    protected void execute(String[] args, MessageReceivedEvent e) {
        e.getChannel().sendMessage(
                new EmbedBuilder()
                        .setColor(new Color(255,35,206))
                        .setDescription(":alarm_clock:   __**UPTIME**__")
                        .addField("Last restart", getTime(Info.lastRestart, "dd.MM.yyyy - HH:mm:ss (z)"), false)
                        .addField("Online since", getTimeDiff(new Date(), Info.lastRestart), false)
                        .build()
        ).queue();
    }


    @Override
    public String getDescription() {
        return "Sends the Bot uptime and when the last Restart was.";
    }

    @Override
    public String getUsage() {
        return "uptime";
    }

    @Override
    public int getPermissionLevel() {
        return 0;
    }
}

