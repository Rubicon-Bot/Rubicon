package fun.rubicon.features;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.UnavailableCommandHandler;
import fun.rubicon.listener.VerificationListener;
import fun.rubicon.sql.MySQL;
import fun.rubicon.sql.VerificationKickSQL;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.entities.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TimerTask;

/**
 * Rubicon Discord bot
 *
 * @author Edited copy of fun.rubicon.features.RemindHandler (Michael Rittemsiter / Schlaubi)
 * @copyright Rubicon Dev Team 2018
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.features
 */
public class VerficationKickHandler {

    public static Set<VerifyKick> verifyKicks = new HashSet<>();


    public static class VerifyKick{
        private final long guildid;
        private final long userid;
        private final String kickText;
        private final long kickDate;
        private final long messageId;
        private final TimerTask resolveTask = new TimerTask() {
            @Override
            public void run() {
                schedule();
            }
        };


        public VerifyKick(Guild guild, User user, Date kickDate, String kicktext, long messageid){
            this.guildid = guild.getIdLong();
            this.userid = user.getIdLong();
            this.messageId = messageid;
            this.kickDate = kickDate.getTime();
            this.kickText = kicktext;

            this.save();

            if(kickDate.before(new Date()))
                this.schedule();
            else
                RubiconBot.getTimer().schedule(resolveTask, new Date(this.kickDate));
            verifyKicks.add(this);
        }

        public static VerifyKick fromMember(Member member){
            if(!exits(member)) return null;
            VerificationKickSQL sql = new VerificationKickSQL(member.getUser(), member.getGuild());
            return new VerifyKick(RubiconBot.getJDA().getGuildById(sql.get("guildid")), RubiconBot.getJDA().getUserById(sql.get("userid")),new Date(Long.parseLong(sql.get("kickdate"))), sql.get("kicktext"), Long.parseLong(sql.get("message")));
        }

        private void schedule(){
            if(!verifyKicks.contains(this)) return;
            Guild guild = RubiconBot.getJDA().getGuildById(this.guildid);
            Member member = guild.getMemberById(this.userid);
            member.getUser().openPrivateChannel().queue(c -> c.sendMessage(this.kickText.replace("%invite%", guild.getTextChannelById(RubiconBot.getMySQL().getVerificationValue(guild, "channelid")).createInvite().setMaxUses(1).complete().getURL())).queue());
            guild.getController().kick(member).reason(this.kickText).queue();
            VerificationKickSQL sql = new VerificationKickSQL(member.getUser(), member.getGuild());
            guild.getTextChannelById(RubiconBot.getMySQL().getVerificationValue(guild, "channelid")).getMessageById(Long.parseLong(sql.get("message"))).complete().delete().queue();
            this.remove();
            verifyKicks.remove(this);
        }

        public boolean save(){
            try {
                PreparedStatement saveStatement = MySQL.getConnection().prepareStatement("INSERT INTO `verifykicks` (" +
                        "'guildid','userid', 'kickText', 'kicktime', 'message')" +
                        "VALUES (?,?,?,?,?)");
                saveStatement.setLong(1, this.guildid);
                saveStatement.setLong(2, this.userid);
                saveStatement.setString(3, this.kickText);
                saveStatement.setLong(4, this.kickDate);
                saveStatement.setLong(5, this.messageId);
            } catch (SQLException e){
                Logger.error(e);
                return false;
            }
            return true;
        }

        public boolean remove(){
            try{
                PreparedStatement deleteStatement = MySQL.getConnection().prepareStatement("DELETE FROM `verifykicks` WHERE `userid` =? AND `guildid` = ?");
                deleteStatement.setLong(1, this.userid);
                deleteStatement.setLong(2, this.guildid);
                deleteStatement.execute();
            } catch (SQLException e){
                Logger.error(e);
                return false;
            }
            return true;
        }

        public boolean exits(){
            try{
                PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT * FROM `verifykicks`WHERE `userid` = ? AND `guildi` = ?");
                ps.setLong(1, this.userid);
                ps.setLong(2, this.guildid);
                ResultSet rs = ps.executeQuery();
                return rs.next();
            } catch (SQLException e){
                Logger.error(e);
                return false;
            }
        }

        public static boolean exits(Member member){
            try{
                PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT * FROM `verifykicks`WHERE `userid` = ? AND `guildi` = ?");
                ps.setLong(1, member.getUser().getIdLong());
                ps.setLong(2, member.getGuild().getIdLong());
                ResultSet rs = ps.executeQuery();
                return rs.next();
            } catch (SQLException e){
                Logger.error(e);
                return false;
            }
        }

        public static void loadVerifyKicks(){
            try {
                for (Guild guild : RubiconBot.getJDA().getGuilds()) {
                    for (Member member : guild.getMembers()) {
                        PreparedStatement selectStatement = MySQL.getConnection()
                                .prepareStatement("SELECT * FROM `verifykicks` WHERE `guildid` =?;");
                        selectStatement.setLong(1, guild.getIdLong());
                        ResultSet channelResult = selectStatement.executeQuery();
                        while (channelResult.next())
                            new VerifyKick(guild,
                                    RubiconBot.getJDA().getUserById(channelResult.getString("userid")),
                                    new Date(Long.parseLong(channelResult.getString("kickDate"))),
                                    channelResult.getString("kicktetxt"), channelResult.getLong("message"));
                    }
                }
            } catch (SQLException e) {
                Logger.error("Could not load verifykicks, disabling verification feature");
                RubiconBot.getJDA().removeEventListener(new VerificationListener());
                Logger.error(e);
            }
        }

    }




}
