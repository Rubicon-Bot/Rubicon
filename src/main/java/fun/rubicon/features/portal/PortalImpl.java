package fun.rubicon.features.portal;

import com.rethinkdb.gen.ast.Filter;
import com.rethinkdb.gen.ast.Table;
import com.sun.security.auth.callback.TextCallbackHandler;
import fun.rubicon.RubiconBot;
import fun.rubicon.core.entities.RubiconGuild;
import fun.rubicon.rethink.Rethink;
import fun.rubicon.util.Colors;
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
    public void addGuild(String guildId, String channelId) {
        rawMembers.put(guildId, channelId);
        dbPortal.update(rethink.rethinkDB.hashMap("members", rawMembers)).run(rethink.connection);
    }

    @Override
    public void removeGuild(String guildId) {
        if (guildId.equals(getRootGuild().getId())) {
            changeOwnership();
            return;
        }
        rawMembers.remove(guildId);
        dbPortal.update(rethink.rethinkDB.hashMap("members", rawMembers)).run(rethink.connection);
    }

    @Override
    public void delete() {
        for (Map.Entry entry : getMembers().entrySet()) {
            Guild guild = (Guild) entry.getKey();
            TextChannel textChannel = (TextChannel) entry.getValue();
            RubiconGuild.fromGuild(guild).closePortal();

            if (RubiconBot.getShardManager().getGuildById(guild.getId()) != null && RubiconBot.getShardManager().getTextChannelById(textChannel.getId()) != null) {
                if (guild.getSelfMember().hasPermission(textChannel, Permission.MANAGE_CHANNEL))
                    textChannel.getManager().setTopic("Closed").queue();
            }
        }
        RubiconGuild.fromGuild(getRootGuild()).closePortal();
        dbPortal.delete().run(rethink.connection);
    }

    @Override
    public void broadcast(String channelExclude, String message, String username, String avatarUrl, String guildName) {
        Map<Guild, Channel> members = getMembers();
        members.put(getRootGuild(), getRootChannel());

        for (Map.Entry entry : members.entrySet()) {
            Guild guild = (Guild) entry.getKey();
            TextChannel textChannel = (TextChannel) entry.getValue();

            if (nullCheck(guild, textChannel))
                continue;

            if(channelExclude.equals(textChannel.getId()))
                continue;

            RubiconGuild rubiconGuild = RubiconGuild.fromGuild(guild);
            if (rubiconGuild.hasPortalEmbedsEnables()) {
                sendEmbedMessage(textChannel, message, username, avatarUrl, guildName);
                continue;
            }

            if (!guild.getSelfMember().hasPermission(textChannel, Permission.MANAGE_WEBHOOKS)) {
                sendEmbedMessage(textChannel, message, username, avatarUrl, guildName);
                return;
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
            client.send(messageBuilder.build());
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
        if (RubiconBot.getShardManager().getGuildById(guild.getId()) == null || RubiconBot.getShardManager().getTextChannelById(textChannel.getId()) == null) {
            RubiconGuild.fromGuild(guild).closePortal();
            removeGuild(guild.getId());

            if (rawMembers.size() == 0) {
                delete();
            }
            return true;
        }
        return false;
    }

    @Override
    public void changeOwnership() {
        HashMap<Guild, Channel> members = getMembers();
        for (Map.Entry entry : members.entrySet()) {
            Guild guild = (Guild) entry.getKey();
            TextChannel textChannel = (TextChannel) entry.getValue();
            if (nullCheck(guild, textChannel))
                continue;
            dbPortal.update(rethink.rethinkDB.hashMap("root_guild", guild.getId()).with("root_channel", textChannel.getId())).run(rethink.connection);
            removeGuild(guild.getId());
            RubiconGuild.fromGuild(getRootGuild()).closePortal();
            return;
        }
    }

    @Override
    public boolean containsChannel(Channel channel) {
        if (getMembers().values().contains(channel) || getRootChannel().equals(channel))
            return true;
        return false;
    }
}