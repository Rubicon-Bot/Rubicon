package de.rubicon.commands.general;

import de.rubicon.command.Command;
import de.rubicon.command.CommandCategory;
import de.rubicon.command.CommandHandler;
import de.rubicon.core.DiscordCore;
import de.rubicon.util.Colors;
import de.rubicon.util.Info;
import de.rubicon.util.Logger;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.Map;

public class CommandHelp extends Command {

    public CommandHelp(String command, CommandCategory category) {
        super(command, category);
    }

    @Override
    protected void execute(String[] args, MessageReceivedEvent e) {
        Logger.debug(e.getAuthor().getName() + " | Help Executed");
        if (args.length == 1 && CommandHandler.getCommands().containsKey(args[0].toLowerCase())) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setAuthor("Hilfe - " + CommandHandler.getCommands().get(args[0].toLowerCase()).getCommand(), null, DiscordCore.getJDA().getSelfUser().getEffectiveAvatarUrl());
            builder.setColor(Colors.COLOR_PRIMARY);
            builder.addField("Beschreibung", CommandHandler.getCommands().get(args[0].toLowerCase()).getDescription(), false);
            builder.addField("Benutzung", Info.BOT_DEFAULT_PREFIX + CommandHandler.getCommands().get(args[0].toLowerCase()).getUsage(), false);
            e.getAuthor().openPrivateChannel().queue((privateChannel) -> {
                privateChannel.sendMessage(builder.build()).queue();
            });
        } else {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setAuthor(Info.BOT_NAME + " - Hilfe", null, DiscordCore.getJDA().getSelfUser().getEffectiveAvatarUrl());
            builder.setColor(Colors.COLOR_PRIMARY);
            builder.setDescription("--- Wenn du die Benutzung eines Command sehen willst, dann benutze " + Info.BOT_DEFAULT_PREFIX + "hilfe <command> ---");

            for (Map.Entry<String, Command> entry : CommandHandler.getCommands().entrySet()) {
                builder.addField(" " + Info.BOT_DEFAULT_PREFIX + entry.getKey(), " " + entry.getValue().getDescription(), false);
            }
            e.getAuthor().openPrivateChannel().queue((privateChannel) -> {
                privateChannel.sendMessage(builder.build()).queue();
            });
        }
    }

    @Override
    public String getDescription() {
        return "Shows all commands in a list!";
    }

    @Override
    public String getUsage() {
        return "help [command]";
    }

    @Override
    public int getPermissionLevel() {
        return 0;
    }
}
