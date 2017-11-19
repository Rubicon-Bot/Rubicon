package fun.rubicon.command;

import fun.rubicon.core.permission.PermissionManager;
import fun.rubicon.util.Colors;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Rubicon Discord bot
 *
 * @author Yannick Seeger / ForYaSee
 * @copyright Rubicon Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.command
 */

public abstract class Command {

    /*
     * Permissions
     * 0 - everyone
     * 1 - With Permissions
     * 2 - Admisitrator
     * 3 - Server Owner
     * 4 - Bot Owner
     */

    protected int defaultDeleteSeconds = 30;

    protected String command;
    protected String[] args;
    protected MessageReceivedEvent e;
    protected CommandCategory category;
    protected PermissionManager permissionManager;

    public Command(String command, CommandCategory category) {
        this.command = command;
        this.category = category;
    }

    public void call(String[] args, MessageReceivedEvent e) {
        this.args = args;
        this.e = e;
        this.permissionManager = new PermissionManager(e.getMember(), this);
        if(permissionManager.hasPermission()) {
            execute(args, e);
        } else {
            sendNoPermissionMessage();
        }
        try {
            e.getMessage().delete().queue();
        } catch (Exception ex) {

        }
    }

    protected void sendEmbededMessage(String message) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setDescription(message);
        builder.setColor(Colors.COLOR_PRIMARY);
        e.getTextChannel().sendMessage(builder.build()).queue(msg -> msg.delete().queueAfter(defaultDeleteSeconds, TimeUnit.SECONDS));
    }

    protected void sendEmbededMessage(TextChannel ch, String title, Color color, String message) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setAuthor(title, null, e.getJDA().getSelfUser().getEffectiveAvatarUrl());
        builder.setDescription(message);
        builder.setColor(color);
        ch.sendMessage(builder.build()).queue(msg -> msg.delete().queueAfter(defaultDeleteSeconds, TimeUnit.SECONDS));
    }
    protected void sendEmbededMessage(PrivateChannel pc, String title, Color color, String message) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setAuthor(title, null, e.getJDA().getSelfUser().getEffectiveAvatarUrl());
        builder.setDescription(message);
        builder.setColor(color);
        pc.sendMessage(builder.build()).queue();
    }


    protected void sendErrorMessage(String message) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setAuthor("Error", null, e.getJDA().getSelfUser().getEffectiveAvatarUrl());
        builder.setDescription(message);
        builder.setColor(Colors.COLOR_ERROR);
        builder.setFooter(generateTimeStamp(), null);
        e.getTextChannel().sendMessage(builder.build()).queue(msg -> msg.delete().queueAfter(defaultDeleteSeconds, TimeUnit.SECONDS));
    }

    protected void sendNoPermissionMessage() {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setAuthor("No Permissions", null, e.getJDA().getSelfUser().getEffectiveAvatarUrl());
        builder.setDescription("You don't have permissions to do this!");
        builder.setColor(Colors.COLOR_NO_PERMISSION);
        builder.setFooter(generateTimeStamp(), null);
        e.getTextChannel().sendMessage(builder.build()).queue(msg -> msg.delete().queueAfter(defaultDeleteSeconds, TimeUnit.SECONDS));
    }

    protected void sendUsageMessage() {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setAuthor("Usage of " + getCommand(), null, e.getJDA().getSelfUser().getEffectiveAvatarUrl());
        builder.setDescription(getUsage());
        builder.setColor(Colors.COLOR_SECONDARY);
        builder.setFooter(generateTimeStamp(), null);
        e.getTextChannel().sendMessage(builder.build()).queue(msg -> msg.delete().queueAfter(defaultDeleteSeconds, TimeUnit.SECONDS));
    }

    protected void sendNotImplementedMessage() {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setAuthor("Command ", null, e.getJDA().getSelfUser().getEffectiveAvatarUrl());
        builder.setDescription(getUsage());
        builder.setColor(Colors.COLOR_NOT_IMPLEMENTED);
        builder.setFooter(generateTimeStamp(), null);
        e.getTextChannel().sendMessage(builder.build()).queue(msg -> msg.delete().queueAfter(defaultDeleteSeconds, TimeUnit.SECONDS));
    }

    public String getCommand() {
        return command;
    }

    protected abstract void execute(String[] args, MessageReceivedEvent e);

    public CommandCategory getCategory() {
        return category;
    }


    public abstract String getDescription();

    public abstract String getUsage();

    public abstract int getPermissionLevel();

    //Stuff
    private String generateTimeStamp() {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        return simpleDateFormat.format(date);
    }
}

