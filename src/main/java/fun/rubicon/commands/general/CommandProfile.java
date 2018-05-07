package fun.rubicon.commands.general;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.core.entities.RubiconUser;
import fun.rubicon.core.translation.TranslationUtil;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.Colors;
import fun.rubicon.util.DateUtil;
import fun.rubicon.util.Info;
import fun.rubicon.util.SafeMessage;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;

import java.util.Arrays;

/**
 * @author Leon Kappes / Lee
 * @copyright RubiconBot Dev Team 2018
 * @License GPL-3.0 License <http://rubicon.fun/license>
 */public class CommandProfile extends CommandHandler {

    public CommandProfile(){
        super(new String[]{"profile", "user", "level", "profil"}, CommandCategory.GENERAL, new PermissionRequirements("command.profile", false, true), "Displays the bio, money and level of a user.", "" +
                "\n" +
                "[@User]");
    }
    
    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        //profile
        if (invocation.getArgs().length == 0) {
            generateProfile(invocation.getMember(), invocation.getTextChannel());
            return null;
        }

        //profile @User
        if (invocation.getMessage().getMentionedUsers().size() == 1) {
            Member mentionedMember = invocation.getGuild().getMember(invocation.getMessage().getMentionedUsers().get(0));
            if (invocation.getArgs().length == 1) {
                generateProfile(mentionedMember, invocation.getTextChannel());
                return null;
            }
        }
        return null;
    }

    private void generateProfile(Member member, TextChannel textChannel) {
        User user = member.getUser();
        RubiconUser rubiconUser = RubiconUser.fromUser(user);

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Colors.COLOR_PRIMARY);
        embedBuilder.setAuthor(member.getEffectiveName() + "'s profile", null, member.getUser().getAvatarUrl());
        if (Arrays.asList(Info.BOT_AUTHOR_IDS).contains(member.getUser().getIdLong())) {
            embedBuilder.addField("VIP", "Official RubiconBot Developer", false);
        } else {
            if (Arrays.asList(Info.COMMUNITY_STAFF_TEAM).contains(member.getUser().getIdLong()))
                embedBuilder.addField("VIP", "RubiconBot Community Staff", false);
        }
        embedBuilder.setDescription(rubiconUser.getBio());
        embedBuilder.addField("Money", "Balance: " + rubiconUser.getMoney() + " Rubys", true);
        embedBuilder.addField("Premium", (rubiconUser.isPremium()) ? "Until " + DateUtil.formatDate(rubiconUser.getPremiumExpiryDate(),TranslationUtil.translate(user,"date.format")) : "No premium", true);
        //embedBuilder.addField("Level", buildProgressBar(rubiconUser), false);
        SafeMessage.sendMessage(textChannel,embedBuilder.build(),300000);
    }

    /*private String buildProgressBar(RubiconUser user) {
        int currentPoints = Integer.parseInt(sql.get("points"));
        int currentLevel = Integer.parseInt(sql.get("level"));
        int requiredPoints = MemberLevelListener.getRequiredPointsByLevel(currentLevel);
        int percent = (currentPoints * 100) / requiredPoints;
        return "Level: " + currentLevel + "\n" +
                "Points: " + currentPoints + "/" + requiredPoints + " (" + percent + "%)";
    }*/

}