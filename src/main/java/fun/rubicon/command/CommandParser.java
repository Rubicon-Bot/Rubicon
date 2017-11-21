package fun.rubicon.command;

import java.util.ArrayList;
import java.util.Collections;

import fun.rubicon.core.Main;
import fun.rubicon.util.Info;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandParser {

    //TODO rework
    public CommandContainer parse(String raw, MessageReceivedEvent e) {
        // cut off command prefix
        String beheaded = raw.substring(Main.getMySQL().getGuildValue(e.getGuild(), "prefix").length(), raw.length());

        // split arguments
        String[] splitBeheaded = beheaded.split(" ");
        ArrayList<String> split = new ArrayList<>();
        Collections.addAll(split, splitBeheaded);

        // extract invoker argument
        String invoke = split.get(0);
        String[] args = new String[split.size()-1];
        split.subList(1, split.size()).toArray(args);

        return new CommandContainer(raw, beheaded, splitBeheaded, invoke, args, e);
    }

    public CommandContainer parsep(String raw, MessageReceivedEvent e) {
        ArrayList<String> split = new ArrayList<>();

        if (Main.getMySQL().getGuildValue(e.getGuild(),"prefix").equals(Info.BOT_DEFAULT_PREFIX)){
            //TODO?
        }

        String beheaded = raw.substring(Info.BOT_DEFAULT_PREFIX.length(), raw.length());
        String[] splitBeheaded = beheaded.split(" ");

        for (String s : splitBeheaded) {
            split.add(s);
        }

        String invoke = split.get(0);
        String[] args = new String[split.size()-1];
        split.subList(1, split.size()).toArray(args);

        return new CommandContainer(raw, beheaded, splitBeheaded, invoke, args, e);
    }

    public class CommandContainer {

        public final String raw;
        public final String beheaded;
        public final String[] splitBeheaded;
        public final String invoke;
        public final String[] args;
        public final MessageReceivedEvent event;

        public CommandContainer(String rw, String beheaded, String[] splitBeheaded, String invoke, String[] args, MessageReceivedEvent e) {
            this.raw = rw;
            this.beheaded = beheaded;
            this.splitBeheaded = splitBeheaded;
            this.invoke = invoke;
            this.args = args;
            this.event = e;
        }
    }
}