package fun.rubicon.listener;

import fun.rubicon.core.Main;
import fun.rubicon.util.MySQL;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Rubicon Discord bot
 *
 * @author Leon Kappes / Lee
 * @copyright Rubicon Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package listener
 */


public class SQLPreventDisconnect extends ListenerAdapter{
    private boolean check = false;
    public void onMessageReceived(MessageReceivedEvent event) {

        if (check = false){
            MySQL s = Main.getMySQL();
            check= true;
            String d=s.getGuildValue(event.getGuild(), "prefix");
            System.out.println("Prevented Disconnect" + d);
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                check=false;
                }
            },3600000);
        }

    }

}
