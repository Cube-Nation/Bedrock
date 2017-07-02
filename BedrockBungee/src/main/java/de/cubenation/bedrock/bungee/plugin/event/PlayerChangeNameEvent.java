package de.cubenation.bedrock.bungee.plugin.event;

import net.md_5.bungee.api.plugin.Event;

import java.util.UUID;

/**
 * @author Cube-Nation
 * @version 1.0
 */
public class PlayerChangeNameEvent extends Event {

    private UUID uuid;

    private String oldName;

    private String newName;

    public PlayerChangeNameEvent(UUID uuid, String oldName, String newName) {
        this.uuid = uuid;
        this.oldName = oldName;
        this.newName = newName;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getOldName() {
        return oldName;
    }

    public void setOldName(String oldName) {
        this.oldName = oldName;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }
}
