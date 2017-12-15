/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.admin;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command2.CommandHandler;
import fun.rubicon.command2.CommandManager;
import fun.rubicon.data.PermissionLevel;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import fun.rubicon.util.Colors;
import fun.rubicon.util.EmbedUtil;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class CommandPortal extends CommandHandler {

    private static String portalChannelName = "rubicon-portal";
    private String closedChannelName = "closed-rubicon-portal";
    public static HashMap<Guild, Message> requests = new HashMap<>();

    public CommandPortal() {
        super(new String[]{"portal", "mirror", "telephone"}, CommandCategory.ADMIN, new PermissionRequirements(PermissionLevel.ADMINISTRATOR, "command.portal"), "Create a portal and talk with users of other guilds.", "create\nportal close");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        if (parsedCommandInvocation.args.length == 0) {
            return createHelpMessage(parsedCommandInvocation);
        }

        switch (parsedCommandInvocation.args[0].toLowerCase()) {
            case "open":
            case "o":
            case "create":
                if (parsedCommandInvocation.args.length == 1) {
                    createPortalWithRandomGuild(parsedCommandInvocation);
                    return null;
                } else if (parsedCommandInvocation.args.length == 2) {
                    createPortaWithSpecifiedGuild(parsedCommandInvocation);
                } else {
                    return createHelpMessage(parsedCommandInvocation);
                }
                return null;
            case "close":
                closePortal(parsedCommandInvocation);
                return null;
            default:
                return createHelpMessage(parsedCommandInvocation);
        }
    }

    /**
     * Creates a portal with a random guild
     *
     * @param parsedCommandInvocation parsedCommandInvocation
     */
    private void createPortalWithRandomGuild(CommandManager.ParsedCommandInvocation parsedCommandInvocation) {
        Guild messageGuild = parsedCommandInvocation.invocationMessage.getGuild();
        TextChannel messageChannel = parsedCommandInvocation.invocationMessage.getTextChannel();

        //Check if portal exists
        String oldGuildPortalEntry = RubiconBot.getMySQL().getGuildValue(messageGuild, "portal");
        if (oldGuildPortalEntry.equals("open") || oldGuildPortalEntry.contains("waiting") || RubiconBot.getMySQL().ifPortalExist(messageGuild)) {
            messageChannel.sendMessage(EmbedUtil.error("Portal error!", "Portal is already open").build()).queue();
            return;
        }

        List<Guild> waitingGuilds = RubiconBot.getMySQL().getGuildsByContainingValue("portal", "waiting");
        if (waitingGuilds.size() == 0) {
            setGuildWaiting(messageGuild, messageChannel);
            return;
        } else {
            connectGuilds(messageGuild, waitingGuilds.get(0), messageChannel);
            return;
        }
    }

    /**
     * Create portals at two guilds
     *
     * @param guildOne message guild
     * @param guildTwo guild with waiting status
     */
    public static void connectGuilds(Guild guildOne, Guild guildTwo, TextChannel messageChannel) {
        //Channel creation and waiting check
        TextChannel channelOne = (guildOne.getTextChannelsByName(portalChannelName, true).size() == 0) ? null : guildOne.getTextChannelsByName(portalChannelName, true).get(0);
        TextChannel channelTwo = (guildTwo.getTextChannelsByName(portalChannelName, true).size() == 0) ? null : guildTwo.getTextChannelsByName(portalChannelName, true).get(0);
        if (channelOne == null) {
            if (guildOne.getMemberById(RubiconBot.getJDA().getSelfUser().getId()).getPermissions().contains(Permission.MANAGE_CHANNEL)) {
                channelOne = (TextChannel) guildOne.getController().createTextChannel(portalChannelName).complete();
            } else {
                messageChannel.sendMessage(EmbedUtil.error("Portal Error!", "I need the `MANAGE_CHANNEL` permissions or you create yourself a channel called `rubicon-portal`.").setFooter(guildOne.getName(), null).build()).queue();
                return;
            }
        }
        if (channelTwo == null) {
            if (guildTwo.getMemberById(RubiconBot.getJDA().getSelfUser().getId()).getPermissions().contains(Permission.MANAGE_CHANNEL)) {
                channelTwo = (TextChannel) guildTwo.getController().createTextChannel(portalChannelName).complete();
            } else {
                guildTwo.getOwner().getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(EmbedUtil.error("Portal Error!", "I need the `MANAGE_CHANNEL` permissions or you create yourself a channel called `rubicon-portal`.").setFooter(guildTwo.getName(), null).build()).queue());
                RubiconBot.getMySQL().updateGuildValue(guildTwo, "portal", "closed");
                setGuildWaiting(guildOne, messageChannel);
                return;
            }
        }

        //Update Database Values
        RubiconBot.getMySQL().updateGuildValue(guildOne, "portal", "open");
        RubiconBot.getMySQL().createPortal(guildOne, guildTwo, channelOne);
        RubiconBot.getMySQL().updateGuildValue(guildTwo, "portal", "open");
        RubiconBot.getMySQL().createPortal(guildTwo, guildOne, channelTwo);

        //Send Connected Message
        sendConnectedMessage(channelOne, channelTwo);
    }

    private static void sendConnectedMessage(TextChannel channelOne, TextChannel channelTwo) {
        //GuildOne Message
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setAuthor("Connection established with " + channelTwo.getGuild().getName(), null, channelTwo.getGuild().getIconUrl());
        embedBuilder.setDescription(":white_check_mark: Successfully created and connected portals.");
        Message message1 = channelOne.sendMessage(embedBuilder.build()).complete();
        channelOne.pinMessageById(message1.getId()).queue();

        //GuildTwo Message
        embedBuilder.setAuthor("Connection established with " + channelOne.getGuild().getName(), null, channelOne.getGuild().getIconUrl());
        embedBuilder.setDescription(":white_check_mark: Successfully created and connected portals.");
        Message message2 = channelTwo.sendMessage(embedBuilder.build()).complete();
        channelTwo.pinMessageById(message2.getId()).queue();
    }

    private static void setGuildWaiting(Guild g, TextChannel messageChannel) {
        RubiconBot.getMySQL().updateGuildValue(g, "portal", "waiting");
        if (g.getTextChannelsByName(portalChannelName, true).size() == 0)
            messageChannel.sendMessage(EmbedUtil.success("Portal opened", "Successfully opened portal.\nA portal-channel will be created as soon as another server opens a portal.").build()).queue();
        else
            messageChannel.sendMessage(EmbedUtil.success("Portal opened", "Successfully opened portal.\nA message will be sent to the portal channel as soon as a partner is found.").build()).queue();
        return;
    }

    private void closePortal(CommandManager.ParsedCommandInvocation parsedCommandInvocation) {
        JDA jda = parsedCommandInvocation.invocationMessage.getJDA();
        Guild messageGuild = parsedCommandInvocation.invocationMessage.getGuild();
        TextChannel messageChannel = parsedCommandInvocation.invocationMessage.getTextChannel();

        //Check if portal exists
        String oldGuildPortalEntry = RubiconBot.getMySQL().getGuildValue(messageGuild, "portal");
        if (oldGuildPortalEntry.equals("closed")) {
            messageChannel.sendMessage(EmbedUtil.error("Portal error!", "Portal is already closed").build()).queue();
            return;
        }
        if (oldGuildPortalEntry.equals("waiting")) {
            RubiconBot.getMySQL().updateGuildValue(messageGuild, "portal", "closed");
            messageChannel.sendMessage(EmbedUtil.success("Portal", "Successfull closed portal request.").build()).queue();
            return;
        }
        Guild partnerGuild = jda.getGuildById(RubiconBot.getMySQL().getPortalValue(messageGuild, "partnerid"));

        //Close Channels
        TextChannel channelOne = null;
        TextChannel channelTwo = null;
        try {
            channelOne = jda.getTextChannelById(RubiconBot.getMySQL().getPortalValue(messageGuild, "channelid"));
            channelTwo = jda.getTextChannelById(RubiconBot.getMySQL().getPortalValue(partnerGuild, "channelid"));
        } catch (NullPointerException ignored) {
            //Channels doesn't exist
        }

        if (channelOne != null)
            channelOne.getManager().setName(closedChannelName).queue();
        if (channelTwo != null)
            channelTwo.getManager().setName(closedChannelName).queue();

        //Close and delete DB Portal
        RubiconBot.getMySQL().updateGuildValue(messageGuild, "portal", "closed");
        RubiconBot.getMySQL().deletePortal(messageGuild);
        RubiconBot.getMySQL().updateGuildValue(partnerGuild, "portal", "closed");
        RubiconBot.getMySQL().deletePortal(partnerGuild);

        EmbedBuilder portalClosedMessage = new EmbedBuilder();
        portalClosedMessage.setAuthor("Portal closed!", null, jda.getSelfUser().getEffectiveAvatarUrl());
        portalClosedMessage.setDescription("Portal was closed by the owner. Create a new one with `" + parsedCommandInvocation.serverPrefix + "portal create`");
        portalClosedMessage.setColor(Colors.COLOR_ERROR);

        channelOne.sendMessage(portalClosedMessage.build()).queue();
        portalClosedMessage.setDescription("Portal was closed by the other server owner. Create a new one with `" + parsedCommandInvocation.serverPrefix + "portal create`");
        channelTwo.sendMessage(portalClosedMessage.build()).queue();
    }


    /**
     * Creates portal to a specified guild
     *
     * @param parsedCommandInvocation
     */
    private void createPortaWithSpecifiedGuild(CommandManager.ParsedCommandInvocation parsedCommandInvocation) {
        if (!parsedCommandInvocation.invocationMessage.getGuild().getTextChannelsByName(portalChannelName, true).isEmpty()) {
            parsedCommandInvocation.invocationMessage.getTextChannel().sendMessage(EmbedUtil.error("Portal already opened", "You have already opened a portal").build()).queue(msg -> msg.delete().queueAfter(10, TimeUnit.SECONDS));
            return;
        }
        String[] args = parsedCommandInvocation.args;
        User author = parsedCommandInvocation.invocationMessage.getAuthor();
        Guild guild = RubiconBot.getJDA().getGuildById(args[1]);
        if (guild == null) {
            parsedCommandInvocation.invocationMessage.getTextChannel().sendMessage(EmbedUtil.error("Guild not found", " Sorry the specified guild does not exits or I'm not on it").build()).queue(msg -> msg.delete().queueAfter(10, TimeUnit.SECONDS));
            return;
        }
        if (!guild.getTextChannelsByName(portalChannelName, true).isEmpty()) {
            parsedCommandInvocation.invocationMessage.getTextChannel().sendMessage(EmbedUtil.error("Portal already opened", "There is already a portal opened to your partner guild").build()).queue(msg -> msg.delete().queueAfter(10, TimeUnit.SECONDS));
            return;
        }
        TextChannel portal;
        if (!guild.getCategoriesByName("rubicon", true).isEmpty())
            portal = (TextChannel) guild.getCategoriesByName("rubicon", true).get(0).createTextChannel(portalChannelName).complete();
        else
            portal = (TextChannel) guild.getController().createTextChannel(portalChannelName).complete();

        Message request = portal.sendMessage(EmbedUtil.info("Portal request", "**" + author.getName() + "#" + author.getDiscriminator() + " requested to create a portal from " + parsedCommandInvocation.invocationMessage.getGuild().getName() + " to your guild! React to accept or deny!").build()).complete();
        parsedCommandInvocation.invocationMessage.getTextChannel().sendMessage(EmbedUtil.success("Request sent", "I sent a request to " + guild.getName() + "!").build()).queue(msg -> msg.delete().queueAfter(10, TimeUnit.SECONDS));
        request.addReaction("✅").queue();
        request.addReaction("❌").queue();
        requests.put(request.getGuild(), request);
    }

    public static void handleReaction(MessageReactionAddEvent event){
        if(!requests.containsKey(event.getGuild())) return;
        Message message = event.getTextChannel().getMessageById(event.getMessageId()).complete();
        if(!message.equals(requests.get(event.getGuild()))) return;
        event.getReaction().removeReaction().queue();
        String emote = event.getReactionEmote().getName();
        if(emote.equalsIgnoreCase("❌")){
            message.editMessage(EmbedUtil.info("Declined", "You declined portal request").build()).queue();
            message.getReactions().forEach(r -> {
                r.removeReaction().queue();
            });
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    message.getTextChannel().delete().queue();
                }
            }, 7000);
            return;
        } else if(emote.equalsIgnoreCase("✅")) {
            message.delete().queue();
            connectGuilds(event.getGuild(), message.getGuild(), message.getTextChannel());
        }
    }
}