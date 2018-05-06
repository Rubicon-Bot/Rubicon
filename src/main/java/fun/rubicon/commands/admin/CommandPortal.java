package fun.rubicon.commands.admin;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.core.entities.RubiconGuild;
import fun.rubicon.features.portal.Portal;
import fun.rubicon.features.portal.PortalInvite;
import fun.rubicon.features.portal.PortalInviteManager;
import fun.rubicon.features.portal.PortalManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.Colors;
import fun.rubicon.util.EmbedUtil;
import fun.rubicon.util.Logger;
import fun.rubicon.util.SafeMessage;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import javax.sound.sampled.Port;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ForYaSee / Yannick Seeger
 */
public class CommandPortal extends CommandHandler {

    private final PortalManager portalManager;

    public CommandPortal() {
        super(new String[]{"portal"}, CommandCategory.ADMIN, new PermissionRequirements("portal", false, false), "Creates a connection between two servers.", "create\n" +
                "invites \n" +
                "invite <serverId>\n" +
                "accept <serverId>\n" +
                "close\n" +
                "invites <enable/disable>\n" +
                "embeds <enable/disable>");
        portalManager = new PortalManager();
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        RubiconGuild rubiconGuild = RubiconGuild.fromGuild(invocation.getGuild());
        if (invocation.getArgs().length == 0)
            return createHelpMessage();
        else if (invocation.getArgs().length == 1) {
            switch (invocation.getArgs()[0].toLowerCase()) {
                case "create":
                    return createPortal(invocation);
                case "info":
                    break;
                case "close":
                    return closePortal(invocation);
                case "invites":
                    return sendInvites(invocation);
                default:
                    return createHelpMessage();
            }
        } else if (invocation.getArgs().length == 2) {
            String option = invocation.getArgs()[1];
            switch (invocation.getArgs()[0].toLowerCase()) {
                case "invite":
                    return invite(invocation);
                case "accept":
                    return accept(invocation);
                case "invites":
                    if (option.equalsIgnoreCase("enable")) {
                        rubiconGuild.setPortalInvites(true);
                        return message(success("Enabled Invites!", "You now can receive invites to join another portal."));
                    } else if (option.equalsIgnoreCase("disable")) {
                        rubiconGuild.setPortalInvites(false);
                        return message(success("Disabled Invites!", "You now are receiving no invites."));
                    } else
                        return createHelpMessage();
                case "embeds":
                    if (option.equalsIgnoreCase("enable")) {
                        rubiconGuild.setPortalEmbeds(true);
                        return message(success("Enabled Embeds!", "The messages will now be send as embeds."));
                    } else if (option.equalsIgnoreCase("disable")) {
                        rubiconGuild.setPortalEmbeds(false);
                        return message(success("Disabled Embeds!", "The messages will now be send as webhooks."));
                    } else
                        return createHelpMessage();
                default:
                    return createHelpMessage();
            }
        }
        return createHelpMessage();
    }

    private Message accept(CommandManager.ParsedCommandInvocation invocation) {
        if (RubiconGuild.fromGuild(invocation.getGuild()).hasPortal())
            return message(error("Already in a portal!", "You only can create one portal."));
        PortalInviteManager portalInviteManager = new PortalInviteManager();
        List<PortalInvite> invites = portalInviteManager.getIncomingInvites(invocation.getGuild().getId());
        String id = invocation.getArgs()[1];
        Guild guild = null;
        try {
            guild = RubiconBot.getShardManager().getGuildById(id);
        } catch (Exception e) {
            return message(error("Invalid Server Id", "Either the id you entered is not correct or rubicon is not on this server anymore."));
        }
        PortalInvite portalInvite = null;
        for (PortalInvite invite : invites) {
            if (invite.getSender().equals(guild.getId())) {
                portalInvite = invite;
                break;
            }
        }
        if (portalInvite == null)
            return message(error("No invite!", "You have no invite from this server."));
        RubiconGuild senderGuild = RubiconGuild.fromGuild(guild);
        RubiconGuild receiverGuild = RubiconGuild.fromGuild(invocation.getGuild());

        TextChannel portalChannel;
        List<TextChannel> resChannelList = invocation.getGuild().getTextChannelsByName("rubicon-portal", true);
        if (resChannelList.isEmpty()) {
            if (!invocation.getGuild().getSelfMember().hasPermission(Permission.MANAGE_CHANNEL)) {
                return message(error("Missing permissions!", "I need the `MANAGE_CHANNELS` permissions."));
            } else {
                portalChannel = (TextChannel) invocation.getGuild().getController().createTextChannel("rubicon-portal").complete();
            }
        } else
            portalChannel = resChannelList.get(0);

        if (senderGuild.hasPortal()) {
            Portal portal = portalManager.getPortalByOwner(senderGuild.getPortalRoot());
            portal.addGuild(invocation.getGuild().getId(), portalChannel.getId(), invocation.getGuild().getName());
            receiverGuild.setPortal(senderGuild.getPortalRoot());
            portal.setPortalTopic("Connected to " + portal.getMembers().size() + " servers");
            portalInvite.delete();
        } else {
            TextChannel otherChannel;
            List<TextChannel> otherResChannelList = guild.getTextChannelsByName("rubicon-portal", true);
            if (otherResChannelList.isEmpty()) {
                if (!guild.getSelfMember().hasPermission(Permission.MANAGE_CHANNEL)) {
                    return message(error("Missing permissions!", "I can't create a portal channel on the other server."));
                } else {
                    otherChannel = (TextChannel) guild.getController().createTextChannel("rubicon-portal").complete();
                }
            } else
                otherChannel = otherResChannelList.get(0);

            Portal portal = portalManager.createPortal(invocation.getGuild().getId(), portalChannel.getId());
            portal.addGuild(guild.getId(), otherChannel.getId(), null);
            RubiconGuild.fromGuild(invocation.getGuild()).setPortal(invocation.getGuild().getId());
            RubiconGuild.fromGuild(guild).setPortal(invocation.getGuild().getId());
            sendPortalCreatedMessages(invocation.getGuild(), guild, portalChannel, otherChannel);
        }
        return null;
    }

    private Message invite(CommandManager.ParsedCommandInvocation invocation) {
        PortalInviteManager portalInviteManager = new PortalInviteManager();
        String guildSearch = invocation.getArgs()[1];
        Guild guild = null;
        List<Guild> res = RubiconBot.getGuildsByName(guildSearch, true);
        if (!res.isEmpty())
            guild = res.get(0);
        if (guild == null) {
            try {
                guild = RubiconBot.getShardManager().getGuildById(guildSearch);
            } catch (Exception ignored) {

            }
        }
        if (guild == null)
            return message(error("No guild found!", "Found no guild with that name or id."));
        if (!RubiconGuild.fromGuild(guild).allowsPortalInvites()) {
            return message(error("Denied!", "This guild doesn't allow portal invites."));
        }
        if (!portalInviteManager.sendInvite(invocation.getGuild().getId(), guild.getId()))
            return message(error("Already invited!", "You already invited this server."));
        try {
            final Guild sendGuild = guild;
            guild.getOwner().getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(new EmbedBuilder().setDescription(String.format("You received a portal invite from `%s` for creating a portal with `%s`. Type `%s` to accept the request.", invocation.getGuild().getName(), sendGuild.getName(), RubiconGuild.fromGuild(invocation.getGuild()).getPrefix() + "portal accept " + invocation.getGuild().getId())).setAuthor("Portal Invite").setColor(Colors.COLOR_PRIMARY).build()).queue());
        } catch (Exception e) {
            return message(error("Can't invite server!", "I can't send a message to the owner."));
        }
        return message(success("Sent invite!", "Successfully invited `" + guildSearch + "`"));
    }

    private Message sendInvites(CommandManager.ParsedCommandInvocation invocation) {
        PortalInviteManager portalInviteManager = new PortalInviteManager();
        List<PortalInvite> invites = portalInviteManager.getOutcomingInvites(invocation.getGuild().getId());
        StringBuilder stringBuilder = new StringBuilder();
        if (invites.isEmpty())
            stringBuilder.append("None");
        else {
            for (PortalInvite invite : invites) {
                Guild guild = RubiconBot.getShardManager().getGuildById(invite.getReceiver());
                if (guild == null) {
                    invite.delete();
                    continue;
                }
                stringBuilder.append(":small_blue_diamond: " + guild.getName() + "\n");
            }
        }
        return message(info("Your outgoing invites:", stringBuilder.toString()));
    }

    private Message closePortal(CommandManager.ParsedCommandInvocation invocation) {
        RubiconGuild rubiconGuild = RubiconGuild.fromGuild(invocation.getGuild());
        PortalManager portalManager = new PortalManager();

        if (!rubiconGuild.hasPortal())
            return message(error("No Portal!", "You have to create a portal first."));

        String portalRoot = rubiconGuild.getPortalRoot();
        if (portalRoot.equals("SEARCH")) {
            rubiconGuild.closePortal();
            return message(success("Closed Portal!", "Successfully closed the portal request."));
        }

        Portal portal = portalManager.getPortalByOwner(portalRoot);
        if (portal == null) {
            rubiconGuild.closePortal();
            return message(success("Closed Portal!", "Successfully closed the portal."));
        }

        portal.removeGuild(invocation.getGuild().getId());
        rubiconGuild.closePortal();
        return message(success("Closed Portal!", "Successfully closed your portal."));
    }

    private Message createPortal(CommandManager.ParsedCommandInvocation invocation) {
        Member self = invocation.getSelfMember();
        RubiconGuild rubiconGuild = RubiconGuild.fromGuild(invocation.getGuild());
        if (rubiconGuild.hasPortal())
            return message(error("Already in a portal!", "You only can create one portal."));
        TextChannel portalChannel;
        List<TextChannel> resChannelList = invocation.getGuild().getTextChannelsByName("rubicon-portal", true);
        if (resChannelList.isEmpty()) {
            if (!self.hasPermission(Permission.MANAGE_CHANNEL)) {
                return message(error("Missing permissions!", "I need the `MANAGE_CHANNELS` permissions."));
            } else {
                portalChannel = (TextChannel) invocation.getGuild().getController().createTextChannel("rubicon-portal").complete();
            }
        } else
            portalChannel = resChannelList.get(0);

        String possiblePartner = portalManager.getSearchingGuild(invocation.getGuild().getId());
        if (possiblePartner == null) {
            rubiconGuild.setPortal("SEARCH");
            SafeMessage.sendMessage(portalChannel, info("No other portal!", "There is currently no other open portal. I will create a connection as soon as possible.").build());
            return null;
        }
        Guild rootGuild = RubiconBot.getShardManager().getGuildById(possiblePartner);
        TextChannel rootChannel = rootGuild.getTextChannelsByName("rubicon-portal", true).size() > 0 ? rootGuild.getTextChannelsByName("rubicon-portal", true).get(0) : null;
        if (rootChannel == null)
            return createPortal(invocation);
        Portal portal = portalManager.createPortal(rootGuild.getId(), rootChannel.getId());
        portal.addGuild(invocation.getGuild().getId(), portalChannel.getId(), null);
        rubiconGuild.setPortal(rootGuild.getId());
        RubiconGuild.fromGuild(rootGuild).setPortal(rootGuild.getId());
        sendPortalCreatedMessages(rootGuild, invocation.getGuild(), rootChannel, portalChannel);
        return null;
    }

    private void sendPortalCreatedMessages(Guild root, Guild guild, TextChannel rootChannel, TextChannel channel) {
        EmbedBuilder rootEmbed = new EmbedBuilder();
        rootEmbed.setColor(Colors.COLOR_PRIMARY);
        rootEmbed.setDescription(String.format("Connected to %s", guild.getName()));
        rootEmbed.setThumbnail(guild.getIconUrl());
        rootEmbed.addField("Id", guild.getId(), true);
        Message rMsg = SafeMessage.sendMessageBlocking(rootChannel, rootEmbed.build());

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Colors.COLOR_PRIMARY);
        embed.setThumbnail(root.getIconUrl());
        embed.addField("Id", root.getId(), true);
        embed.setDescription(String.format("Connected to **%s**", root.getName()));
        embed.setThumbnail(root.getIconUrl());
        Message mMsg = SafeMessage.sendMessageBlocking(channel, embed.build());

        if (root.getSelfMember().hasPermission(rootChannel, Permission.MESSAGE_MANAGE)) {
            try {
                rMsg.pin().queue();
            } catch (NullPointerException ignored) {
            }
        }
        if (root.getSelfMember().hasPermission(rootChannel, Permission.MANAGE_CHANNEL))
            rootChannel.getManager().setTopic("Portal: Connected to " + guild.getName()).queue();

        if (guild.getSelfMember().hasPermission(channel, Permission.MESSAGE_MANAGE)) {
            try {
                mMsg.pin().queue();
            } catch (NullPointerException ignored) {
            }
        }
        if (guild.getSelfMember().hasPermission(channel, Permission.MANAGE_CHANNEL))
            channel.getManager().setTopic("Portal: Connected to " + root.getName()).queue();
    }
}
