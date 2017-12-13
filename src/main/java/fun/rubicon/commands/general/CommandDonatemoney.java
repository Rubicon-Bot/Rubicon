package fun.rubicon.commands.general;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command2.CommandHandler;
import fun.rubicon.command2.CommandManager;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.text.ParseException;

/**
 * Rubicon Discord bot
 *
 * @author xEiisKeksx
 * @copyright Rubicon Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.commands.general
 * @deprecated
 */
public class CommandDonatemoney extends CommandHandler {

    public CommandDonatemoney() {
        super(new String[] {}, CommandCategory.GENERAL, new PermissionRequirements(0, "command.donatemoney"), "You can give someone some Ruby's!", "givemoney <UserAsMention> <amount>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        String[] args = parsedCommandInvocation.args;
        Message message = parsedCommandInvocation.invocationMessage;
        int user1_has_money = 0;
        int user2_has_money = 0;
        int user_spend_money = 0;
        if (args.length == 2) {
            try {
                user_spend_money = Integer.parseInt(args[1]);
                user1_has_money = Integer.parseInt(RubiconBot.getMySQL().getUserValue(message.getAuthor(), "money"));
                user2_has_money = Integer.parseInt(RubiconBot.getMySQL().getUserValue(message.getMentionedUsers().get(0), "money"));
                if (user1_has_money < user_spend_money) {
                    return new MessageBuilder().setEmbed(EmbedUtil.error("Error","Sorry " + message.getAuthor().getAsMention() + ". You only have " + user1_has_money + "Ruby's you can donate!").build()).build();
                } else {
                    RubiconBot.getMySQL().updateUserValue(message.getAuthor(), "money", String.valueOf(user1_has_money - user_spend_money));
                    RubiconBot.getMySQL().updateUserValue(message.getMentionedUsers().get(0), "money", String.valueOf(user2_has_money + user_spend_money));
                    return new MessageBuilder().setEmbed(EmbedUtil.error("Error", message.getAuthor().getAsMention() + " give " + user_spend_money + " Ruby's to " + message.getMentionedUsers().get(0).getAsMention() + ".").build()).build();
                }
            } catch (NumberFormatException exception) {
                return new MessageBuilder().setEmbed(EmbedUtil.info("Usage", "givemoney <UserAsMention> <amount>").build()).build();
            }
        } else {
            return new MessageBuilder().setEmbed(EmbedUtil.info("Usage", "givemoney <UserAsMention> <amount>").build()).build();
        }
    }

}
