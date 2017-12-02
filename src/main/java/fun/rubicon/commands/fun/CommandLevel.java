package fun.rubicon.commands.fun;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command2.CommandHandler;
import fun.rubicon.command2.CommandManager;
import fun.rubicon.core.Main;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import fun.rubicon.util.MySQL;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.text.ParseException;

/**
 * Rubicon Discord bot
 *
 * @author Leon Kappes / Lee
 * @copyright Rubicon Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.commands.fun
 */
public class CommandLevel extends CommandHandler{


    public CommandLevel() {
        super(new String[]{"rank","level","money","lvl"},CommandCategory.FUN,new PermissionRequirements(0,"command.rank"),"Get your level, points and ruby's.","rank");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        MySQL LVL = RubiconBot.getMySQL();
        return new MessageBuilder().setEmbed(new EmbedBuilder().setDescription("Your current Level: \n" + LVL.getUserValue(parsedCommandInvocation.invocationMessage.getAuthor(), "level") + "\n" + "Your current Points: \n" + LVL.getUserValue(parsedCommandInvocation.invocationMessage.getAuthor(), "points") + "\n Your Current Ruby´s: \n" + LVL.getUserValue(parsedCommandInvocation.invocationMessage.getAuthor(), "money")).build()).build();
    }
}
