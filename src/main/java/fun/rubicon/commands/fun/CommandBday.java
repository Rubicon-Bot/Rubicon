package fun.rubicon.commands.fun;

import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.core.Main;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Rubicon Discord bot
 *
 * @author Leon Kappes / Lee
 * @copyright Rubicon Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.commands.fun
 */
public class CommandBday extends Command{
    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd:MM");
        Date date = new Date();
        return dateFormat.format(date);
    }
    public CommandBday(String command, CommandCategory category) {
        super(command, category);
    }

    @Override
    protected void execute(String[] args, MessageReceivedEvent e) throws ParseException {
        if (args.length < 1) {sendUsageMessage();
            return;
        }
        switch (args[0]) {
            case "set":
                Main.getMySQL().setString("user", "bday", args[0], "userid", e.getAuthor().getId());
                return;
        }
        User user = e.getAuthor();
        String str_date =  Main.getMySQL().getString("user", "bday", "userid", user.getId());
        if (str_date.equals("0")) {
            sendUsageMessage();
            return;
        }
        DateFormat formatter ;
        Date d;
        formatter = new SimpleDateFormat("dd:MM");
            d = formatter.parse(str_date);



        Date date=new Date();
        Timer timer = new Timer();


        timer.schedule(new TimerTask(){
            public void run(){
                if (getDateTime().equals(d)){
                    if (Main.getMySQL().getGuildValue(e.getGuild(), "channel").equals(0)){
                        e.getTextChannel().sendMessage(new EmbedBuilder()
                                .setColor(Color.CYAN)
                                .setTitle(":cake: Happy Birthday " + user.getAsMention() + " :cake:")
                                .setAuthor(user.getName(), "", user.getAvatarUrl())
                                .setDescription("Hey @here ,\n" + user.getName() + "has his Birthday Today!")
                                .build()
                        ).queue();
                    }else {
                        e.getGuild().getTextChannelById(Main.getMySQL().getGuildValue(e.getGuild(), "channel")).sendMessage(new EmbedBuilder()
                                .setColor(Color.CYAN)
                                .setTitle(":cake: Happy Birthday " + user.getAsMention() + " :cake:")
                                .setAuthor(user.getName(), "", user.getAvatarUrl())
                                .setDescription("Hey @here ,\n" + user.getName() + "has his Birthday Today!")
                                .build()
                        ).queue();
                    }

                }
            }
        },date, 24*60*60*1000);

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
}
