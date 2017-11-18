package fun.rubicon.commands.general;

import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.util.Colors;
import fun.rubicon.util.Info;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.concurrent.TimeUnit;

public class CommandInfo extends Command {

    public CommandInfo(String command, CommandCategory category) {
        super(command, category);
    }

    @Override
    protected void execute(String[] args, MessageReceivedEvent e) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Colors.COLOR_PRIMARY);
        builder.setAuthor(Info.BOT_NAME + " - Info", "https://rubicon.fun", e.getJDA().getSelfUser().getEffectiveAvatarUrl());
        builder.setThumbnail("https://cdn.discordapp.com/attachments/381176080494624768/381176148828356608/13079-thumb.jpg");
        String authors = "";
        for(User u : Info.BOT_AUTHORS) {
            authors += u.getName() + "#" + u.getDiscriminator() + "\n";
        }
        builder.addField("Bot Name", Info.BOT_NAME, true);
        builder.addField("Bot Version", Info.BOT_VERSION, true);
        builder.addField("Website", "[Link](" + Info.BOT_WEBSITE + ")", true);
        builder.addField("Bot Invite", "[Invite Rubicon](https://discordapp.com/oauth2/authorize?client_id=380713705073147915&scope=bot&permissions=2146958591)", true);
        builder.addField("Github Link", "[Github Link](" + Info.BOT_GITHUB + ")", true);
        builder.addField("Patreon Link", "[Rubicon Dev Team](https://www.patreon.com/rubiconbot)", true);
        builder.addField("Authors", authors, true);
        String dependecies = "" +
                "[json.org](http://json.org/)\n" +
                "[JDA](https://github.com/DV8FromTheWorld/JDA)\n" +
                "[mysql-connector](https://mvnrepository.com/artifact/mysql/mysql-connector-java)\n" +
                "[slf4j-simple](https://mvnrepository.com/artifact/org.slf4j/slf4j-simple)\n" +
                "[json-simple](https://mvnrepository.com/artifact/com.googlecode.json-simple/json-simple)\n" +
                "[gson](https://github.com/google/gson)\n" +
                "[apache-commons-io](https://commons.apache.org/proper/commons-io/)\n" +
                "[jsoup](https://jsoup.org/)";
        builder.addField("Dependencies", dependecies, false);

        e.getTextChannel().sendMessage(builder.build()).queue(msg -> msg.delete().queueAfter(120, TimeUnit.SECONDS));
    }

    @Override
    public String getDescription() {
        return "Shows some information about the bot!";
    }

    @Override
    public String getUsage() {
        return "info";
    }

    @Override
    public int getPermissionLevel() {
        return 0;
    }
}
