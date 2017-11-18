package fun.rubicon.core.permission;

import fun.rubicon.command.Command;
import fun.rubicon.core.Main;
import fun.rubicon.util.Info;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;

import java.util.Arrays;

public class PermissionManager {

    private Member member;
    private Command command;

    public PermissionManager(Member member, Command command) {
        this.member = member;
        this.command = command;
    }

    public boolean hasPermission() {
        int lvl = getPermissionLevel();
        int cmdLvl = command.getPermissionLevel();

        if(cmdLvl == 0) {
            return true;
        } else if (cmdLvl == 1) {
            if(getPermissionsAsString().contains(command.getCommand().toLowerCase()))
                return true;
        } else if(cmdLvl == 2) {
            if(member.getPermissions().contains(Permission.ADMINISTRATOR))
                return true;
        } else if(cmdLvl == 3) {
            if(member.isOwner())
                return true;
        } else if(cmdLvl == 4) {
            for(User user : Arrays.asList(Info.BOT_AUTHORS)) {
                if(user.getId().equalsIgnoreCase(member.getUser().getId()))
                    return true;
            }
        }
        return false;
    }

    private String getPermissionsAsString() {
        return Main.getMySQL().getMemberValue(member, "permissions");
    }

    public int getPermissionLevel() {
        String s = Main.getMySQL().getMemberValue(member, "permissionlevel");
        int i = 0;
        try {
            i = Integer.parseInt(s);
        } catch (NumberFormatException ex) {
            Logger.error(ex);
        }
        return i;
    }
}
