package fun.rubicon.commands.general;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command2.CommandHandler;
import fun.rubicon.command2.CommandManager;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import fun.rubicon.util.Colors;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

/**
 * Rubicon Discord bot
 *
 * @author Yannick Seeger
 * @copyright Rubicon Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.commands.fun
 */
public class CommandLevel extends CommandHandler {


    public CommandLevel() {
        super(new String[]{"rank", "level", "money", "lvl"}, CommandCategory.FUN, new PermissionRequirements(0, "command.rank"), "Get your level, points and ruby's.", "rank");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        User user = parsedCommandInvocation.invocationMessage.getAuthor();
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Colors.COLOR_PRIMARY);
        builder.setAuthor(user.getName(), null, user.getAvatarUrl());
        builder.addField("Points", RubiconBot.getMySQL().getUserValue(user, "points"), true);
        builder.addField("Level", RubiconBot.getMySQL().getUserValue(user, "level"), true);
        builder.addField("Ruby's", RubiconBot.getMySQL().getUserValue(user, "money"), true);
        return new MessageBuilder().setEmbed(builder.build()).build();
    }
}
