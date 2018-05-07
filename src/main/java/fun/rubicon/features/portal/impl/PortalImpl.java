package fun.rubicon.features.portal.impl;

import com.rethinkdb.gen.ast.Filter;
import com.rethinkdb.gen.ast.Table;
import com.sun.security.auth.callback.TextCallbackHandler;
import fun.rubicon.RubiconBot;
import fun.rubicon.core.entities.RubiconGuild;
import fun.rubicon.features.portal.Portal;
import fun.rubicon.rethink.Rethink;
import fun.rubicon.util.Colors;
import fun.rubicon.util.EmbedUtil;
import fun.rubicon.util.Logger;
import fun.rubicon.util.SafeMessage;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.webhook.WebhookClient;
import net.dv8tion.jda.webhook.WebhookClientBuilder;
import net.dv8tion.jda.webhook.WebhookMessageBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ForYaSee / Yannick Seeger
 */
public class PortalImpl implements Portal {

    private final Rethink rethink;
    private final Table table;
    private final String rawRootGuild;
    private final String rawRootChannel;
    private final HashMap<String, String> rawMembers;
    private final Filter dbPortal;

    public PortalImpl(String rawRootGuild, String rawRootChannel, HashMap<String, String> rawMembers) {
        this.rawRootGuild = rawRootGuild;
        this.rawRootChannel = rawRootChannel;
        this.rawMembers = rawMembers;
        rethink = RubiconBot.getRethink();
        table = rethink.db.table("portals");
        dbPortal = table.filter(rethink.rethinkDB.hashMap("root_guild", rawRootGuild));

        //Check for null guilds
        for (Map.Entry<String, String> entry : rawMembers.entrySet()) {
            Guild guild = RubiconBot.getShardManager().getGuildById(entry.getKey());
            TextChannel channel = RubiconBot.getShardManager().getTextChannelById(entry.getValue());

            if (guild == null || channel == null) {
                removeGuild(entry.getKey());
            }
        }
        if (getRootGuild() == null || getRootChannel() == null) {
            delete("The owner closed the portal.");
        }
    }

    @Override
    public Guild getRootGuild() {
        return RubiconBot.getShardManager().getGuildById(rawRootGuild);
    }

    @Override
    public TextChannel getRootChannel() {
        return RubiconBot.getShardManager().getTextChannelById(rawRootChannel);
    }

    @Override
    public HashMap<Guild, Channel> getMembers() {
        HashMap<Guild, Channel> map = new HashMap<>();
        for (Map.Entry entry : rawMembers.entrySet()) {
            try {
                map.put(RubiconBot.getShardManager().getGuildById((String) entry.getKey()), RubiconBot.getShardManager().getTextChannelById((String) entry.getValue()));
            } catch (Exception ignored) {
            }
        }
        return map;
    }

    @Override
    public void addGuild(String guildId, String channelId, String servername) {
        rawMembers.put(guildId, channelId);
        dbPortal.update(rethink.rethinkDB.hashMap("members", rawMembers)).run(rethink.getConnection());
        if (servername != null)
            broadcastSystemMessage(EmbedUtil.success("Server joined!", String.format("`%s` joined the portal.", servername)).build());
    }

    @Override
    public void removeGuild(String guildId) {
        if (guildId.equals(rawRootGuild)) {
            delete("The portal owner closed the portal");
            return;
        }
        rawMembers.remove(guildId);
        table.update(rethink.rethinkDB.hashMap("members", null)).run(rethink.getConnection());
        table.update(rethink.rethinkDB.hashMap("members", rawMembers)).run(rethink.getConnection());
        if (rawMembers.size() == 0)
            delete("You were the last member.");
        else if(rawMembers.size() == 1)
            setPortalTopic("Connected to " + getMembers().size() + " server");
        else
            setPortalTopic("Connected to " + getMembers().size() + " servers");
    }

    @Override
    public void delete(String reason) {
        rawMembers.put(rawRootGuild, rawRootChannel);
        for (Map.Entry<String, String> entry : rawMembers.entrySet()) {
            Guild guild = RubiconBot.getShardManager().getGuildById(entry.getKey());
            TextChannel textChannel = RubiconBot.getShardManager().getTextChannelById(entry.getValue());
            RubiconGuild.fromGuild(guild).closePortal();

            if (textChannel != null) {
                if (guild.getSelfMember().hasPermission(textChannel, Permission.MANAGE_CHANNEL))
                    textChannel.getManager().setTopic("Closed").queue();
                SafeMessage.sendMessage(textChannel, EmbedUtil.error("Portal closed!", reason).build());
            }
        }
        dbPortal.delete().run(rethink.getConnection());
    }

    @Override
    public void broadcast(String channelExclude, String message, String username, String avatarUrl, String guildName) {
        Map<Guild, Channel> members = getMembers();
        members.put(getRootGuild(), getRootChannel());
        for (Map.Entry entry : members.entrySet()) {
            // Logger.debug(entry.toString());
            Guild guild = (Guild) entry.getKey();
            TextChannel textChannel = (TextChannel) entry.getValue();

            if (nullCheck(guild, textChannel))
                continue;

            if (channelExclude.equals(textChannel.getId()))
                continue;

            RubiconGuild rubiconGuild = RubiconGuild.fromGuild(guild);
            if (rubiconGuild.hasPortalEmbedsEnables()) {
                sendEmbedMessage(textChannel, message, username, avatarUrl, guildName);
                continue;
            }

            if (!guild.getSelfMember().hasPermission(textChannel, Permission.MANAGE_WEBHOOKS)) {
                sendEmbedMessage(textChannel, message, username, avatarUrl, guildName);
                continue;
            }

            Webhook webhook = null;
            for (Webhook hook : textChannel.getWebhooks().complete()) {
                if (hook.getName().equals("rubicon-portal-hook")) {
                    webhook = hook;
                    break;
                }
            }
            if (webhook == null) {
                webhook = textChannel.createWebhook("rubicon-portal-hook").complete();
            }

            WebhookClientBuilder builder = webhook.newClient();
            WebhookClient client = builder.build();
            WebhookMessageBuilder messageBuilder = new WebhookMessageBuilder();
            messageBuilder.setAvatarUrl(avatarUrl);
            messageBuilder.setUsername(username);
            messageBuilder.setContent(message.replace("@here", "@ here").replace("@everyone", "@ everyone"));
            try {
                client.send(messageBuilder.build());
            } catch (Exception ignored) {
                //Empty Message
            }
            client.close();
        }
    }

    @Override
    public void broadcastSystemMessage(MessageEmbed messageEmbed) {
        Map<Guild, Channel> members = getMembers();
        members.put(getRootGuild(), getRootChannel());
        for (Map.Entry entry : members.entrySet()) {
            Guild guild = (Guild) entry.getKey();
            TextChannel textChannel = (TextChannel) entry.getValue();
            if (!nullCheck(guild, textChannel))
                SafeMessage.sendMessage(textChannel, messageEmbed);
        }
    }

    private void sendEmbedMessage(TextChannel channel, String message, String username, String avatarUrl, String guildName) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setAuthor(username, null, avatarUrl);
        embedBuilder.setColor(Colors.COLOR_PRIMARY);
        if (guildName != null)
            embedBuilder.setFooter("Server: " + guildName, null);
        embedBuilder.setDescription(message);
        SafeMessage.sendMessage(channel, embedBuilder.build());
    }

    @Override
    public void setPortalTopic(String topic) {
        HashMap<Guild, Channel> members = getMembers();
        members.put(getRootGuild(), getRootChannel());
        String content = topic.replace("%membercount%", members.size() + "");
        for (Map.Entry entry : members.entrySet()) {
            Guild guild = (Guild) entry.getKey();
            TextChannel textChannel = (TextChannel) entry.getValue();
            if (nullCheck(guild, textChannel))
                continue;
            if (guild.getSelfMember().hasPermission(textChannel, Permission.MANAGE_CHANNEL))
                textChannel.getManager().setTopic(content).queue();
        }
    }

    private boolean nullCheck(Guild guild, TextChannel textChannel) {
        if (guild.getId() == null || textChannel == null) {
            RubiconGuild.fromGuild(guild).closePortal();
            removeGuild(guild.getId());

            if (rawMembers.size() == 0) {
                delete("You were the last members.");
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean containsChannel(Channel channel) {
        try {
            return getMembers().values().contains(channel) || getRootChannel().equals(channel);
        } catch (NullPointerException e) {
            return true;
        }
    }
}