package fun.rubicon.commands.general;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.data.PermissionLevel;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import fun.rubicon.listener.MemberLevelListener;
import fun.rubicon.sql.MemberSQL;
import fun.rubicon.sql.UserSQL;
import fun.rubicon.util.Colors;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class CommandProfile extends CommandHandler {

    public CommandProfile() {
        super(new String[]{"profile", "user"}, CommandCategory.GENERAL, new PermissionRequirements(PermissionLevel.EVERYONE, "command.profile"), "Displays the bio, money and level of a user.", "" +
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
        embedBuilder.setDescription(userSQL.get("bio"));
        embedBuilder.addField("Money", "Balance: " + userSQL.get("money") + " Rubys", true);
        embedBuilder.addField("Premium", (isPremium(userSQL)) ? "Until " + CommandPremium.parsePremiumEntry(userSQL.get("premium")) : "No premium", true);
        embedBuilder.addField("Level", buildProgressBar(memberSQL), false);
        textChannel.sendMessage(EmbedUtil.message(embedBuilder)).queue();
    }

    private String buildProgressBar(MemberSQL sql) {
        int currentPoints = Integer.parseInt(sql.get("points"));
        int currentLevel = Integer.parseInt(sql.get("level"));
        int requiredPoints = MemberLevelListener.getRequiredPointsByLevel(currentLevel);
        int percent = (currentPoints * 100) / requiredPoints;
        return "Level: " + currentLevel + "\n" +
                "Points: " + currentPoints + "/" + requiredPoints + " (" + percent + "%)";
    }

    private boolean isPremium(UserSQL sql) {
        String entry = sql.get("premium");
        if (entry.equalsIgnoreCase("false"))
            return false;
        return true;
    }
}
