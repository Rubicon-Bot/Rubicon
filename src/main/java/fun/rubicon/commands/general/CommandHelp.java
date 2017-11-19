package fun.rubicon.commands.general;

import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.core.DiscordCore;
import fun.rubicon.core.Main;
import fun.rubicon.util.Colors;
import fun.rubicon.util.Info;
import fun.rubicon.util.MySQL;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Rubicon Discord bot
 *
 * @author Foryasee / Yannick
 * @copyright Rubicon Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package commands.general
 */

public class CommandHelp extends Command {

    public CommandHelp(String command, CommandCategory category) {
        super(command, category);
    }

    @Override
    protected void execute(String[] args, MessageReceivedEvent e) {
        MySQL SQL = Main.getMySQL();
        if (args.length == 1 && CommandHandler.getCommands().containsKey(args[0].toLowerCase())) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setAuthor("Help - " + CommandHandler.getCommands().get(args[0].toLowerCase()).getCommand(), null, DiscordCore.getJDA().getSelfUser().getEffectiveAvatarUrl());
            builder.setColor(Colors.COLOR_PRIMARY);
            builder.addField("Description", CommandHandler.getCommands().get(args[0].toLowerCase()).getDescription(), false);
            builder.addField("Usage", SQL.getGuildValue(e.getGuild(), "prefix") + CommandHandler.getCommands().get(args[0].toLowerCase()).getUsage(), false);
            e.getTextChannel().sendMessage(builder.build()).queue(msg -> msg.delete().queueAfter(3, TimeUnit.MINUTES));
        } else {
            StringBuilder sbGeneral = new StringBuilder();
            StringBuilder sbFun = new StringBuilder();
            StringBuilder sbAdmin = new StringBuilder();
            StringBuilder sbGuildOwner = new StringBuilder();
            StringBuilder sbBotOwner = new StringBuilder();
            StringBuilder sbTools = new StringBuilder();

            for (Map.Entry<String, Command> c : CommandHandler.getCommands().entrySet()) {
                if(c.getValue().getCategory().equals(CommandCategory.GENERAL)) {
                    sbGeneral.append(SQL.getGuildValue(e.getGuild(), "prefix") + c.getValue().getCommand() + " - " + c.getValue().getDescription() + "\n");
                } else if(c.getValue().getCategory().equals(CommandCategory.FUN)) {
                    sbFun.append(SQL.getGuildValue(e.getGuild(), "prefix") + c.getValue().getCommand() + " - " + c.getValue().getDescription() + "\n");
                } else if(c.getValue().getCategory().equals(CommandCategory.TOOLS)) {
                    sbTools.append(SQL.getGuildValue(e.getGuild(), "prefix") + c.getValue().getCommand() + " - " + c.getValue().getDescription() + "\n");
                } else if(c.getValue().getCategory().equals(CommandCategory.ADMIN)) {
                    sbAdmin.append(SQL.getGuildValue(e.getGuild(), "prefix") + c.getValue().getCommand() + " - " + c.getValue().getDescription() + "\n");
                } else if(c.getValue().getCategory().equals(CommandCategory.GUILD_OWNER)) {
                    sbGuildOwner.append(SQL.getGuildValue(e.getGuild(), "prefix") + c.getValue().getCommand() + " - " + c.getValue().getDescription() + "\n");
                } else if(c.getValue().getCategory().equals(CommandCategory.BOT_OWNER)) {
                    sbBotOwner.append(SQL.getGuildValue(e.getGuild(), "prefix") + c.getValue().getCommand() + " - " + c.getValue().getDescription() + "\n");
                }
            }

            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Colors.COLOR_SECONDARY);
            builder.setAuthor(Info.BOT_NAME + " Help List", null, e.getJDA().getSelfUser().getEffectiveAvatarUrl());
            builder.setDescription("--- Want more info? Use `" + SQL.getGuildValue(e.getGuild(), "prefix") + "help <command>` ---");
            builder.addField("General", sbGeneral.toString(), false);
            builder.addField("Fun", sbFun.toString(), false);
            builder.addField("Tools", sbTools.toString(), false);
            builder.addField("Admin", sbAdmin.toString(), false);
            builder.addField("Server Owner", sbGuildOwner.toString(), false);
            builder.addField("Bot Owner", sbBotOwner.toString(), false);

            builder.setFooter("Loaded Commands: " + CommandHandler.getCommands().entrySet().size(), null);

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
