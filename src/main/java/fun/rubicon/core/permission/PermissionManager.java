package fun.rubicon.core.permission;

import fun.rubicon.command.Command;
import fun.rubicon.core.Main;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.entities.Member;

public class PermissionManager {

    private Member member;
    private Command command;

    public PermissionManager(Member member, Command command) {
        this.member = member;
        this.command = command;
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
