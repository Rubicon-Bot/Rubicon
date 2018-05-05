/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.permission;

import java.util.Objects;

/**
 * Represents a permission.
 *
 * @author tr808axm
 */
public class Permission {
    public static final char NEGATION_CHARACTER = '!';

    private final String permissionString;
    private boolean negated;

    /**
     * Constructs a Permission object.
     *
     * @param permissionString the permission node.
     * @param negated          whether the permission is negated.
     */
    public Permission(String permissionString, boolean negated) {
        this.permissionString = Objects.requireNonNull(permissionString);
        this.negated = negated;
    }

    /**
     * @return the permission String.
     */
    public String getPermissionString() {
        return permissionString;
    }

    /**
     * @return whether the permission is negated.
     */
    public boolean isNegated() {
        return negated;
    }

    public Permission setNegated(boolean negated) {
        this.negated = negated;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Permission // object type
                && permissionString.equalsIgnoreCase(((Permission) obj).permissionString) // permission string
                && negated == ((Permission) obj).negated; // negated
    }

    public boolean equalsIgnoreNegation(Object obj) {
        return obj instanceof Permission // object type
                && permissionString.equalsIgnoreCase(((Permission) obj).permissionString); // permission string
    }

    @Override
    public String toString() {
        return negated ? NEGATION_CHARACTER + permissionString : permissionString;
    }

    /**
     * Parses a {@link Permission} object from the serialized form.
     *
     * @param permissionAsString serialized permission {@link String}.
     * @return the {@link Permission} object.
     */
    public static Permission parse(String permissionAsString) {
        return permissionAsString.startsWith(String.valueOf(NEGATION_CHARACTER))
                ? new Permission(permissionAsString.substring(1), true)
                : new Permission(permissionAsString, false);
    }
}