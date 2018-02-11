/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.listener;

import fun.rubicon.RubiconBot;
import fun.rubicon.commands.admin.CommandVerification;
import fun.rubicon.features.VerificationKickHandler;
import fun.rubicon.features.VerificationUserHandler;
import fun.rubicon.util.SafeMessage;
import fun.rubicon.util.StringUtil;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Michael Rittmeister / Schlaubi
 */
public class VerificationListener extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if(event.getAuthor().isBot()) return;
        if (!CommandVerification.setups.containsKey(event.getGuild())) return;
        if (!CommandVerification.setups.get(event.getGuild()).author.equals(event.getAuthor())) return;
        CommandVerification.VerificationSetup setup = CommandVerification.setups.get(event.getGuild());
        Message message = setup.message;
        Message response = event.getMessage();
        response.delete().queue();
        if (setup.step == 1)
            CommandVerification.setupStepOne(message, response);
        else if (setup.step == 2)
            CommandVerification.setupStepTwo(message, response);
        else if (setup.step == 3)
            CommandVerification.setupStepThree(message, response);
        else if (setup.step == 5)
            CommandVerification.setupStepFive(message, response);
        else if (setup.step == 6)
            CommandVerification.setupStepSix(message, response);
        else if (setup.step == 7)
            CommandVerification.setupStepSeven(message, response);
    }

    /**
     * Deactivate verification when channel got deleted
     */
    @Override
    public void onTextChannelDelete(TextChannelDeleteEvent event) {
        if (!RubiconBot.getMySQL().verificationEnabled(event.getGuild())) return;
        if (!RubiconBot.getMySQL().getVerificationValue(event.getGuild(), "channelid").equals(event.getChannel().getId()))
            return;

        RubiconBot.getMySQL().deleteGuildVerification(event.getGuild());
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        if(event.getUser().isBot()) return;
        if (!RubiconBot.getMySQL().verificationEnabled(event.getGuild())) return;
        if (event.getUser().isBot())
            return;
        TextChannel channel = event.getGuild().getTextChannelById(RubiconBot.getMySQL().getVerificationValue(event.getGuild(), "channelid"));
        Message message = SafeMessage.sendMessageBlocking(channel, RubiconBot.getMySQL().getVerificationValue(event.getGuild(), "text").replace("%user%", event.getUser().getAsMention()).replace("%guild%", event.getGuild().getName()));
        CommandVerification.users.put(message, event.getUser());

        String emoteRaw = RubiconBot.getMySQL().getVerificationValue(event.getGuild(), "emote");
        if (!StringUtil.isNumeric(emoteRaw))
            message.addReaction(emoteRaw).queue();
        else
            message.addReaction(event.getJDA().getEmoteById(emoteRaw)).queue();
        int delay = Integer.parseInt(RubiconBot.getMySQL().getVerificationValue(event.getGuild(), "kicktime"));
        if (delay == 0) return;
        new VerificationUserHandler.VerifyUser(event.getMember(), message);
        new VerificationKickHandler.VerifyKick(event.getGuild(), event.getMember(), getKickTime(delay), RubiconBot.getMySQL().getVerificationValue(event.getGuild(), "kicktext").replace("%guild%", event.getGuild().getName()), message.getIdLong(), false, true);
    }

    @Override
    public void onGuildMemberLeave(GuildMemberLeaveEvent event) {
        if(event.getUser().isBot()) return;
        if (VerificationKickHandler.VerifyKick.exists(event.getMember())) {
            VerificationKickHandler.VerifyKick kick = VerificationKickHandler.VerifyKick.fromMember(event.getMember(), true);
            event.getJDA().getTextChannelById(RubiconBot.getMySQL().getVerificationValue(event.getGuild(), "channelid")).getMessageById(kick.getMessageId()).complete().delete().queue();
            kick.remove();
        }
    }


    private Date getKickTime(int mins) {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int minutes = calendar.get(Calendar.MINUTE) + mins;
        calendar.set(Calendar.MINUTE, minutes);
        return calendar.getTime();
    }
}
