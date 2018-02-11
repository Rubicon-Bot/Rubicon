package fun.rubicon.commands.general;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.listener.MemberLevelListener;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.sql.MemberSQL;
import fun.rubicon.sql.UserSQL;
import fun.rubicon.util.Colors;
import fun.rubicon.util.EmbedUtil;
import fun.rubicon.util.Info;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class CommandProfile extends CommandHandler {

    public CommandProfile() {
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
            if (invocation.getArgs().length == mentionedMember.getEffectiveName().split(" ").length) {
                generateProfile(mentionedMember, invocation.getTextChannel());
                return null;
            }
        }
        return null;
    }

    private void generateProfile(Member member, TextChannel textChannel) {
        User user = member.getUser();
        MemberSQL memberSQL = new MemberSQL(member);
        UserSQL userSQL = new UserSQL(user);

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Colors.COLOR_PRIMARY);
        embedBuilder.setAuthor(member.getEffectiveName() + "'s profile", null, member.getUser().getAvatarUrl());
        if (Arrays.asList(Info.BOT_AUTHOR_IDS).contains(member.getUser().getIdLong())) {
            embedBuilder.addField("VIP", "Official RubiconBot Developer", false);
        } else if (Arrays.asList(Info.COMMUNITY_STAFF_TEAM).contains(member.getUser().getIdLong())) {
            embedBuilder.addField("VIP", "RubiconBot Community Staff", false);
        }
        embedBuilder.setDescription(userSQL.get("bio"));
        embedBuilder.addField("Money", "Balance: " + userSQL.get("money") + " Rubys", true);
        embedBuilder.addField("Premium", (userSQL.isPremium()) ? "Until " + userSQL.formatExpiryDate() : "No premium", true);
        embedBuilder.addField("Level", buildProgressBar(memberSQL), false);
        textChannel.sendMessage(EmbedUtil.message(embedBuilder)).queue(message -> message.delete().queueAfter(5, TimeUnit.MINUTES));
    }

    private String buildProgressBar(MemberSQL sql) {
        int currentPoints = Integer.parseInt(sql.get("points"));
        int currentLevel = Integer.parseInt(sql.get("level"));
        int requiredPoints = MemberLevelListener.getRequiredPointsByLevel(currentLevel);
        int percent = (currentPoints * 100) / requiredPoints;
        return "Level: " + currentLevel + "\n" +
                "Points: " + currentPoints + "/" + requiredPoints + " (" + percent + "%)";
    }

}
