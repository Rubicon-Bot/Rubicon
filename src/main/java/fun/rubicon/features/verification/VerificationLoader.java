package fun.rubicon.features.verification;

import com.rethinkdb.net.Cursor;
import fun.rubicon.RubiconBot;
import fun.rubicon.core.entities.RubiconGuild;
import fun.rubicon.io.deprecated_rethink.Rethink;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

import java.util.*;

/**
 * @author Schlaubi / Michael Rittmeister
 */

public class VerificationLoader {

    public HashMap<Member, Message> getUserStorage() {
        return userStorage;
    }

    private HashMap<Member, Message> userStorage = new HashMap<>();

    public void loadVerificationCache() {
        new Thread(() -> {
            Cursor cursor = RubiconBot.getRethink().db.table("verification_users").run(RubiconBot.getRethink().getConnection());
            for (Object obj : cursor) {
                Map map = (Map) obj;
                Guild guild = RubiconBot.getShardManager().getGuildById((String) map.get("guildId"));
                RubiconGuild rubiconGuild = RubiconGuild.fromGuild(guild);
                if(rubiconGuild == null) continue;
                Member member = guild.getMemberById((String) map.get("userId"));
                if (member == null) continue;
                try {
                    userStorage.put(member, rubiconGuild.getVerificationChannel().getMessageById((String) map.get("messageId")).complete());
                } catch (NullPointerException ignored) {
                    //Channel is null
                    continue;
                }
                Rethink rethink = RubiconBot.getRethink();
                if ((long) map.get("expiry") != 1L) {
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            try {
                                if (member.getRoles().contains(rubiconGuild.getVerificationRole())) return;
                                if (rubiconGuild.getGuild().getSelfMember().hasPermission(Permission.KICK_MEMBERS, Permission.CREATE_INSTANT_INVITE)) {
                                    member.getUser().openPrivateChannel().complete().sendMessage(rubiconGuild.getVerificationKickText().replace("%user%", member.getUser().getAsMention()).replace("%guild%", guild.getName()).replace("%invite%", rubiconGuild.getVerificationChannel().createInvite().setMaxUses(1).complete().getURL())).queue();
                                    guild.getController().kick(member).reason("Rules not accepted").queue();
                                }
                                rethink.db.table("verification_users").filter(rethink.rethinkDB.hashMap("guildId", guild.getId()).with("userId", member.getUser().getId())).delete().run(rethink.getConnection());
                            } catch (Exception ignored) {
                            }
                        }
                    }, new Date((long) map.get("expiry")));
                } else {
                    rethink.db.table("verification_users").filter(rethink.rethinkDB.hashMap("guildId", guild.getId()).with("userId", member.getUser().getId())).delete().run(rethink.getConnection());
                }
            }
        }, "VerificationCacheLoadingThread").start();
    }


}
