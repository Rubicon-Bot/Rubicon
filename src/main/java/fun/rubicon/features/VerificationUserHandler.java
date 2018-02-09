package fun.rubicon.features;

import fun.rubicon.RubiconBot;
import fun.rubicon.commands.admin.CommandVerification;
import fun.rubicon.sql.MySQL;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class VerificationUserHandler {

    public static HashMap<Member, VerifyUser> users = new HashMap<>();
    static Connection connection = MySQL.getConnection();

    public static class VerifyUser {
        private final long guildid;
        private final long userid;
        private final long messageid;
        private final Runnable resolveTask = new Runnable() {
            @Override
            public void run() {
                run();
            }
        };

        public VerifyUser(Member member, Message message) {
            this.guildid = member.getGuild().getIdLong();
            this.userid = member.getUser().getIdLong();
            this.messageid = message.getIdLong();
            CommandVerification.users.put(message, member.getUser());
            this.save();
            users.put(member, this);
            CommandVerification.users.put(message, member.getUser());
        }

        public static VerifyUser fromMember(Member member) {
            return users.get(member);
        }


        private boolean save() {
            try {
                PreparedStatement ps = connection.prepareStatement("INSERT INTO `verifyusers` (`guildid`, `userid`, `messageid`) VALUES (?,?,?)");
                ps.setLong(1, this.guildid);
                ps.setLong(2, this.userid);
                ps.setLong(3, this.messageid);
                ps.execute();
                return true;
            } catch (SQLException e) {
                Logger.error(e);
                return false;
            }
        }

        public boolean remove() {
            try {
                PreparedStatement ps = connection.prepareStatement("DELETE FROM `verifyusers` WHERE `userid` =? AND guildid =?");
                ps.setLong(1, this.userid);
                ps.setLong(2, this.guildid);
                ps.execute();
                return true;
            } catch (SQLException e) {
                Logger.error(e);
                return false;
            }
        }

    }

    public static void loadVerifyUser() {
        try {
            PreparedStatement selectStatement = MySQL.getConnection()
                    .prepareStatement("SELECT * FROM `verifyusers` ");
            ResultSet channelResult = selectStatement.executeQuery();
            while (channelResult.next()) {
                Guild guild = RubiconBot.getJDA().getGuildById(channelResult.getString("guildid"));
                Member member = guild.getMemberById(channelResult.getLong("userid"));
                Message message = guild.getTextChannelById(RubiconBot.getMySQL().getVerificationValue(guild, "channelid")).getMessageById(channelResult.getString("messageid")).complete();
                new VerifyUser(member, message);
            }
        } catch (SQLException e) {
            Logger.error("Could not load verifykicks!");
            Logger.error(e);
        }
    }
}
