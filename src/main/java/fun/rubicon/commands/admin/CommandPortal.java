package fun.rubicon.commands.admin;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command2.CommandHandler;
import fun.rubicon.command2.CommandManager;
import fun.rubicon.core.Main;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import fun.rubicon.util.Colors;
import fun.rubicon.util.EmbedUtil;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.text.ParseException;
import java.util.List;

/**
 * Rubicon Discord bot
 *
 * @author Yannick Seeger / ForYaSee
 * @copyright RubiconBot Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.commands.fun
 */
public class CommandPortal extends CommandHandler {

    public CommandPortal() {
        super(new String[]{"portal", "mirror", "telephone"}, CommandCategory.ADMIN, new PermissionRequirements(2, "command.portal"), "Create a portal and talk with users of other guilds.", "portal create\nportal close");
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
                    //TODO Direct Portal
                } else {
                    return createHelpMessage(parsedCommandInvocation);
                }
                break;
            case "close":
                //TODO closePortal(parsedCommandInvocation);
                break;
            default:
                return createHelpMessage(parsedCommandInvocation);
        }
        Logger.debug("Hä?");
        return null;
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

        /*Message searchMessage;
        try {
            parsedCommandInvocation.invocationMessage.getGuild().getController().createTextChannel("rubicon-portal").complete();
        } catch (Exception ex) {
            RubiconBot.getMySQL().updateGuildValue(parsedCommandInvocation.invocationMessage.getGuild(), "portal", "closed");
            parsedCommandInvocation.invocationMessage.getTextChannel().sendMessage(EmbedUtil.error("Portal error", "Portal is already opened!").build());
        }
        TextChannel channel = parsedCommandInvocation.invocationMessage.getGuild().getTextChannelsByName("rubicon-portal", false).get(0);
        EmbedBuilder builder = new EmbedBuilder();
        builder.setAuthor(parsedCommandInvocation.invocationMessage.getGuild().getName() + "'s portal opened", null, parsedCommandInvocation.invocationMessage.getGuild().getIconUrl());
        builder.setDescription("Searching other open portal...");
        builder.setColor(Colors.COLOR_NOT_IMPLEMENTED);
        searchMessage = channel.sendMessage(builder.build()).complete();
        RubiconBot.getMySQL().updateGuildValue(parsedCommandInvocation.invocationMessage.getGuild(), "portal", "waiting:" + searchMessage.getId());

        List<Guild> openGuilds = RubiconBot.getMySQL().getGuildsByContainingValue("portal", "waiting");
        Guild foundGuild = null;
        if (openGuilds.size() != 0) {
            for (Guild g : openGuilds) {
                if (!g.getId().equals(parsedCommandInvocation.invocationMessage.getGuild().getId())) foundGuild = g;
            }
            if (foundGuild == null) {
                return;
            }
            try {
                TextChannel otherChannel = foundGuild.getTextChannelsByName("rubicon-portal", true).get(0);
                builder.setAuthor("Portal created!", null, parsedCommandInvocation.invocationMessage.getGuild().getIconUrl());
                builder.setDescription("@here Created Portal to " + parsedCommandInvocation.invocationMessage.getGuild().getName());
                builder.setColor(Colors.COLOR_PRIMARY);
                otherChannel.editMessageById(RubiconBot.getMySQL().getGuildValue(openGuilds.get(0), "portal").split(":")[1], builder.build()).queue();
                parsedCommandInvocation.invocationMessage.getGuild().getTextChannelsByName("rubicon-portal", true).get(0).editMessageById(searchMessage.getId(), builder.setDescription("@here Created Portal to " + foundGuild.getName()).build()).queue();
                RubiconBot.getMySQL().updateGuildValue(parsedCommandInvocation.invocationMessage.getGuild(), "portal", "connected:" + foundGuild.getId() + ":" + otherChannel.getId());
                RubiconBot.getMySQL().updateGuildValue(foundGuild, "portal", "connected:" + parsedCommandInvocation.invocationMessage.getGuild().getId() + ":" + channel.getId());
            } catch (Exception ex) {
                RubiconBot.getMySQL().updateGuildValue(parsedCommandInvocation.invocationMessage.getGuild(), "portal", "closed");
                parsedCommandInvocation.invocationMessage.getTextChannel().sendMessage(EmbedUtil.error("Error", "An error occured").build());
            }
        }*/
    }

    /*private void closePortal(CommandManager.ParsedCommandInvocation parsedCommandInvocation) {
        String stat = RubiconBot.getMySQL().getGuildValue(parsedCommandInvocation.invocationMessage.getGuild(), "portal");
        if (stat.contains("waiting")) {
            RubiconBot.getMySQL().updateGuildValue(parsedCommandInvocation.invocationMessage.getGuild(), "portal", "closed");
            TextChannel textChannel;
            try {
                textChannel = parsedCommandInvocation.invocationMessage.getGuild().getTextChannelsByName("rubicon-portal", true).get(0);
                textChannel.delete().queue();
                parsedCommandInvocation.invocationMessage.getGuild().getOwner().getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Portal successfully closed!").queue());
            } catch (Exception ignored) {

            }
        } else if (stat.contains("connected")) {
            RubiconBot.getMySQL().updateGuildValue(parsedCommandInvocation.invocationMessage.getGuild(), "portal", "closed");
            TextChannel textChannel;
            try {
                textChannel = parsedCommandInvocation.invocationMessage.getGuild().getTextChannelsByName("rubicon-portal", true).get(0);
                textChannel.delete().queue();
                parsedCommandInvocation.invocationMessage.getGuild().getOwner().getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Portal successfully closed!").queue());
            } catch (Exception ignored) {

            }
            Guild otherGuild = parsedCommandInvocation.invocationMessage.getJDA().getGuildById(stat.split(":")[1]);
            otherGuild.getOwner().getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Portal was closed from the other owner!").queue());
            RubiconBot.getMySQL().updateGuildValue(otherGuild, "portal", "closed");
            TextChannel textChannel2;
            try {
                textChannel2 = otherGuild.getTextChannelsByName("rubicon-portal", true).get(0);
                textChannel2.delete().queue();
            } catch (Exception ignored) {
            }
        }
    }*/

    private void setGuildWaiting(Guild g, TextChannel messageChannel) {
        RubiconBot.getMySQL().updateGuildValue(g, "portal", "waiting");
        messageChannel.sendMessage(EmbedUtil.success("Portal opened", "Successfully opened portal.\nA portal will be created as soon as another server opens a portal").build()).queue();
        return;
    }

    /**
     * Create portals at two guilds
     *
     * @param guildOne message guild
     * @param guildTwo guild with waiting status
     */
    private void connectGuilds(Guild guildOne, Guild guildTwo, TextChannel messageChannel) {
        //Channel check and assign
        Logger.debug("one: " + guildOne.getTextChannelsByName("rubicon-portal", true).size());
        Logger.debug("two: " + guildTwo.getTextChannelsByName("rubicon-portal", true).size());
        TextChannel channelOne = (guildOne.getTextChannelsByName("rubicon-portal", true).size() == 0) ? null : guildOne.getTextChannelsByName("rubicon-portal", true).get(0);
        TextChannel channelTwo = (guildTwo.getTextChannelsByName("rubicon-portal", true).size() == 0) ? null : guildTwo.getTextChannelsByName("rubicon-portal", true).get(0);
        if (channelOne == null && guildOne.getMemberById(RubiconBot.getJDA().getSelfUser().getId()).getPermissions().contains(Permission.MANAGE_CHANNEL)) {
            channelOne = (TextChannel) guildOne.getController().createTextChannel("rubicon-portal").complete();
        } else {
            messageChannel.sendMessage(EmbedUtil.error("Portal Error!", "I need the `MANAGE_CHANNEL` permissions or you create yourself a channel called` rubicon-portal`.").setFooter(guildOne.getName(), null).build()).queue();
            return;
        }
        if (channelTwo == null && guildTwo.getMemberById(RubiconBot.getJDA().getSelfUser().getId()).getPermissions().contains(Permission.MANAGE_CHANNEL)) {
            channelTwo = (TextChannel) guildTwo.getController().createTextChannel("rubicon-portal").complete();
        } else {
            guildTwo.getOwner().getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(EmbedUtil.error("Portal Error!", "I need the `MANAGE_CHANNEL` permissions or you create yourself a channel called` rubicon-portal`.").setFooter(guildTwo.getName(), null).build()).queue());
            RubiconBot.getMySQL().updateGuildValue(guildTwo, "portal", "closed");
            setGuildWaiting(guildOne, messageChannel);
            return;
        }


    }
}