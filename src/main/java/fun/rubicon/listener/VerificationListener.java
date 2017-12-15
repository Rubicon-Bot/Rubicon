package fun.rubicon.listener;

import fun.rubicon.RubiconBot;
import fun.rubicon.commands.admin.CommandVerification;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Rubicon Discord bot
 *
 * @author Michael Rittmeister / Schlaubi
 * @copyright Rubicon Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.listener
 */
public class VerificationListener extends ListenerAdapter{

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if(!CommandVerification.setups.containsKey(event.getGuild())) return;
        if(!CommandVerification.setups.get(event.getGuild()).author.equals(event.getAuthor())) return;
        CommandVerification.VerificationSetup setup = CommandVerification.setups.get(event.getGuild());
        Message message = setup.message;
        Message response = event.getMessage();
        response.delete().queue();
        if(setup.step == 1)
            CommandVerification.setupStepOne(message, response);
        else if (setup.step == 2)
            CommandVerification.setupStepTwo(message, response);
        else if (setup.step == 3)
            CommandVerification.setupStepThree(message, response);
        else if (setup.step == 4)
            CommandVerification.setupStepFour(message, response);
        else if(setup.step == 5)
            CommandVerification.setupStepFive(message, response);
        else if (setup.step == 6)
            CommandVerification.setupStepSix(message, response);
    }

    /**
     * Deactivate verification when channel got deleted
     */
    @Override
    public void onTextChannelDelete(TextChannelDeleteEvent event) {
        if(!RubiconBot.getMySQL().verificationEnabled(event.getGuild())) return;
        if(!RubiconBot.getMySQL().getVerificationValue(event.getGuild(), "channelid").equals(event.getChannel().getId())) return;

        RubiconBot.getMySQL().deleteGuildVerification(event.getGuild());
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        if(!RubiconBot.getMySQL().verificationEnabled(event.getGuild())) return;
        TextChannel channel = event.getGuild().getTextChannelById(RubiconBot.getMySQL().getVerificationValue(event.getGuild(), "channelid"));
        Message message = channel.sendMessage(new EmbedBuilder().setDescription(RubiconBot.getMySQL().getVerificationValue(event.getGuild(), "text").replace("%user%", event.getUser().getAsMention())).build()).complete();
        message.addReaction("✅").queue();
        Role verified = event.getGuild().getRoleById(RubiconBot.getMySQL().getVerificationValue(event.getGuild(), "roleid"));
        int delay = Integer.parseInt(RubiconBot.getMySQL().getVerificationValue(event.getGuild(), "kicktime"));
        if(delay == 0) return;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if(!event.getMember().getRoles().contains(verified)){
                    event.getUser().openPrivateChannel().complete().sendMessage(RubiconBot.getMySQL().getVerificationValue(event.getGuild(), "kicktext").replace("%user%", event.getUser().getAsMention())).queue();
                    event.getGuild().getController().kick(event.getMember()).queue();
                }
            }
        }, delay * 1000 * 60);
    }
}
