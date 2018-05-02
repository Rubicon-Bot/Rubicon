package fun.rubicon.commands.admin;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.core.entities.RubiconGuild;
import fun.rubicon.features.portal.Portal;
import fun.rubicon.features.portal.PortalManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.Colors;
import fun.rubicon.util.SafeMessage;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.List;

/**
 * @author ForYaSee / Yannick Seeger
 */
public class CommandPortal extends CommandHandler {

    private final PortalManager portalManager;

    public CommandPortal() {
        super(new String[]{"portal"}, CommandCategory.ADMIN, new PermissionRequirements("portal", false, false), "Creates a connection between two servers.", "create\n" +
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
                default:
                    return createHelpMessage();
            }
        } else if(invocation.getArgs().length == 2) {
            String option = invocation.getArgs()[1];
            switch (invocation.getArgs()[0].toLowerCase()) {
                case "invite":
                    break;
                case "accept":
                    break;
                case "invites":
                    if(option.equalsIgnoreCase("enable")) {
                        rubiconGuild.setPortalInvites(true);
                        return message(success("Enabled Invites!", "You now can receive invites to join another portal."));
                    } else if(option.equalsIgnoreCase("disable")) {
                        rubiconGuild.setPortalInvites(false);
                        return message(success("Disabled Invites!", "You now are receiving no invites."));
                    } else
                        return createHelpMessage();
                case "embeds":
                    if(option.equalsIgnoreCase("enable")) {
                        rubiconGuild.setPortalEmbeds(true);
                        return message(success("Enabled Embeds!", "The messages will now be send as embeds."));
                    } else if(option.equalsIgnoreCase("disable")) {
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

    private Message closePortal(CommandManager.ParsedCommandInvocation invocation) {
        RubiconGuild rubiconGuild = RubiconGuild.fromGuild(invocation.getGuild());
        PortalManager portalManager = new PortalManager();

        if(!rubiconGuild.hasPortal())
            return message(error("No Portal!", "You have to create a portal first."));

        String portalRoot = rubiconGuild.getPortalRoot();
        if(portalRoot.equals("SEARCH")) {
            rubiconGuild.closePortal();
            return message(success("Closed Portal!", "Successfully closed the portal request."));
        }

        Portal portal = portalManager.getPortalByOwner(portalRoot);
        if(portal == null) {
            rubiconGuild.closePortal();
            return message(success("Closed Portal!", "Successfully closed the portal."));
        }

        portal.removeGuild(invocation.getGuild().getId());
        rubiconGuild.closePortal();
        if(portal.getMembers().size() == 0) {
            portal.broadcastSystemMessage(new EmbedBuilder().setColor(Colors.COLOR_ERROR).setDescription(invocation.getGuild().getName() + " left the portal. Closing portal.").build());
            portal.delete();
        } else {
            portal.broadcastSystemMessage(new EmbedBuilder().setColor(Colors.COLOR_ERROR).setDescription(invocation.getGuild().getName() + " left the portal").build());
        }
        return message(success("Portal closed!", "Successfully closed the portal."));
    }

    private Message createPortal(CommandManager.ParsedCommandInvocation invocation) {
        Member self = invocation.getSelfMember();
        RubiconGuild rubiconGuild = RubiconGuild.fromGuild(invocation.getGuild());
        if(rubiconGuild.hasPortal())
            return message(error("Already in a portal!", "You only can create one portal."));
        TextChannel portalChannel;
        List<TextChannel> resChannelList = invocation.getGuild().getTextChannelsByName("rubicon-portal", true);
        if(resChannelList.isEmpty()) {
            if (!self.hasPermission(Permission.MANAGE_CHANNEL)) {
                return message(error("Missing permissions!", "I need the `MANAGE_CHANNELS` permissions."));
            } else {
                portalChannel = (TextChannel) invocation.getGuild().getController().createTextChannel("rubicon-portal").complete();
            }
        } else
            portalChannel = resChannelList.get(0);

        String possiblePartner = portalManager.getSearchingGuild(invocation.getGuild().getId());
        if(possiblePartner == null) {
            rubiconGuild.setPortal("SEARCH");
            SafeMessage.sendMessage(portalChannel, info("No other portal!", "There is currently no other open portal. I will create a connection as soon as possible.").build());
            return null;
        }
        Guild rootGuild = RubiconBot.getShardManager().getGuildById(possiblePartner);
        TextChannel rootChannel = rootGuild.getTextChannelsByName("rubicon-portal", true).size() > 0 ? rootGuild.getTextChannelsByName("rubicon-portal", true).get(0) : null;
        if(rootChannel == null)
            return createPortal(invocation);
        Portal portal = portalManager.createPortal(rootGuild.getId(), rootChannel.getId());
        portal.addGuild(invocation.getGuild().getId(), portalChannel.getId());
        rubiconGuild.setPortal(rootGuild.getId());
        RubiconGuild.fromGuild(rootGuild).setPortal(rootGuild.getId());
        EmbedBuilder embed1 = new EmbedBuilder();
        embed1.setColor(Colors.COLOR_PRIMARY);
        embed1.setDescription(String.format("Connected to %s", invocation.getGuild().getName(), invocation.getGuild().getId()));
        embed1.setThumbnail(invocation.getGuild().getIconUrl());
        embed1.addField("Id", invocation.getGuild().getId(), true);
        Message rMsg = SafeMessage.sendMessageBlocking(rootChannel, embed1.build());

        EmbedBuilder embed2 = new EmbedBuilder();
        embed2.setColor(Colors.COLOR_PRIMARY);
        embed2.setDescription(String.format("Connected to **%s**", invocation.getGuild().getName(), invocation.getGuild().getId()));
        embed2.setThumbnail(invocation.getGuild().getIconUrl());
        embed2.addField("Id", invocation.getGuild().getId(), true);
        embed2.setDescription(String.format("Connected to **%s**", rootGuild.getName(), rootGuild.getId()));
        embed2.setThumbnail(rootGuild.getIconUrl());
        Message mMsg = SafeMessage.sendMessageBlocking(portalChannel, embed2.build());

        if(rootGuild.getSelfMember().hasPermission(rootChannel, Permission.MESSAGE_MANAGE)) {
            try {
                rMsg.pin().queue();
            } catch (NullPointerException ignored) {}
        }
        if(rootGuild.getSelfMember().hasPermission(rootChannel, Permission.MANAGE_CHANNEL))
            rootChannel.getManager().setTopic("Portal: Connected to " + invocation.getGuild().getName()).queue();

        if(self.hasPermission(portalChannel, Permission.MESSAGE_MANAGE)) {
            try {
                mMsg.pin().queue();
            } catch (NullPointerException ignored) {}
        }
        if(invocation.getSelfMember().hasPermission(portalChannel, Permission.MANAGE_CHANNEL))
            portalChannel.getManager().setTopic("Portal: Connected to " + rootGuild.getName()).queue();
        return null;
    }
}
