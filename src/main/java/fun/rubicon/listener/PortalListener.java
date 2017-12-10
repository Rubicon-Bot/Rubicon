package fun.rubicon.listener;

import fun.rubicon.RubiconBot;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.entities.Icon;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.Webhook;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.webhook.WebhookClient;
import net.dv8tion.jda.webhook.WebhookClientBuilder;
import net.dv8tion.jda.webhook.WebhookMessage;
import net.dv8tion.jda.webhook.WebhookMessageBuilder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

/**
 * Rubicon Discord bot
 *
 * @author Yannick Seeger / ForYaSee
 * @copyright RubiconBot Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.listener
 */
public class PortalListener extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if (!e.getAuthor().isBot()) {
            if (e.getChannel().getId().equals(RubiconBot.getMySQL().getPortalValue(e.getGuild(), "channelid"))) {
                String status = RubiconBot.getMySQL().getGuildValue(e.getGuild(), "portal");
                if (status.contains("open")) {
                    TextChannel otherChannel = e.getJDA().getTextChannelById(RubiconBot.getMySQL().getPortalValue(e.getJDA().getGuildById(RubiconBot.getMySQL().getPortalValue(e.getGuild(), "partnerid")), "channelid"));
                    try {
                        Webhook webhook = null;

                        for(Webhook hook : otherChannel.getWebhooks().complete()) {
                            if(hook.getName().equals("rubicon-portal-hook")) {
                                webhook = hook;
                                break;
                            }
                        }
                        if(webhook == null) {
                            webhook = otherChannel.createWebhook("rubicon-portal-hook").complete();
                        }
                        WebhookClientBuilder clientBuilder = webhook.newClient();
                        WebhookClient client = clientBuilder.build();

                        WebhookMessageBuilder builder = new WebhookMessageBuilder();
                        builder.setContent(e.getMessage().getContent());
                        builder.setAvatarUrl(e.getAuthor().getAvatarUrl());
                        builder.setUsername(e.getAuthor().getName());
                        WebhookMessage message = builder.build();
                        client.send(message);
                        client.close();

                        /*EmbedBuilder builder = new EmbedBuilder();
                        builder.setAuthor(e.getAuthor().getName(), null, e.getAuthor().getEffectiveAvatarUrl());
                        builder.setDescription(e.getMessage().getContent());
                        otherChannel.sendMessage(builder.build()).queue();*/
                    } catch (NullPointerException fuck) {
                        fuck.printStackTrace();
                    }
                }
            }
        }
    }
}
