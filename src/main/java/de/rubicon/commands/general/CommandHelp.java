package de.rubicon.commands.general;

import de.rubicon.command.Command;
import de.rubicon.command.CommandCategory;
import de.rubicon.command.CommandHandler;
import de.rubicon.core.DiscordCore;
import de.rubicon.util.Colors;
import de.rubicon.util.Info;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CommandHelp extends Command {

    public CommandHelp(String command, CommandCategory category) {
        super(command, category);
    }

    @Override
    protected void execute(String[] args, MessageReceivedEvent e) {
        if (args.length == 1 && CommandHandler.getCommands().containsKey(args[0].toLowerCase())) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setAuthor("Help - " + CommandHandler.getCommands().get(args[0].toLowerCase()).getCommand(), null, DiscordCore.getJDA().getSelfUser().getEffectiveAvatarUrl());
            builder.setColor(Colors.COLOR_PRIMARY);
            builder.addField("Description", CommandHandler.getCommands().get(args[0].toLowerCase()).getDescription(), false);
            builder.addField("Usage", Info.BOT_DEFAULT_PREFIX + CommandHandler.getCommands().get(args[0].toLowerCase()).getUsage(), false);
            e.getTextChannel().sendMessage(builder.build()).queue(msg -> msg.delete().queueAfter(3, TimeUnit.MINUTES));
        } else {
            StringBuilder sbGeneral = new StringBuilder();
            StringBuilder sbFun = new StringBuilder();
            StringBuilder sbModeration = new StringBuilder();
            StringBuilder sbAdmin = new StringBuilder();
            StringBuilder sbGuildOwner = new StringBuilder();
            StringBuilder sbBotOwner = new StringBuilder();
            StringBuilder sbTools = new StringBuilder();

            for (Map.Entry<String, Command> c : CommandHandler.getCommands().entrySet()) {
                if(c.getValue().getCategory().equals(CommandCategory.GENERAL)) {
                    sbGeneral.append(Info.BOT_DEFAULT_PREFIX + c.getValue().getCommand() + " - " + c.getValue().getDescription() + "\n");
                } else if(c.getValue().getCategory().equals(CommandCategory.FUN)) {
                    sbFun.append(Info.BOT_DEFAULT_PREFIX + c.getValue().getCommand() + " - " + c.getValue().getDescription() + "\n");
                } else if(c.getValue().getCategory().equals(CommandCategory.TOOLS)) {
                    sbTools.append(Info.BOT_DEFAULT_PREFIX + c.getValue().getCommand() + " - " + c.getValue().getDescription() + "\n");
                } else if(c.getValue().getCategory().equals(CommandCategory.MODERATION)) {
                    sbModeration.append(Info.BOT_DEFAULT_PREFIX + c.getValue().getCommand() + " - " + c.getValue().getDescription() + "\n");
                } else if(c.getValue().getCategory().equals(CommandCategory.ADMIN)) {
                    sbAdmin.append(Info.BOT_DEFAULT_PREFIX + c.getValue().getCommand() + " - " + c.getValue().getDescription() + "\n");
                } else if(c.getValue().getCategory().equals(CommandCategory.GUILD_OWNER)) {
                    sbGuildOwner.append(Info.BOT_DEFAULT_PREFIX + c.getValue().getCommand() + " - " + c.getValue().getDescription() + "\n");
                } else if(c.getValue().getCategory().equals(CommandCategory.BOT_OWNER)) {
                    sbBotOwner.append(Info.BOT_DEFAULT_PREFIX + c.getValue().getCommand() + " - " + c.getValue().getDescription() + "\n");
                }
            }

            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Colors.COLOR_SECONDARY);
            builder.setAuthor(Info.BOT_NAME + " Help List", null, e.getJDA().getSelfUser().getEffectiveAvatarUrl());
            builder.setDescription("--- Want more info? Use `" + Info.BOT_DEFAULT_PREFIX + "help <command>` ---");
            builder.addField("General", sbGeneral.toString(), false);
            builder.addField("Fun", sbFun.toString(), false);
            builder.addField("Tools", sbTools.toString(), false);
            builder.addField("Moderation", sbModeration.toString(), false);
            builder.addField("Admin", sbAdmin.toString(), false);
            builder.addField("Server Owner", sbGuildOwner.toString(), false);
            builder.addField("Bot Owner", sbBotOwner.toString(), false);

            e.getAuthor().openPrivateChannel().queue(ch -> ch.sendMessage(builder.build()).queue());
        }
    }

    @Override
    public String getDescription() {
        return "Shows all commands in a list.";
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
