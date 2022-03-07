package de.cubenation.bedrock.bungee.plugin.event;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.md_5.bungee.api.plugin.Event;

import java.util.UUID;

/**
 * @author Cube-Nation
 * @version 2.0
 */
@ToString
public class PlayerChangeNameEvent extends Event {

    @Getter @Setter
    private UUID uuid;

    @Getter @Setter
    private String oldName;

    @Getter @Setter
    private String newName;

    public PlayerChangeNameEvent(UUID uuid, String oldName, String newName) {
        this.uuid = uuid;
        this.oldName = oldName;
        this.newName = newName;
    }
}
