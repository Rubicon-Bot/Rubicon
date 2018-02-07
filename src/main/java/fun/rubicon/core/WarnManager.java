package fun.rubicon.core;

import fun.rubicon.sql.WarnSQL;
import fun.rubicon.util.Colors;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class WarnManager {

    public static void addWarn(User warnedUser, Guild guild, User executor, String reason) {
        Warn warn = new Warn(0, warnedUser, guild, executor, reason, new Date().getTime());
        new WarnSQL().addWarn(warn);
    }

    public static void removeWarn(User user, Guild guild, int index) {
        new WarnSQL().deleteWarn(user, guild, index);
    }

    public static EmbedBuilder listWarns(User user, Guild guild) {
        String[] emotes = {
                ":one:",
                ":two:",
                ":three:",
                ":four:",
                ":five:",
                ":six:",
                ":seven:",
                ":eight:",
                ":nine:",
                ":keycap_ten:"
        };
        List<Warn> warnList = new WarnSQL().getWarns(user, guild);
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setAuthor(user.getName() + "'s warns", null, user.getAvatarUrl());
        embedBuilder.setColor(Colors.COLOR_PRIMARY);
        if (warnList.size() == 0) {
            embedBuilder.setDescription("User has no warns.");
        } else {
            int warnCount = 0;
            for (Warn warn : warnList) {
                embedBuilder.addField(emotes[warnCount] + " " + warn.getReason(), "Executor: " + warn.getExecutor().getName() + "\n" +
                        "Date: " + new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(warn.getDate()), false);
                warnCount++;
                if (warnCount == 10)
                    break;
            }
        }
        return embedBuilder;
    }

    public static boolean isWarned(User user, Guild guild) {
        List<Warn> warnList = new WarnSQL().getWarns(user, guild);
        if (warnList.size() == 0)
            return false;
        return true;
    }
}