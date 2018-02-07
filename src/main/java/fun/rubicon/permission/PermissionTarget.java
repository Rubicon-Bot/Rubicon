/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.permission;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;

/**
 * Specifies a permission target.
 */
public class PermissionTarget {
    public enum Type {
        USER('u', "user"),
        ROLE('r', "role"),
        DISCORD_PERMISSION('d', "discord permission");

        private final char identifier;

        private final String name;

        Type(char identifier, String name) {
            this.identifier = identifier;
            this.name = name;
        }

        public char getIdentifier() {
            return identifier;
        }

        public String getName() {
            return name;
        }

        public static Type getByIdentifier(char identifier) {
            for (Type type : values())
                if (type.identifier == identifier)
                    return type;
            return null;
        }

    }

    private final Guild guild;
    private final Type type;
    private final long id;

    public PermissionTarget(Guild guild, Type type, long id) {
        this.guild = guild;
        this.type = type;
        this.id = id;
    }

    /**
     * Overloading constructor for creating a user permission target.
     *
     * @param member the target user.
     */
    public PermissionTarget(Member member) {
        this(member.getGuild(), Type.USER, member.getUser().getIdLong());
    }

    /**
     * Overloading constructor for creating a role permission target.
     *
     * @param role the target role.
     */
    public PermissionTarget(Role role) {
        this(role.getGuild(), Type.ROLE, role.getIdLong());
    }

    /**
     * Overloading constructor for creating a discord-permission permission target.
     *
     * @param guild      the guild this permission should apply for.
     * @param permission the target discord-permission.
     */
    public PermissionTarget(Guild guild, Permission permission) {
        this(guild, Type.DISCORD_PERMISSION, permission.getOffset());
    }

    public Guild getGuild() {
        return guild;
    }

    public Type getType() {
        return type;
    }

    public long getId() {
        return id;
    }

    public User getUser() {
        return type == Type.USER ? guild.getJDA().getUserById(id) : null;
    }

    public Role getRole() {
        return type == Type.ROLE ? guild.getRoleById(id) : null;
    }

    public Permission getPermission() {
        return type == Type.DISCORD_PERMISSION ? Permission.getFromOffset((int) id) : null;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof PermissionTarget // object type
                && guild.equals(((PermissionTarget) obj).guild) // guild
                && type == ((PermissionTarget) obj).type // target type
                && id == ((PermissionTarget) obj).id; // id
    }

    public String getName() {
        return type == Type.USER ? getUser().getName() // user name
                : (type == Type.ROLE ? getRole().getName() // or role name
                : getPermission().getName()); // or permission name
    }

    @Override
    public String toString() {
        return (exists() ? getName() : id)
                + " (" + type.getName() + ")"; // and type
    }

    /**
     * Checks whether the target exists in the given context (guild).
     *
     * @return {@code true} if the actual target exists and {@code false} otherwise.
     */
    public boolean exists() {
        switch (type) {
            case USER:
                User user = getUser();
                return user != null && guild.isMember(user);
            case ROLE:
                Role role = getRole();
                return role != null && role.getGuild().equals(guild);
            case DISCORD_PERMISSION:
                return Permission.getFromOffset((int) id) != Permission.UNKNOWN;
        }
        return false;
    }
}
