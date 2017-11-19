package fun.rubicon.commands.admin;

import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.core.permission.PermissionManager;
import fun.rubicon.util.Colors;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.concurrent.TimeUnit;

/**
 * Rubicon Discord bot
 *
 * @author Yannick Seeger / ForYaSee
 * @copyright Rubicon Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.commands.admin
 */

public class CommandPermission extends Command {

    public CommandPermission(String command, CommandCategory category) {
        super(command, category);
    }

    @Override
    protected void execute(String[] args, MessageReceivedEvent e) {
        Role role = null;
        Member member = null;
        if(e.getMessage().getMentionedUsers().size() == 1) {
            member = e.getGuild().getMember(e.getMessage().getMentionedUsers().get(0));
        }
        if(e.getMessage().getMentionedRoles().size() == 1) {
            role = e.getMessage().getMentionedRoles().get(0);
        }

        if(member == null && role == null) {
            sendErrorMessage("You have to mention one user or role!");
            return;
        }
        if(args.length < 2) {
            sendErrorMessage("You have to use more arguments!");
            return;
        }

        if(member != null) {
            int nameLength = member.getEffectiveName().split(" ").length;
            String operator = args[nameLength].toLowerCase();
            String command = null;
            try { command = args[nameLength + 1]; } catch (ArrayIndexOutOfBoundsException ex) {}

            switch (operator) {
                case "add":
                    break;
                case "remove":
                    break;
                case "list":
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setAuthor(member.getEffectiveName() + "'s Commands", null,member.getUser().getEffectiveAvatarUrl());
                    builder.setDescription(generatePermissionList(member));
                    builder.setColor(Colors.COLOR_NOT_IMPLEMENTED);
                    e.getTextChannel().sendMessage(builder.build()).queue(msg -> msg.delete().queueAfter(defaultDeleteSeconds, TimeUnit.SECONDS));
                    break;
            }
        }
    }

    private String generatePermissionList(Member member) {
        PermissionManager perm = new PermissionManager(member, this);
        String allPermissions = perm.getPermissionsAsString();
        String[] arr = allPermissions.split(",");
        String res = "";
        for(int i = 0; i < arr.length; i++) {
            res += ":small_blue_diamond: **" + arr[i] + "**\n";
        }
        return res;
    }

    @Override
    public String getDescription() {
        return "Manages permissions of a user or a role.";
    }

    @Override
    public String getUsage() {
        return "permission <@User/@Role> <add/remove> <command>\n" +
                "permission <@User/@Role> <list>";
    }

    @Override
    public int getPermissionLevel() {
        return 2;
    }
}
