package fun.rubicon.listener.feature;

import com.rethinkdb.net.Cursor;
import fun.rubicon.RubiconBot;
import fun.rubicon.core.entities.RubiconGuild;
import fun.rubicon.rethink.Rethink;
import fun.rubicon.util.SafeMessage;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * @author Schlaubi / Michael Rittmeister
 */

public class VerificationListener extends ListenerAdapter {

    private Rethink rethink = RubiconBot.getRethink();

    private HashMap<Member, Message> userStorage = RubiconBot.getVerificationLoader().getUserStorage();


    @Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
        if (event.getUser().isBot() || event.getUser().isFake()) return;
        RubiconGuild rubiconGuild = RubiconGuild.fromGuild(event.getGuild());
        if (rubiconGuild.isVerificationEnabled()) {
            if (!userStorage.containsKey(event.getMember())) return;
            if (event.getMessageId().equals(userStorage.get(event.getMember()).getId())) {
                if (!checkEmote(event)) return;
                Role role = event.getGuild().getRoleById(getDatabaseValue(event.getGuild(), "roleId"));
                if (!event.getGuild().getSelfMember().canInteract(role) || !event.getGuild().getSelfMember().hasPermission(Permission.MANAGE_ROLES)) {
                    event.getGuild().getOwner().getUser().openPrivateChannel().complete().sendMessage("Verification error: Rubicon has no permission to interact with " + role.getAsMention()).queue();
                    return;
                }
                event.getGuild().getController().addRolesToMember(event.getMember(), role).queue();
                String verifiedText = getDatabaseValue(rubiconGuild.getGuild(), "verifiedText").replace("%user%", event.getUser().getAsMention()).replace("%guild%", event.getGuild().getName());
                event.getChannel().getMessageById(event.getMessageId()).complete().editMessage(verifiedText).complete().delete().queueAfter(10, TimeUnit.SECONDS);
                rethink.db.table("verification_users").filter(rethink.rethinkDB.hashMap("guildId", event.getGuild().getId()).with("userId", event.getUser().getId())).delete().run(rethink.getConnection());
            }
        }
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        if(event.getUser().isBot()) return;
        RubiconGuild rubiconGuild = RubiconGuild.fromGuild(event.getGuild());
        if (rubiconGuild.isVerificationEnabled()) {
            TextChannel verifyChannel = event.getGuild().getTextChannelById(getDatabaseValue(rubiconGuild.getGuild(), "channelId"));
            if (verifyChannel == null) return;
            String welcomeText = getDatabaseValue(rubiconGuild.getGuild(), "welcomeText").replace("%user%", event.getUser().getAsMention()).replace("%guild%", event.getGuild().getName());
            String emoteRaw = getDatabaseValue(rubiconGuild.getGuild(), "emote");
            Message verifyMsg = SafeMessage.sendMessageBlocking(verifyChannel, welcomeText);
            Date expiry = null;
            if (isEmojiCustom(event.getGuild(), emoteRaw))
                verifyMsg.addReaction(event.getGuild().getEmoteById(emoteRaw)).queue();
            else {
                verifyMsg.addReaction(emoteRaw).queue();
            }
            if (!getDatabaseValue(event.getGuild(), "kickTime").equals("0")) {
                if (!event.getGuild().getSelfMember().hasPermission(Permission.KICK_MEMBERS)) {
                    event.getGuild().getOwner().getUser().openPrivateChannel().complete().sendMessage("Verification error: Rubicon has no permission to kick users").queue();
                    return;
                }
                if (!event.getGuild().getSelfMember().canInteract(event.getMember())) {
                    event.getGuild().getOwner().getUser().openPrivateChannel().complete().sendMessage("Verification error: Rubicon has no permission to kick " + event.getMember().getAsMention()).queue();
                    return;
                }
                expiry = getExpiry(Integer.parseInt(getDatabaseValue(event.getGuild(), "kickTime")));
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (event.getMember().getRoles().contains(event.getGuild().getRoleById(getDatabaseValue(event.getGuild(), "roleId"))))
                            return;
                        if (rubiconGuild.getGuild().getSelfMember().hasPermission(Permission.KICK_MEMBERS, Permission.CREATE_INSTANT_INVITE)) {
                            event.getUser().openPrivateChannel().complete().sendMessage(getDatabaseValue(event.getGuild(), "kickText").replace("%user%", event.getUser().getAsMention()).replace("%guild%", event.getGuild().getName()).replace("%invite%", getInviteURL(event.getGuild()))).queue();
                            event.getGuild().getController().kick(event.getMember()).reason("Rules not accepted").queue();
                        }
                    }
                }, expiry);
            }
            userStorage.put(event.getMember(), verifyMsg);
            rethink.db.table("verification_users").insert(rethink.rethinkDB.hashMap("guildId", event.getGuild().getId()).with("userId", event.getUser().getId()).with("messageId", verifyMsg.getId()).with("expiry", expiry == null ? 1L : expiry.getTime())).run(rethink.getConnection());
        }
    }

    private boolean isEmojiCustom(Guild guild, String id) {
        try {
            return guild.getEmoteById(id) != null;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean checkEmote(GuildMessageReactionAddEvent event) {
        String emoteRaw = getDatabaseValue(event.getGuild(), "emote");
        if (event.getReactionEmote().getId() != null) {
            return emoteRaw.equals(event.getReactionEmote().getId());
        } else {
            return emoteRaw.equals(event.getReactionEmote().getName());
        }
    }

    private String getDatabaseValue(Guild guild, String key) {
        Cursor cursor = rethink.db.table("verification_settings").filter(rethink.rethinkDB.hashMap("guildId", guild.getId())).run(rethink.getConnection());
        Map map = ((Map) cursor.toList().get(0));
        return (String) map.get(key);
    }

    private Date getExpiry(int minutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) + minutes);
        return cal.getTime();
    }

    private String getInviteURL(Guild guild) {
        TextChannel channel = guild.getTextChannelById(getDatabaseValue(guild, "channelId"));
        if (!guild.getSelfMember().hasPermission(channel, Permission.CREATE_INSTANT_INVITE))
            return "No permission";
        return channel.createInvite().setMaxUses(1).complete().getURL();
    }

}
