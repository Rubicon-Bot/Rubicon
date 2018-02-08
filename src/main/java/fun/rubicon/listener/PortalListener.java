/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.listener;

import fun.rubicon.RubiconBot;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.Webhook;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.webhook.WebhookClient;
import net.dv8tion.jda.webhook.WebhookClientBuilder;
import net.dv8tion.jda.webhook.WebhookMessage;
import net.dv8tion.jda.webhook.WebhookMessageBuilder;

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

                        for (Webhook hook : otherChannel.getWebhooks().complete()) {
                            if (hook.getName().equals("rubicon-portal-hook")) {
                                webhook = hook;
                                break;
                            }
                        }
                        if (webhook == null) {
                            webhook = otherChannel.createWebhook("rubicon-portal-hook").complete();
                        }
                        WebhookClientBuilder clientBuilder = webhook.newClient();
                        WebhookClient client = clientBuilder.build();

                        WebhookMessageBuilder builder = new WebhookMessageBuilder();
                        builder.setContent(e.getMessage().getContentDisplay().replace("@here", "@ here").replace("@everyone", "@ everyone"));
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
