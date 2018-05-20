package fun.rubicon.entities.impl;

import fun.rubicon.entities.Guild;
import fun.rubicon.io.db.RethinkDataset;
import lombok.Getter;
import lombok.ToString;
import net.dv8tion.jda.client.requests.restaction.pagination.MentionPaginationAction;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Region;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.managers.AudioManager;
import net.dv8tion.jda.core.managers.GuildController;
import net.dv8tion.jda.core.managers.GuildManager;
import net.dv8tion.jda.core.managers.GuildManagerUpdatable;
import net.dv8tion.jda.core.requests.RestAction;
import net.dv8tion.jda.core.requests.restaction.MemberAction;
import net.dv8tion.jda.core.requests.restaction.pagination.AuditLogPaginationAction;
import net.dv8tion.jda.core.utils.cache.MemberCacheView;
import net.dv8tion.jda.core.utils.cache.SnowflakeCacheView;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * @author ForYaSee / Yannick Seeger
 */
@ToString
public class GuildImpl extends RethinkDataset implements Guild {

    public static final transient String TABLE = "guilds";
    private transient net.dv8tion.jda.core.entities.Guild guild;
    private String id;
    @Getter private String prefix;

    public GuildImpl(net.dv8tion.jda.core.entities.Guild guild, String prefix) {
        super(TABLE);
        this.guild = guild;
        this.id = guild.getId();
        this.prefix = prefix;
    }

    public GuildImpl() {
        super(TABLE);
    }

    public void setGuild(net.dv8tion.jda.core.entities.Guild guild) {
        this.guild = guild;
        this.id = guild.getId();
    }

    @Override
    public void setPrefix(String prefix) {
        this.prefix = prefix;
        saveData();
    }

    @Override
    public String getId() {
        return guild.getId();
    }

    @Override
    public RestAction<EnumSet<Region>> retrieveRegions() {
        return guild.retrieveRegions();
    }

    @Override
    public MemberAction addMember(String accessToken, String userId) {
        return guild.addMember(accessToken, userId);
    }

    @Override
    public String getName() {
        return guild.getName();
    }

    @Override
    public String getIconId() {
        return guild.getIconId();
    }

    @Override
    public String getIconUrl() {
        return guild.getIconUrl();
    }

    @Override
    public Set<String> getFeatures() {
        return guild.getFeatures();
    }

    @Override
    public String getSplashId() {
        return guild.getSplashId();
    }

    @Override
    public String getSplashUrl() {
        return guild.getSplashUrl();
    }

    @Override
    public RestAction<String> getVanityUrl() {
        return guild.getVanityUrl();
    }

    @Override
    public VoiceChannel getAfkChannel() {
        return guild.getAfkChannel();
    }

    @Override
    public TextChannel getSystemChannel() {
        return guild.getSystemChannel();
    }

    @Override
    public Member getOwner() {
        return guild.getOwner();
    }

    @Override
    public Timeout getAfkTimeout() {
        return guild.getAfkTimeout();
    }

    @Override
    public String getRegionRaw() {
        return guild.getRegionRaw();
    }

    @Override
    public boolean isMember(User user) {
        return guild.isMember(user);
    }

    @Override
    public Member getSelfMember() {
        return guild.getSelfMember();
    }

    @Override
    public Member getMember(User user) {
        return guild.getMember(user);
    }

    @Override
    public MemberCacheView getMemberCache() {
        return guild.getMemberCache();
    }

    @Override
    public SnowflakeCacheView<Category> getCategoryCache() {
        return guild.getCategoryCache();
    }

    @Override
    public SnowflakeCacheView<TextChannel> getTextChannelCache() {
        return guild.getTextChannelCache();
    }

    @Override
    public SnowflakeCacheView<VoiceChannel> getVoiceChannelCache() {
        return guild.getVoiceChannelCache();
    }

    @Override
    public SnowflakeCacheView<Role> getRoleCache() {
        return guild.getRoleCache();
    }

    @Override
    public SnowflakeCacheView<Emote> getEmoteCache() {
        return guild.getEmoteCache();
    }

    @Nonnull
    @Override
    public RestAction<List<Ban>> getBanList() {
        return guild.getBanList();
    }

    @Override
    public RestAction<Integer> getPrunableMemberCount(int days) {
        return guild.getPrunableMemberCount(days);
    }

    @Override
    public Role getPublicRole() {
        return guild.getPublicRole();
    }

    @Nullable
    @Override
    public TextChannel getDefaultChannel() {
        return guild.getDefaultChannel();
    }

    @Override
    public GuildManager getManager() {
        return guild.getManager();
    }

    @Override
    public GuildManagerUpdatable getManagerUpdatable() {
        return guild.getManagerUpdatable();
    }

    @Override
    public GuildController getController() {
        return guild.getController();
    }

    @Override
    public MentionPaginationAction getRecentMentions() {
        return guild.getRecentMentions();
    }

    @Override
    public AuditLogPaginationAction getAuditLogs() {
        return guild.getAuditLogs();
    }

    @Override
    public RestAction<Void> leave() {
        return guild.leave();
    }

    @Override
    public RestAction<Void> delete() {
        return guild.delete();
    }

    @Override
    public RestAction<Void> delete(String mfaCode) {
        return guild.delete(mfaCode);
    }

    @Override
    public AudioManager getAudioManager() {
        return guild.getAudioManager();
    }

    @Override
    public JDA getJDA() {
        return guild.getJDA();
    }

    @Override
    public RestAction<List<Invite>> getInvites() {
        return guild.getInvites();
    }

    @Override
    public RestAction<List<Webhook>> getWebhooks() {
        return guild.getWebhooks();
    }

    @Override
    public List<GuildVoiceState> getVoiceStates() {
        return guild.getVoiceStates();
    }

    @Override
    public VerificationLevel getVerificationLevel() {
        return guild.getVerificationLevel();
    }

    @Override
    public NotificationLevel getDefaultNotificationLevel() {
        return guild.getDefaultNotificationLevel();
    }

    @Override
    public MFALevel getRequiredMFALevel() {
        return guild.getRequiredMFALevel();
    }

    @Override
    public ExplicitContentLevel getExplicitContentLevel() {
        return guild.getExplicitContentLevel();
    }

    @Override
    public boolean checkVerification() {
        return guild.checkVerification();
    }

    @Override
    public boolean isAvailable() {
        return guild.isAvailable();
    }

    @Override
    public long getIdLong() {
        return guild.getIdLong();
    }

    @Override
    public RestAction<EnumSet<Region>> retrieveRegions(boolean b) {
        return guild.retrieveRegions(b);
    }
}
