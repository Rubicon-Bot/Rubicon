package fun.rubicon.commands.admin;

import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.core.Main;
import fun.rubicon.util.Colors;
import fun.rubicon.util.Info;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Category;
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
 * @copyright Rubicon Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.commands.fun
 */
public class CommandPortal extends Command {

    public CommandPortal(String command, CommandCategory category) {
        super(command, category);
    }

    @Override
    protected void execute(String[] args, MessageReceivedEvent e) throws ParseException {
        if (args.length == 0) {
            sendUsageMessage();
            return;
        }

        switch (args[0]) {
            case "open":
            case "o":
            case "create":
                if (Main.getMySQL().getGuildValue(e.getGuild(), "portal").equals("closed")) {
                    if (args.length == 1) {
                        createPortalWithRandomGuild();
                    } else if (args.length == 2) {
                        //Direct Portal
                    } else {
                        sendUsageMessage();
                        return;
                    }
                } else {
                    sendErrorMessage("Portal is already open!");
                    return;
                }
                break;
            case "close":
                closePortal();
                break;
            default:
                sendUsageMessage();
        }
    }

    private void createPortalWithRandomGuild() {
        Category cat;
        Message searchMessage;
        try {
            cat = e.getGuild().getCategoriesByName(Info.BOT_NAME, true).get(0);
        } catch (IndexOutOfBoundsException ex) {
            sendErrorMessage("You deleted or renamed the rubicon category! Please use " + Main.getMySQL().getGuildValue(e.getGuild(), "prefix") + "rebuild");
            return;
        }
        try {
            e.getGuild().getController().createTextChannel("rubicon-portal").setParent(e.getGuild().getCategoriesByName(Info.BOT_NAME, true).get(0)).complete();
        } catch (Exception ex) {
            sendErrorMessage("An error occured!");
            Main.getMySQL().updateGuildValue(e.getGuild(), "portal", "closed");
            return;
        }
        TextChannel channel = e.getGuild().getTextChannelsByName("rubicon-portal", false).get(0);
        EmbedBuilder builder = new EmbedBuilder();
        builder.setAuthor(e.getGuild().getName() + "'s portal opened", null, e.getGuild().getIconUrl());
        builder.setDescription("Searching other open portal...");
        builder.setColor(Colors.COLOR_NOT_IMPLEMENTED);
        searchMessage = channel.sendMessage(builder.build()).complete();
        Main.getMySQL().updateGuildValue(e.getGuild(), "portal", "waiting:" + searchMessage.getId());

        List<Guild> openGuilds = Main.getMySQL().getGuildsByContainingValue("portal", "waiting");
        Guild foundGuild = null;
        if (openGuilds.size() != 0) {
            for (Guild g : openGuilds) {
                if (!g.getId().equals(e.getGuild().getId())) foundGuild = g;
            }
            if (foundGuild == null) {
                return;
            }
            try {
                TextChannel otherChannel = foundGuild.getTextChannelsByName("rubicon-portal", true).get(0);
                builder.setAuthor("Portal created!", null, e.getGuild().getIconUrl());
                builder.setDescription("@here Created Portal to " + e.getGuild().getName());
                builder.setColor(Colors.COLOR_PRIMARY);
                otherChannel.editMessageById(Main.getMySQL().getGuildValue(openGuilds.get(0), "portal").split(":")[1], builder.build()).queue();
                e.getGuild().getTextChannelsByName("rubicon-portal", true).get(0).editMessageById(searchMessage.getId(), builder.setDescription("@here Created Portal to " + foundGuild.getName()).build()).queue();
                Main.getMySQL().updateGuildValue(e.getGuild(), "portal", "connected:" + foundGuild.getId() + ":" + otherChannel.getId());
                Main.getMySQL().updateGuildValue(foundGuild, "portal", "connected:" + e.getGuild().getId() + ":" + channel.getId());
            } catch (Exception ex) {
                sendErrorMessage("An error occured!");
                Main.getMySQL().updateGuildValue(e.getGuild(), "portal", "closed");
                return;
            }
        }

    }

    private void closePortal() {
        String stat = Main.getMySQL().getGuildValue(e.getGuild(), "portal");
        if (stat.contains("waiting")) {
            Main.getMySQL().updateGuildValue(e.getGuild(), "portal", "closed");
            TextChannel tc = null;
            try {
                tc = e.getGuild().getTextChannelsByName("rubicon-portal", true).get(0);
                tc.delete().queue();
                e.getGuild().getOwner().getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Portal successfully closed!").queue());
            } catch (Exception ex) {

            }
        } else if (stat.contains("connected")) {
            Main.getMySQL().updateGuildValue(e.getGuild(), "portal", "closed");
            TextChannel tcc = null;
            try {
                tcc = e.getGuild().getTextChannelsByName("rubicon-portal", true).get(0);
                tcc.delete().queue();
                e.getGuild().getOwner().getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Portal successfully closed!").queue());
            } catch (Exception ex) {

            }
            Guild otherGuild = e.getJDA().getGuildById(stat.split(":")[1]);
            otherGuild.getOwner().getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Portal was closed from the other owner!").queue());
            Main.getMySQL().updateGuildValue(otherGuild, "portal", "closed");
            TextChannel tc = null;
            try {
                tc = otherGuild.getTextChannelsByName("rubicon-portal", true).get(0);
                tc.delete().queue();
            } catch (Exception ex) {

            }
        }
    }

    @Override
    public String getDescription() {
        return "Talk with users of other guilds.";
    }

    @Override
    public String getUsage() {
        return "portal open\n" +
                "portal close";
    }

    @Override
    public int getPermissionLevel() {
        return 2;
    }
}
