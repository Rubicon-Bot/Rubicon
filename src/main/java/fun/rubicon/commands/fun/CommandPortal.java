package fun.rubicon.commands.fun;

import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.core.Main;
import fun.rubicon.util.Info;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Category;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.text.ParseException;

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
        if(args.length == 0) { sendUsageMessage(); return;}

        switch (args[0]) {
            case "open":
            case "o":
            case "create":
                if(Main.getMySQL().getGuildValue(e.getGuild(), "portal").equals("closed")) {
                    if(args.length == 1) {
                        //Search Portal
                        createOwnPortal();
                        Category cat = e.getGuild().getCategoriesByName(Info.BOT_NAME, true).get(0);
                        if(cat == null) {
                            sendErrorMessage("You deleted or renamed the rubicon category! Please use " + Main.getMySQL().getGuildValue(e.getGuild(), "prefix") + "rebuild");
                            return;
                        }
                        try {
                            e.getGuild().getController().createTextChannel("rubicon-portal").setParent(e.getGuild().getCategoriesByName(Info.BOT_NAME, true).get(0)).complete();
                        } catch (Exception ex) {
                            sendErrorMessage("An error occured!");
                            return;
                        }
                        TextChannel channel = e.getGuild().getTextChannelsByName("rubicon-portal", false).get(0);
                        EmbedBuilder builder = new EmbedBuilder();
                        builder.setAuthor(e.getGuild().getName() + "'s portal opened", null, e.getGuild().getIconUrl());
                        builder.setDescription("Searching other open portal...");
                        channel.sendMessage(builder.build()).queue();
                    } else if(args.length == 2) {
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
                break;
        }
    }

    private void createOwnPortal() {
        Main.getMySQL().updateGuildValue(e.getGuild(), "portal", "open");
    }

    @Override
    public String getDescription() {
        return "Talk with users of other guilds.";
    }

    @Override
    public String getUsage() {
        return "portal open [guildid]\n" +
                "portal close";
    }

    @Override
    public int getPermissionLevel() {
        return 2;
    }
}
