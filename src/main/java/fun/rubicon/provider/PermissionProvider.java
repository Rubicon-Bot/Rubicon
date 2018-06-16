package fun.rubicon.provider;

import fun.rubicon.RubiconBot;
import fun.rubicon.io.Data;
import fun.rubicon.permission.Permission;
import fun.rubicon.permission.PermissionTarget;
import fun.rubicon.util.DoubleLong;
import fun.rubicon.util.DoubleString;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Leon Kappes / Lee
 * @copyright RubiconBot Dev Team 2018
 * @License GPL-3.0 License <http://rubicon.fun/license>
 */
public class PermissionProvider {

    @Getter
    private static final Map<PermissionTarget, List<Permission>> cache = new HashMap<>();

    public static List<Permission> getPermissionList(PermissionTarget target) {
        //TODO: Replace Line
        assert RubiconBot.getShardManager() != null;
        return cache.containsKey(target) ? cache.get(target) : RubiconBot.getPermissionManager().retrivePermissions(target);
        }


    public static void addPermissions(PermissionTarget target,List<Permission> permissions) {
        if (!cache.containsKey(target))
            cache.put(target,permissions);
    }


}
