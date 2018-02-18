/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.general;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.sql.UserSQL;
import fun.rubicon.util.Colors;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;

import static fun.rubicon.util.EmbedUtil.*;

public class CommandMoney extends CommandHandler {
    public CommandMoney() {
        super(new String[]{"money", "ruby"}, CommandCategory.GENERAL,
                new PermissionRequirements("command.money", false, true),
                "You can donate Ruby's to someone!", "<give | set | add | remove> <@User> <amount>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        UserSQL userSQL = new UserSQL(parsedCommandInvocation.getAuthor());
        UserSQL userSQL2 = null;
        if (parsedCommandInvocation.getMessage().getMentionedUsers().size() == 1) {
            userSQL2 = new UserSQL(parsedCommandInvocation.getMessage().getMentionedUsers().get(0));
        }
        int user1_has_money = 0;
        int user2_has_money = 0;
        int user_spend_money = 0;
        if (parsedCommandInvocation.getArgs().length == 0) {
            parsedCommandInvocation.getTextChannel().sendMessage(new EmbedBuilder().setColor(Colors.COLOR_PRIMARY).setDescription("Balance: `" + new UserSQL(parsedCommandInvocation.getAuthor()).get("money") + "`").setAuthor(parsedCommandInvocation.getAuthor().getName() + "'s money", null, parsedCommandInvocation.getAuthor().getAvatarUrl()).build()).queue();
            return null;
        }
        switch (parsedCommandInvocation.getArgs()[0]) {
            case "give":
                if (parsedCommandInvocation.getArgs().length == 3) {
                    try {
                        if (parsedCommandInvocation.getMessage().getMentionedMembers().size() == 1) {
                            if (parsedCommandInvocation.getMessage().getMentionedMembers().get(0).getUser().getId().equalsIgnoreCase(parsedCommandInvocation.getAuthor().getId())) {
                                return new MessageBuilder().setEmbed(EmbedUtil.error("Error!", "You cant donate money yourself!").build()).build();
                            }
                        } else {
                            return createHelpMessage();
                        }
                        user_spend_money = Integer.parseInt(parsedCommandInvocation.getArgs()[parsedCommandInvocation.getArgs().length - 1]);
                        user1_has_money = Integer.parseInt(userSQL.get("money"));
                        user2_has_money = Integer.parseInt(userSQL2.get("money"));
                        if (user1_has_money < user_spend_money) {
                            return message(error("Not enough Money", "Sorry " + parsedCommandInvocation.getMessage().getAuthor().getAsMention() + ". You only have " + user1_has_money + " Ruby's!"));
                        } else {
                            if ((user2_has_money + user_spend_money) <= 2147483647) {
                                userSQL.set("money", String.valueOf(user1_has_money - user_spend_money));
                                userSQL2.set("money", String.valueOf(user2_has_money + user_spend_money));
                                return message(success("Donation completed", parsedCommandInvocation.getMessage().getAuthor().getAsMention() + " give " + user_spend_money + " Ruby's to " + parsedCommandInvocation.getMessage().getMentionedUsers().get(0).getAsMention() + "."));
                            } else {
                                return message(error("Money value to big!", "Money value must be smaller than " + ((2147483647 - user2_has_money) + 1) + "!"));
                            }
                        }
                    } catch (NumberFormatException exception) {
                        return message(error("NaN or money value is to big!", "The money value is not a number or bigger than 2.147.483.647!"));
                    }
                } else {
                    return message(error("Wrong count of arguments", "Three arguments are necessary!\n" + getParameterUsage()));
                }
            case "set":
                if (new PermissionRequirements("command.money.modify", true, false).coveredBy(userPermissions)) {
                    if (parsedCommandInvocation.getArgs().length == 3) {
                        try {
                            user_spend_money = Integer.parseInt(parsedCommandInvocation.getArgs()[parsedCommandInvocation.getArgs().length - 1]);
                            if (user_spend_money > -1) {
                                userSQL2.set("money", String.valueOf(user_spend_money));
                                return message(success("Money has been set!", "Money of " + parsedCommandInvocation.getMessage().getMentionedUsers().get(0).getAsMention() + " has been set to " + user_spend_money + " Ruby's."));
                            } else {
                                return message(error("Money value to small!", "Money value must be 0 or bigger!"));
                            }

                        } catch (NumberFormatException exception) {
                            return message(error("NaN or money value is to big!", "The money value is not a number or bigger than 2.147.483.647!"));
                        }
                    } else {
                        return message(error("Wrong count of arguments", "Three arguments are necessary!\n" + getParameterUsage()));
                    }
                } else {
                    return message(no_permissions());
                }
            case "add":
                int max_money = 2147483647;
                user2_has_money = Integer.parseInt(userSQL.get("money"));
                if (new PermissionRequirements("command.money.modify", true, false).coveredBy(userPermissions)) {
                    if (parsedCommandInvocation.getArgs().length == 3) {
                        try {
                            user_spend_money = Integer.parseInt(parsedCommandInvocation.getArgs()[parsedCommandInvocation.getArgs().length - 1]);
                            if (user_spend_money > 0) {
                                if ((user2_has_money + user_spend_money) <= 2147483647 && (user2_has_money + user_spend_money) > 0) {
                                    if (user2_has_money == 2147483647) {
                                        return message(error("Too much money!", parsedCommandInvocation.getMessage().getMentionedUsers().get(0).getAsMention() + " has already the money maximum!"));
                                    } else {
                                        userSQL2.set("money", String.valueOf(user2_has_money + user_spend_money));
                                        return message(success("Money has been added!", "Money of " + parsedCommandInvocation.getMessage().getMentionedUsers().get(0).getAsMention() + " has been set to " + (user2_has_money + user_spend_money) + " Ruby's."));
                                    }
                                } else {
                                    return message(error("Money value to big!", "Money value must be smaller than " + ((max_money - user2_has_money) + 1) + "!"));
                                }
                            } else {
                                return message(error("Money value to small!", "Money value must be bigger than 0!"));
                            }

                        } catch (NumberFormatException exception) {
                            return message(error("Not a number!", "The money value is not a number!"));
                        }
                    } else {
                        return message(error("Wrong count of arguments", "Three arguments are necessary!\n" + getParameterUsage()));
                    }
                } else {
                    return message(no_permissions());
                }

            case "remove":
                user2_has_money = Integer.parseInt(userSQL2.get("money"));
                if (new PermissionRequirements("command.money.modify", true, false).coveredBy(userPermissions)) {
                    if (parsedCommandInvocation.getArgs().length == 3) {
                        try {
                            user_spend_money = Integer.parseInt(parsedCommandInvocation.getArgs()[parsedCommandInvocation.getArgs().length - 1]);
                            if (user_spend_money > 0) {
                                if ((Integer.parseInt(userSQL2.get("money")) - user_spend_money) >= 0) {
                                    userSQL2.set("money", String.valueOf(user2_has_money - user_spend_money));
                                    return message(success("Money has been removed!", "Money of " + parsedCommandInvocation.getMessage().getMentionedUsers().get(0).getAsMention() + " has been set to " + (user2_has_money - user_spend_money) + " Ruby's."));
                                } else {
                                    return message(error("Money value to big!", "Money value must be smaller than " + ((2147483647 - user2_has_money) + 1) + "!"));
                                }
                            } else {
                                return message(error("Money value to small!", "Money value must be bigger than 0!"));
                            }

                        } catch (NumberFormatException exception) {
                            return message(error("NaN or money value is to big!", "The money value is not a number or bigger than 2.147.483.647!"));
                        }
                    } else {
                        return message(error("Wrong count of arguments", "Three arguments are necessary!\n" + getParameterUsage()));
                    }
                } else {
                    return message(no_permissions());
                }
            default:
                return message(error("No valid money option", "Only give, set, add, remove are valid money options!"));
        }
    }
}
