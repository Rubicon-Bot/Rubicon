package fun.rubicon.core.permission;

import fun.rubicon.command.Command;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.core.Main;
import fun.rubicon.util.Info;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;

import java.util.*;

/**
 * Rubicon Discord bot
 *
 * @author Yannick Seeger / ForYaSee
 * @copyright Rubicon Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.core.permission
 */

public class PermissionManager {

    private Member member;
    private Command command;

    public PermissionManager(Member member, Command command) {
        this.member = member;
        this.command = command;
    }

    public boolean hasPermission() {
        try {
            int lvl = getPermissionLevel();
            int cmdLvl = command.getPermissionLevel();
            for (User user : Arrays.asList(Info.BOT_AUTHORS)) {
                if (user.getId().equalsIgnoreCase(member.getUser().getId())) {
                    return true;
                }
            }
            if (getPermissionLevel() >= cmdLvl) {
                return true;
            }
            if(containsPermission(command.getCommand())) {
                return true;
            }

            if (cmdLvl == 0) {
                return true;
            } else if (cmdLvl == 1) {
                if (getPermissionsAsString().contains(command.getCommand().toLowerCase()))
                    return true;
            } else if (cmdLvl == 2) {
                if (member.getPermissions().contains(Permission.ADMINISTRATOR))
                    return true;
            } else if (cmdLvl == 3) {
                if (member.isOwner())
                    return true;
            }
        } catch (NullPointerException ignored) {

        }
        return false;
    }

    public String getPermissionsAsString() {
        return Main.getMySQL().getMemberValue(member, "permissions");
    }

    public int getPermissionLevel() {
        String s = Main.getMySQL().getMemberValue(member, "permissionlevel");
        int i;
        try {
            i = Integer.parseInt(s);
        } catch (NumberFormatException ex) {
            Logger.error(ex);
            return 0;
        }
        return i;
    }

    public String getAllAllowedCommands() {
        List<Command> allCommands = new ArrayList<>(CommandHandler.getCommands().values());
        String res = "";
        for(Command cmd : allCommands) {
            PermissionManager p = new PermissionManager(member, cmd);
            if(p.hasPermission()) {
                res += cmd.getCommand() + ",";
            }
        }
        return res;
    }

    public void addPermissions(String command) {
        String s = getPermissionsAsString();
        s += command.toLowerCase() + ",";
        Main.getMySQL().updateMemberValue(member, "permissions", s);
    }

    public void removePermission(String command) {
        String s = getPermissionsAsString();
        s = s.replace(command + ",", "");
        Main.getMySQL().updateMemberValue(member, "permissions", s);
    }

    public boolean containsPermission(String command) {
        return getPermissionsAsString().contains(command);
    }
}
