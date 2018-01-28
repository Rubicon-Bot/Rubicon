package fun.rubicon.commands.general;

import fun.rubicon.RubiconBot;
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
import fun.rubicon.util.Info;
<<<<<<< HEAD
=======
import fun.rubicon.util.Logger;
>>>>>>> b15f36abd526cbd4fa18fd10e6cd5efbf9da9d6c
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;

import java.util.Arrays;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class CommandProfile extends CommandHandler {

    public CommandProfile() {
        super(new String[]{"profile", "user", "level"}, CommandCategory.GENERAL, new PermissionRequirements(PermissionLevel.EVERYONE, "command.profile"), "Displays the bio, money and level of a user.", "" +
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
        Emote staff = RubiconBot.getJDA().getGuildById("380415148545802250").getEmoteById("406484992240386048");

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Colors.COLOR_PRIMARY);
        if(Arrays.asList(Info.BOT_AUTHOR_IDS).contains(user.getIdLong())) {
            embedBuilder.setAuthor(member.getEffectiveName() + "'s profile (Rubicon developer)", null, member.getUser().getAvatarUrl());
        } else
            embedBuilder.setAuthor(member.getEffectiveName() + "'s profile", null, member.getUser().getAvatarUrl());
        embedBuilder.setDescription(userSQL.get("bio"));
        if (Arrays.asList(Info.BOT_AUTHOR_IDS).contains(member.getUser().getIdLong())) {
            embedBuilder.addField("VIP", "Official RubiconBot Developer", false);
        } else if (Arrays.asList(Info.COMMUNITY_STAFF_TEAM).contains(member.getUser().getIdLong())) {
            embedBuilder.addField("VIP", "RubiconBot Community Staff", false);
        }
        embedBuilder.addField("Money", "Balance: " + userSQL.get("money") + " Rubys", true);
        embedBuilder.addField("Premium", (userSQL.isPremium()) ? "Until " + CommandPremium.parsePremiumEntry(userSQL.get("premium")) : "No premium", true);
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
<<<<<<< HEAD

    private boolean isPremium(UserSQL sql) {
        String entry = sql.get("premium");
        if (entry.equalsIgnoreCase("false"))
            return false;
        return true;
    }
    private String getEomjiInMessage(Emote emote) {
        return String.format("<:%s:%s>", emote.getName(), emote.getId());
    }
=======
>>>>>>> b15f36abd526cbd4fa18fd10e6cd5efbf9da9d6c
}
