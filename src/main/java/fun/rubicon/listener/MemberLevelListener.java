package fun.rubicon.listener;

import fun.rubicon.RubiconBot;
import fun.rubicon.core.Main;
import fun.rubicon.util.Cooldown;
import fun.rubicon.util.MySQL;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Rubicon Discord bot
 *
 * @author Leon Kappes / Lee
 * @copyright RubiconBot Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.listener
 */
public class MemberLevelListener extends ListenerAdapter {


    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }
        if (Cooldown.has(event.getAuthor().getId())) {
            return;
        }
        MySQL sql = RubiconBot.getMySQL();
        if (sql.ifUserExist(event.getAuthor())) {
            //Point System
            int current = Integer.parseInt(sql.getUserValue(event.getAuthor(), "points"));
            int randomNumber = (int) ((Math.random() * 10) + 10);
            String point = String.valueOf(current + randomNumber);
            int points = current + randomNumber;
            sql.updateUserValue(event.getAuthor(), "points", point);
            //Cooldown
            Cooldown.add(event.getAuthor().getId());
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    Cooldown.remove(event.getAuthor().getId());
                }
            }, 30000);

            String lvlnow = sql.getUserValue(event.getAuthor(), "level");
            int dann = Integer.parseInt(lvlnow);
            int req = dann * 30;

            if (points > req) {
                dann++;
                String fina = String.valueOf(dann);
                sql.updateUserValue(event.getAuthor(), "level", fina);
                sql.updateUserValue(event.getAuthor(), "points", "0");
                String l = (sql.getUserValue(event.getAuthor(), "level"));
                int foo = Integer.parseInt(l);
                //Level Up
                //TODO Stop editing my classes
                /*Message msg = event.getChannel().sendMessage(new EmbedBuilder()
                        .setDescription(event.getAuthor().getAsMention() + " ,wow you got a Level up to Level **" + sql.getUserValue(event.getAuthor(), "level") + "** !")
                        .build()
                ).complete();*/
                Random r = new Random();
                int Low = 10;
                int High = 100;
                int Result = r.nextInt(High - Low) + Low;
                int ran = Math.round(Result);
                int foa = foo * 200 / 3 + ran;
                String m = String.valueOf(Math.round(foa));
                sql.updateUserValue(event.getAuthor(), "money", m);

                /*new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        msg.delete().queue();
                    }
                }, 3000);*/

            }
        } else sql.createUser(event.getAuthor());
    }
}
