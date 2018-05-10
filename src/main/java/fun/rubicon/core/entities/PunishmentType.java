package fun.rubicon.core.entities;

public enum PunishmentType {

    MUTE("Mute"),
    BAN("Ban"),
    KICK("Kick");

    private String name;

    PunishmentType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
