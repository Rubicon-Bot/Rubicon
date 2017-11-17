package de.rubicon.command;

import java.util.ArrayList;

import de.rubicon.util.Info;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandParser {

    public CommandContainer parse(String raw, MessageReceivedEvent e) {
        String beheaded = raw.toLowerCase().replaceFirst(Info.BOT_DEFAULT_PREFIX.toLowerCase(), "");
        String[] splitBeheaded = beheaded.split(" ");
        String invoke = splitBeheaded[0];
        ArrayList<String> split = new ArrayList<>();
        for (String s : splitBeheaded) {
            split.add(s);
        }
        String[] args = new String[split.size() - 1];
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

        public CommandContainer(String rw, String beheaded, String[] splitBeheaded, String invoke, String[] args,
                                MessageReceivedEvent event) {
            this.raw = rw;
            this.beheaded = beheaded;
            this.splitBeheaded = splitBeheaded;
            this.invoke = invoke.toLowerCase();
            this.args = args;
            this.event = event;
        }

    }

}