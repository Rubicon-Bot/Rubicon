package fun.rubicon.commands.general;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.core.PriceList;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.sql.UserSQL;
import fun.rubicon.util.Colors;
import fun.rubicon.util.EmbedUtil;
import fun.rubicon.util.Info;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class CommandPremium extends CommandHandler {

    private PermissionRequirements authorPermissions;
    private final long PREMIUM_TIME = new Date().getTime() + 15638400000L;

    public CommandPremium() {
        super(new String[]{"premium"}, CommandCategory.GENERAL, new PermissionRequirements("command.premium", false, true), "See your premium state or buy premium.",
                "| Shows current premium state\n" +
                        "buy | Buy premium");
        authorPermissions = new PermissionRequirements("command.premium.modify", true, false);
    }

    public static void assignPremiumRole(UserSQL user) {
        if (!user.isPremium()) return;
        Guild guild = RubiconBot.getJDA().getGuildById(Info.COMMUNITY_SERVER);
        if (guild.getMember(user.getUser()) == null) return;
        Role role = guild.getRoleById(Info.PREMIUM_ROLE);
        guild.getController().addSingleRoleToMember(user.getMember(guild), role).queue();
    }

    public static class PremiumChecker extends TimerTask {
        @Override
        public void run() {
            check();
        }

        public static void check() {
            Guild guild = RubiconBot.getJDA().getGuildById(Info.COMMUNITY_SERVER);
            Role role = guild.getRoleById(Info.PREMIUM_ROLE);
            guild.getMembers().stream().filter(member -> member.getRoles().contains(role)).collect(Collectors.toList()).forEach(m -> {
                UserSQL user = UserSQL.fromMember(m);
                if (!user.isPremium())
                    guild.getController().removeSingleRoleFromMember(m, role).queue();
            });
        }

        //call this method from your servlet init method
        public static void startTask() {
            Timer timer = new Timer();
            timer.schedule(new PremiumChecker(), 10* 60 * 1000, 10 * 60 * 1000);
        }
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        String[] args = parsedCommandInvocation.getArgs();
        UserSQL userSQL = UserSQL.fromUser(parsedCommandInvocation.getAuthor());
        if (args.length == 0) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setAuthor(parsedCommandInvocation.getAuthor().getName(), null, parsedCommandInvocation.getAuthor().getAvatarUrl());
            embedBuilder.setColor(Colors.COLOR_SECONDARY);
            String premiumEntry = userSQL.get("premium");
            if (premiumEntry.equalsIgnoreCase("false")) {
                embedBuilder.setDescription("No premium. \n\nBuy premium with `rc!premium buy` for 500k rubys.");
            } else {
                embedBuilder.setDescription("Premium is activated until " + userSQL.formatExpiryDate());
            }
            parsedCommandInvocation.getTextChannel().sendMessage(embedBuilder.build()).queue(message -> message.delete().queueAfter(3, TimeUnit.MINUTES));
            return null;
        }
        if (args[0].equalsIgnoreCase("buy")) {
            if (!userSQL.get("premium").equalsIgnoreCase("false")) {
                return EmbedUtil.message(EmbedUtil.info("Already activated!", "You already have premium until " + userSQL.getPremiumExpiryDate()));
            }
            int userMoney = Integer.parseInt(userSQL.get("money"));
            if (userMoney - PriceList.PREMIUM.getPrice() >= 0) {
                userSQL.set("premium", (PREMIUM_TIME) + "");
                userSQL.set("money", userMoney - PriceList.PREMIUM.getPrice() + "");
                assignPremiumRole(userSQL);
                return EmbedUtil.message(EmbedUtil.success("Success!", "Successfully bought premium."));
            } else {
                return EmbedUtil.message(EmbedUtil.error("Not enough money!", "Premium costs `" + PriceList.PREMIUM.getPrice() + "` rubys but you only have `" + userMoney + "` rubys"));
            }
        } else if (args[0].equalsIgnoreCase("add")) {
            if (authorPermissions.coveredBy(userPermissions)) {
                List<User> users = parsedCommandInvocation.getMessage().getMentionedUsers();
                if (users.size() == 0) {
                    return EmbedUtil.message(EmbedUtil.error("Error!", "You have to mention at least 1 user."));
                } else {
                    StringBuilder successValue = new StringBuilder();
                    successValue.append("Successfully added premium to");
                    for (User user : users) {
                        UserSQL pUser = UserSQL.fromUser(user);
                        pUser.set("premium", (PREMIUM_TIME) + "");
                        assignPremiumRole(pUser);
                        successValue.append(" / ").append(user.getAsMention());
                    }
                    String successText = successValue.toString().replaceFirst(" /", "");
                    return EmbedUtil.message(EmbedUtil.success("Success!", successText + "."));
                }
            }
        } else if (args[0].equalsIgnoreCase("remove")) {
            if (authorPermissions.coveredBy(userPermissions)) {
                List<User> users = parsedCommandInvocation.getMessage().getMentionedUsers();
                if (users.size() == 0) {
                    return EmbedUtil.message(EmbedUtil.error("Error!", "You have to mention at least 1 user."));
                } else {
                    StringBuilder successValue = new StringBuilder();
                    successValue.append("Successfully removed premium from");
                    for (User user : users) {
                        UserSQL pUser = UserSQL.fromUser(user);
                        pUser.set("premium", "false");
                        assignPremiumRole(pUser);
                        successValue.append(" / ").append(user.getAsMention());
                    }
                    String successText = successValue.toString().replaceFirst(" /", "");
                    return EmbedUtil.message(EmbedUtil.success("Success!", successText + "."));
                }
            }
        }
        return createHelpMessage();
    }
}