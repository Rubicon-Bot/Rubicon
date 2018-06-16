package fun.rubicon.entities.impl;

import fun.rubicon.entities.PortalSettings;
import fun.rubicon.io.db.RethinkDataset;
import lombok.Getter;

/**
 * @author Leon Kappes / Lee
 * @copyright RubiconBot Dev Team 2018
 * @License GPL-3.0 License <http://rubicon.fun/license>
 */
public class PortalSettingsImpl extends RethinkDataset implements PortalSettings {

    public static transient String TABLE = "portal_settings";

    @Getter
    private String id;
    private boolean invites = true;
    private boolean embeds;

    public PortalSettingsImpl() {
        super(TABLE);
    }

    public PortalSettingsImpl(String guildId, boolean invitesEnabled, boolean embedsEnabled){
        super(TABLE);
        this.id = guildId;
        this.invites = invitesEnabled;
        this.embeds = embedsEnabled;
        saveData();
    }

    @Override
    public void setInvites(boolean state) {
        invites = state;
        saveData();
    }

    @Override
    public void setEmbeds(boolean state) {
        embeds = state;
        saveData();
    }

    @Override
    public boolean getInvites() {
        return invites;
    }

    @Override
    public boolean getEmbeds() {
        return embeds;
    }

    @Override
    public void delete() {
        deleteData();
    }

}
