/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.botowner;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command2.CommandHandler;
import fun.rubicon.command2.CommandManager;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.text.ParseException;

public class CommandSetmoney extends CommandHandler {
    public CommandSetmoney() {
        super(new String[]{"setmoney","moneyset"}, CommandCategory.BOT_OWNER, new PermissionRequirements(4,"command.setmoney"),"Set a users money to a given amount.","setmoney <UserAsMention> <amount of money>");
    }


    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        int user_set_money = 0;
        if(parsedCommandInvocation.args.length >= 2){
            try {
                user_set_money = Integer.parseInt(parsedCommandInvocation.args[parsedCommandInvocation.args.length-1]);
                if(user_set_money >= 0){
                    RubiconBot.getMySQL().updateUserValue(parsedCommandInvocation.invocationMessage.getMentionedUsers().get(0),"money",String.valueOf(user_set_money));
                    return new MessageBuilder().setEmbed(new EmbedBuilder().setDescription("Money of "+ parsedCommandInvocation.invocationMessage.getMentionedUsers().get(0).getAsMention() + " has been set to " + user_set_money + " Ruby's.").build()).build();

                }else{
                    return new MessageBuilder().setEmbed(new EmbedBuilder().setDescription("Money value must be bigger than 0!").build()).build();

                }

            } catch (NumberFormatException exception) {
                return new MessageBuilder().setEmbed(new EmbedBuilder().setDescription("The last value is not a number or bigger than 2.147.483.647\n" + getUsage()).build()).build();

            }
            //RubiconBot.getMySQL().updateUserValue(e.getMessage().getMentionedUsers().get(0),"money",String.valueOf(user_set_money));
            //sendEmbededMessage("Money of "+ e.getMessage().getMentionedUsers().get(0).getAsMention() + " has been set to " + user_set_money + " Ruby's.");
        }else{
            return new MessageBuilder().setEmbed(new EmbedBuilder().setDescription("Only two arguments are needed!\n" + getUsage()).build()).build();
        }
    }

}
